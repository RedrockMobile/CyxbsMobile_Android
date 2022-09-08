package com.mredrock.cyxbs.common.network

import android.os.Handler
import android.util.SparseArray
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserStateService
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.bean.BackupUrlStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.service.impl
import com.mredrock.cyxbs.common.utils.LogLocal
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.locks.ReentrantLock


/**
 * 即将被废弃的 lib_common 模块中的网络请求工具类
 *
 * 网络请求的示例代码请看 lib_utils 模块中的 ApiGenerator
 *
 * Created by AceMurder on 2018/1/24.
 */
object ApiGenerator {
    private const val DEFAULT_TIME_OUT = 5

    private var retrofit: Retrofit //统一添加了token到header
    private var commonRetrofit: Retrofit // 未添加token到header

    @Volatile
    private var token = ""
    private val retrofitMap by lazy { SparseArray<Retrofit>() }

    //init对两种公共的retrofit进行配置
    init {
        //添加监听得到登录后的token和refreshToken,应用于初次登录或重新登录
        val accountService = ServiceManager.getService(IAccountService::class.java)
        accountService.getVerifyService().addOnStateChangedListener {
            when (it) {
                IUserStateService.UserState.LOGIN, IUserStateService.UserState.REFRESH -> {
                    token = accountService.getUserTokenService().getToken()
                }
                else -> {
                    //不用操作
                }
            }
        }
        token = accountService.getUserTokenService().getToken()
        LogUtils.d("tokenTag", "token = $token")
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
     * 通过此方法对得到单独的 Retrofit
     * @param retrofitConfig 配置Retrofit.Builder，已配置有
     * @see GsonConverterFactory
     * @see RxJava3CallAdapterFactory
     * null-> 默认BaseUrl
     * @param okHttpClientConfig 配置OkHttpClient.Builder，已配置有
     * @see HttpLoggingInterceptor
     * null-> 默认Timeout
     * @param tokenNeeded 是否需要添加token请求
     */
    fun createSelfRetrofit(retrofitConfig: ((Retrofit.Builder) -> Retrofit.Builder)? = null, okHttpClientConfig: ((OkHttpClient.Builder) -> OkHttpClient.Builder)? = null, tokenNeeded: Boolean): Retrofit {
        return Retrofit.Builder()
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
                            okHttpClientConfig.invoke(
                                it.addInterceptor(BackupInterceptor)
                            )
                    }.build()
                }.build()
    }

    //以下是retrofit基本配置
    /**
     * 现目前必须配置基本的需求，比如Log，Gson，RxJava
     */
    private fun Retrofit.Builder.configRetrofitBuilder(client: ((OkHttpClient.Builder) -> OkHttpClient)): Retrofit.Builder {
        return this.client(client.invoke(OkHttpClient().newBuilder().apply {
                val logging = HttpLoggingInterceptor { message ->
                    LogUtils.d("OKHTTP",message)
                    LogLocal.log("OKHTTP", "OKHTTP$message")
                }
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(Interceptor {
                    it.proceed(
                        it.request().newBuilder()
                            .addHeader("APPVersion", BaseApp.version)
                            .build()
                    )
                })
                addInterceptor(logging)
                //这里是在debug模式下方便开发人员简单确认 http 错误码 和 url(magipoke开始切的)
                if (BuildConfig.DEBUG){
                    addInterceptor(Interceptor{
                        val request = it.request()
                        Log.d("OKHTTP","OKHTTP${request.body}")

                        val response = it.proceed(request)
                        // 因为部分请求一直 403、404，一直不修，就直接不弹了，所以注释掉，以后直接看 Pandora
//                        if (!response.isSuccessful){
//                            Handler(Looper.getMainLooper()).post {
//                                BaseApp.appContext.toast("${response.code} ${request.url} ")
//                            }
//                        }
                        response
                    })
                }
        }))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
    }

    //默认配置
    private fun Retrofit.Builder.defaultConfig() {
        this.baseUrl(getBaseUrl())
    }

    //默认配置
    private fun OkHttpClient.Builder.defaultConfig() {
        this.connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
        this.readTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
    }


    //不带token请求的OkHttp配置
    private fun OkHttpClient.Builder.configureCommonOkHttp(): OkHttpClient {
        return this.apply {
            /**
             * 发送版本号
             */

            /**
             * 连接失败时切换备用url的Interceptor
             * 一旦切换，只有重启app才能切回来（因为如果请求得到的url不是原来的@{link getBaseUrl()}，则切换到新的url，而以后访问都用这个新的url了）
             * 放在tokenInterceptor上游的理由是：因为那里面还有token刷新机制，无法判断是否真正是因为服务器的原因请求失败
             */
            interceptors().add(BackupInterceptor)
        }.build()
    }

    //带token请求的OkHttp配置
    private fun OkHttpClient.Builder.configureTokenOkHttp(): OkHttpClient {
        return this.apply {
            /**
             * 发送版本号
             */
            interceptors().add(Interceptor {
                it.proceed(
                    it.request()
                        .newBuilder()
                        .addHeader("version", BaseApp.version)
                        .build()
                )
            })
            /**
             * 连接失败时切换备用url的Interceptor
             * 一旦切换，只有重启app才能切回来（因为如果请求得到的url不是原来的@{link getBaseUrl()}，则切换到新的url，而以后访问都用这个新的url了）
             * 放在tokenInterceptor上游的理由是：因为那里面还有token刷新机制，无法判断是否真正是因为服务器的原因请求失败
             */
//            interceptors().add(BackupInterceptor())


            interceptors().add(Interceptor {
                
                val accountService = IAccountService::class.impl
                if (!accountService.getVerifyService().isLogin()) {
                    return@Interceptor it.proceed(it.request())
                }
                
                /**
                 * 所有请求添加token到header
                 * 在外面加一层判断，用于token未过期时，能够异步请求，不用阻塞在checkRefresh()
                 * 如果有更好方式再改改
                 */
                when {
                    token.isEmpty() -> {
                        token = accountService.getUserTokenService().getToken()
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
                        proceedPoxyWithTryCatch { it.proceed(it.request().newBuilder().header("Authorization", "Bearer $token").build()) }
                    }
                } as Response
            })
        }.build()
    }
    
    private val mReentrantLock = ReentrantLock()
    
    //对token进行刷新
    private fun checkRefresh(chain: Interceptor.Chain, expiredToken: String): Response? {
        
        mReentrantLock.lock()
        
        /**
         * 刷新token条件设置为，已有refreshToken，并且已经过期，也可以后端返回特定到token失效code
         * 当第一个过期token请求接口后，改变token和refreshToken，防止同步refreshToken失效
         * 之后进入该方法的请求，token已经刷新
         * 2021-04版本中由于添加了自http状态码而来的token过期检测
         * 可能会出现多个接口因为token过期导致同时（虽然是顺序执行）的情况
         * 故要求在刷新时传递过期token过来，如果该过期token已经被刷新，就直接配置新token，不再刷新
         */
        if (expiredToken != token) {//认定已经刷新成功，直接返回新的请求
            mReentrantLock.unlock()
            return proceedPoxyWithTryCatch { chain.run { proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build()) } }
        }
        try {
            token = ServiceManager.getService(IAccountService::class.java).getVerifyService().refresh() ?: return null
            return proceedPoxyWithTryCatch { chain.run { proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build()) } }
        } finally {
            mReentrantLock.unlock()
        }
    }
    
    private var mLastToastTime = 0L

    private fun proceedPoxyWithTryCatch(proceed: () -> Response): Response? {
        // 以前学长在这里使用了 try catch，会导致 Pandora 看到的问题全是空指针
        // 所以修改为直接调用，按正常逻辑，如果出现网络连接问题就会抛异常，然后 Pandora 可以看到异常
        // 因为不想看到一堆报黄，所以该方法仍返回 Response?，暂不打算去掉 ?
        try {
            return proceed.invoke()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                val nowTime = System.currentTimeMillis()
                if (nowTime - mLastToastTime > 10 * 1000) { // 保证不会一直疯狂 toast
                    mLastToastTime = nowTime
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(BaseApp.appContext, "网络请求异常，请查看 Pandora", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            throw e
        }
    }



    object BackupInterceptor : Interceptor {

        @Volatile
        private var useBackupUrl: Boolean = false
        private var backupUrl: String = getBaseUrlWithoutHttps()
        
        private var mLastToastTime = 0L

        override fun intercept(chain: Interceptor.Chain): Response {

            // 如果切换过url，则直接用这个url请求
            if (useBackupUrl) {
                return useBackupUrl(chain)
            }

            // 正常请求，照理说应该进入tokenInterceptor
            // 除了登录和部分接口使用的 CommonApiService 以外，他们不会跑进 tokenInterceptor
            var response: Response? = null
            val exception: Exception
            try {
                response = chain.proceed(chain.request())
                return response // 这里不能检查 code，因为部分老接口会返回 http 状态码 500
            } catch (e: Exception) {
                exception = e
            }
            
            // 分不同的环境触发不同的容灾请求
            when (getBaseUrl()) {
                END_POINT_REDROCK_DEV -> {
                    // dev 环境不触发容灾，不然会导致测试接口 404
                    val nowTime = System.currentTimeMillis()
                    if (nowTime - mLastToastTime > 10 * 1000) { // 保证不会一直疯狂 toast
                        mLastToastTime = nowTime
                        Handler(Looper.getMainLooper()).post {
                            // 使用原生 toast 醒目一点
                            Toast.makeText(
                                BaseApp.appContext,
                                "dev 请求异常, 请查看 Pandora",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                END_POINT_REDROCK_PROD -> {
                    backupUrl = getBackupUrl()
                    if ("https://$backupUrl" != END_POINT_REDROCK_PROD) {
                        useBackupUrl = true
                        response?.close()
                        return useBackupUrl(chain) // 这里面使用新的 response
                    }
                }
                else -> throw RuntimeException("未知请求头！")
            }

            if (response == null) {
                // 这里抛出异常可以被 Pandora 捕获
                // 只有在没有触发容灾时会跑到这一步
                throw exception
            }

            return response
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
                if (backupUrlStatus.data.baseUrl.isNullOrEmpty()) {
                    getBaseUrlWithoutHttps()
                }else{
                    backupUrlStatus.data.baseUrl
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getBaseUrlWithoutHttps()
            }
        }


    }
    //获得没有 https://的地址提供给容灾使用
    private fun getBaseUrlWithoutHttps() = getBaseUrl().subSequence(8, getBaseUrl().length).toString()

    //是否是游客模式
    private fun isTouristMode() = ServiceManager.getService(IAccountService::class.java).getVerifyService().isTouristMode()

    //检查token是否过期
    private fun isTokenExpired() = ServiceManager.getService(IAccountService::class.java).getVerifyService().isExpired()
}