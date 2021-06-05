package com.mredrock.cyxbs.common.network

import android.util.SparseArray
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserStateService
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.bean.BackupUrlStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.config.BASE_NORMAL_BACKUP_GET
import com.mredrock.cyxbs.common.config.SUCCESS
import com.mredrock.cyxbs.common.config.TOKEN_EXPIRE
import com.mredrock.cyxbs.common.config.getBaseUrl
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.takeIfNoException
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by AceMurder on 2018/1/24.
 */
object ApiGenerator {
    private const val DEFAULT_TIME_OUT = 30

    private var retrofit: Retrofit //统一添加了token到header
    private var commonRetrofit: Retrofit // 未添加token到header

    private var token = ""
    private var refreshToken = ""
    private val retrofitMap by lazy { SparseArray<Retrofit>() }

    //是否正在刷新Token
    private var lastExpiredToken = ""

    //init对两种公共的retrofit进行配置
    init {
        //添加监听得到登录后的token和refreshToken,应用于初次登录或重新登录
        val accountService = ServiceManager.getService(IAccountService::class.java)
        accountService.getVerifyService().addOnStateChangedListener {
            when (it) {
                IUserStateService.UserState.LOGIN, IUserStateService.UserState.REFRESH -> {
                    token = accountService.getUserTokenService().getToken()
                    refreshToken = accountService.getUserTokenService().getRefreshToken()
                }
                else -> {
                    //不用操作
                }
            }
        }
        token = accountService.getUserTokenService().getToken()
        refreshToken = accountService.getUserTokenService().getRefreshToken()
        LogUtils.d("tokenTag", "token = $token")
        LogUtils.d("tokenTag", "refresh token = $refreshToken")
        retrofit = Retrofit.Builder().apply {
            this.defaultConfig()
            configRetrofitBuilder {
                it.apply {
                    defaultConfig()
                    configureTokenOkHttp()
                }.build()
            }
        }.build()
        commonRetrofit = Retrofit.Builder().apply {
            this.defaultConfig()
            configRetrofitBuilder {
                it.apply {
                    defaultConfig()
                    configureCommonOkHttp()
                }.build()
            }
        }.build()
    }

    fun <T> getApiService(clazz: Class<T>) = if (isTouristMode()) {
        getCommonApiService(clazz)
    } else {
        retrofit.create(clazz)
    }

    fun <T> getApiService(retrofit: Retrofit, clazz: Class<T>) = retrofit.create(clazz)

    fun <T> getCommonApiService(clazz: Class<T>) = commonRetrofit.create(clazz)

    /**
     *这个方法提供对OkHttp和Retrofit进行自定义的操作，通过uniqueNum可以实现不同子模块中的复用，而不需要在通用模块中添加。
     *默认会完成部分基础设置，此处传入的两个lambda在基础设置之后执行，可以覆盖基础设置。
     * 需要先进行注册才能使用。
     *@throws IllegalAccessException
     */
    fun <T> getApiService(uniqueNum: Int, clazz: Class<T>): T {

        if (retrofitMap[uniqueNum] == null) {
            throw IllegalArgumentException()
        }
        return retrofitMap[uniqueNum]!!.create(clazz)
    }

    /**
     * 通过此方法对配置进行注册，之后即可使用uniqueNum获取service。
     * @param uniqueNum retrofit标识符
     * @param retrofitConfig 配置Retrofit.Builder，已配置有
     * @see GsonConverterFactory
     * @see RxJava2CallAdapterFactory
     * null-> 默认BaseUrl
     * @param okHttpClientConfig 配置OkHttpClient.Builder，已配置有
     * @see HttpLoggingInterceptor
     * null-> 默认Timeout
     * @param tokenNeeded 是否需要添加token请求
     */
    fun registerNetSettings(uniqueNum: Int, retrofitConfig: ((Retrofit.Builder) -> Retrofit.Builder)? = null, okHttpClientConfig: ((OkHttpClient.Builder) -> OkHttpClient.Builder)? = null, tokenNeeded: Boolean) {
        retrofitMap.put(uniqueNum, Retrofit.Builder()
                //对传入的retrofitConfig配置
                .apply {
                    if (retrofitConfig == null)
                        this.defaultConfig()
                    else
                        retrofitConfig.invoke(this)
                }
                //对传入的okHttpClientConfig配置
                .configRetrofitBuilder {
                    it.apply {
                        if (tokenNeeded && !isTouristMode())
                            configureTokenOkHttp()
                        if (okHttpClientConfig == null)
                            this.defaultConfig()
                        else
                            okHttpClientConfig.invoke(it)
                    }.build()
                }.build())
    }

    //以下是retrofit基本配置
    /**
     * 现目前必须配置基本的需求，比如Log，Gson，RxJava
     */
    private fun Retrofit.Builder.configRetrofitBuilder(client: ((OkHttpClient.Builder) -> OkHttpClient)): Retrofit.Builder {
        return this.client(client.invoke(OkHttpClient().newBuilder().apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
        }))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    //默认配置
    private fun Retrofit.Builder.defaultConfig() {
        this.baseUrl(getBaseUrl())
    }

    //默认配置
    private fun OkHttpClient.Builder.defaultConfig() {
        this.connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
    }


    //不带token请求的OkHttp配置
    private fun OkHttpClient.Builder.configureCommonOkHttp(): OkHttpClient {
        return this.apply {
            /**
             * 连接失败时切换备用url的Interceptor
             * 一旦切换，只有重启app才能切回来（因为如果请求得到的url不是原来的@{link getBaseUrl()}，则切换到新的url，而以后访问都用这个新的url了）
             * 放在tokenInterceptor上游的理由是：因为那里面还有token刷新机制，无法判断是否真正是因为服务器的原因请求失败
             */
            interceptors().add(BackupInterceptor())
        }.build()
    }

    //带token请求的OkHttp配置
    private fun OkHttpClient.Builder.configureTokenOkHttp(): OkHttpClient {
        return this.apply {
            /**
             * 连接失败时切换备用url的Interceptor
             * 一旦切换，只有重启app才能切回来（因为如果请求得到的url不是原来的@{link getBaseUrl()}，则切换到新的url，而以后访问都用这个新的url了）
             * 放在tokenInterceptor上游的理由是：因为那里面还有token刷新机制，无法判断是否真正是因为服务器的原因请求失败
             */
            interceptors().add(BackupInterceptor())


            interceptors().add(Interceptor {
                /**
                 * 所有请求添加token到header
                 * 在外面加一层判断，用于token未过期时，能够异步请求，不用阻塞在checkRefresh()
                 * 如果有更好方式再改改
                 */
                when {
                    refreshToken.isEmpty() || token.isEmpty() -> {
                        token = ServiceManager.getService(IAccountService::class.java).getUserTokenService().getToken()
                        refreshToken = ServiceManager.getService(IAccountService::class.java).getUserTokenService().getRefreshToken()
                        if (isTokenExpired()) {
                            checkRefresh(it, token)
                        } else {
                            proceedPoxyWithTryCatch {
                                it.proceed(it.request().newBuilder().header("Authorization", "Bearer $token").build())
                            }
                        }
                    }
                    isTokenExpired() -> {
                        checkRefresh(it, token)
                    }
                    else -> {
                        val response = proceedPoxyWithTryCatch { it.proceed(it.request().newBuilder().header("Authorization", "Bearer $token").build()) }
                        //此处拦截http状态码进行统一处理
                        response?.apply {
                            when (code) {
                                TOKEN_EXPIRE -> {
                                    response.close()
                                    checkRefresh(it, token)
                                }
                                SUCCESS -> {
                                    return@Interceptor this
                                }
                                else -> {
                                    return@Interceptor this
                                }
                            }
                        }
                    }
                } as Response
            })
        }.build()
    }

    //对token和refreshToken进行刷新
    @Synchronized
    private fun checkRefresh(chain: Interceptor.Chain, expiredToken: String): Response? {

        var response = proceedPoxyWithTryCatch { chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build()) }
        /**
         * 刷新token条件设置为，已有refreshToken，并且已经过期，也可以后端返回特定到token失效code
         * 当第一个过期token请求接口后，改变token和refreshToken，防止同步refreshToken失效
         * 之后进入该方法的请求，token已经刷新
         * 2021-04版本中由于添加了自http状态码而来的token过期检测
         * 可能会出现多个接口因为token过期导致同时（虽然是顺序执行）的情况
         * 故要求在刷新时传递过期token过来，如果该过期token已经被刷新，就直接配置新token，不再刷新
         */
        if (lastExpiredToken == expiredToken) {//认定已经刷新成功，直接返回新的请求
            response?.close()
            return proceedPoxyWithTryCatch { chain.run { proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build()) } }
        }
        lastExpiredToken = expiredToken
        if (refreshToken.isNotEmpty() && (isTokenExpired() || response?.code == 403)) {
            takeIfNoException {
                ServiceManager.getService(IAccountService::class.java).getVerifyService().refresh(
                        onError = {
                            response?.close()
                        },
                        action = { s: String ->
                            response?.close()
//                            BaseApp.context.toast("用户认证刷新成功")
                            response = proceedPoxyWithTryCatch { chain.run { proceed(chain.request().newBuilder().header("Authorization", "Bearer $s").build()) } }
                        }
                )
            }

        }
        return response
    }

    private fun proceedPoxyWithTryCatch(proceed: () -> Response): Response? {
        var response: Response? = null
        try {
            response = proceed.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return response
        }
    }

    //检查token是否过期
    private fun isTokenExpired() = ServiceManager.getService(IAccountService::class.java).getVerifyService().isExpired()

    //是否是游客模式
    private fun isTouristMode() = ServiceManager.getService(IAccountService::class.java).getVerifyService().isTouristMode()

    class BackupInterceptor : Interceptor {

        private var useBackupUrl: Boolean = false
        private var backupUrl: String = ""

        override fun intercept(chain: Interceptor.Chain): Response {

            // 如果切换过url，则直接用这个url请求
            if (useBackupUrl) {
                return useBackupUrl(chain)
            }

            // 正常请求，照理说应该进入tokenInterceptor
            val response = proceedPoxyWithTryCatch {
                chain.proceed(chain.request())
            }

            if (response?.isSuccessful == true) {
                return response
            }


            // 如果请求失败（是tokenInterceptor即便刷新token也无法请求成功的情况），则请求是否有新的url
            backupUrl = getBackupUrl()
            // 约定给的backupUrl不带https前缀，加上
            if ("https://$backupUrl" != getBaseUrl()) {
                useBackupUrl = true
                // 重新请求并返回
                return useBackupUrl(chain)
            }


            return response!!
        }

        private fun useBackupUrl(chain: Interceptor.Chain): Response {
            val newUrl: HttpUrl = chain.request().url
                    .newBuilder()
                    .scheme("https")
                    .host(backupUrl)
                    .build()
            val builder: Request.Builder = chain.request().newBuilder()
            return chain.proceed(builder.url(newUrl).build())
        }

        private fun getBackupUrl(): String {
            val okHttpClient = OkHttpClient()
            val request: Request = Request.Builder()
                    .url(BASE_NORMAL_BACKUP_GET)
                    .build()
            val call = okHttpClient.newCall(request)
            val json = call.execute().body?.string()
            return try {
                val backupUrlStatus = Gson().fromJson<RedrockApiWrapper<BackupUrlStatus>>(json, object : TypeToken<RedrockApiWrapper<BackupUrlStatus>>() {}.type)
                backupUrlStatus.data.baseUrl
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }


    }

}