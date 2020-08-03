package com.mredrock.cyxbs.common.network

import android.util.SparseArray
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserStateService
import com.mredrock.cyxbs.common.utils.extensions.takeIfNoException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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

    //init对两种公共的retrofit进行配置
    init {
        //添加监听得到登录后的token和refreshToken,应用于初次登录或重新登录
        val accountService = ServiceManager.getService(IAccountService::class.java)
        accountService.getVerifyService().addOnStateChangedListener {
            when (it) {
                IUserStateService.UserState.LOGIN -> {
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
        retrofit = Retrofit.Builder().apply {
            this.defaultConfig()
            configRetrofitBuilder {
                it.defaultConfig()
                it.configureOkHttp()
            }
        }.build()
        commonRetrofit = Retrofit.Builder().apply {
            this.defaultConfig()
            configRetrofitBuilder {
                it.apply {
                    defaultConfig()
                }.build()
            }
        }.build()
    }

    fun <T> getApiService(clazz: Class<T>) = retrofit.create(clazz)

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
                        if (tokenNeeded)
                            configureOkHttp()
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
        this.baseUrl(END_POINT_REDROCK)
    }

    //默认配置
    private fun OkHttpClient.Builder.defaultConfig() {
        this.connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
    }

    //带token请求的OkHttp配置
    private fun OkHttpClient.Builder.configureOkHttp(): OkHttpClient {
        return this.apply {
            interceptors().add(Interceptor {
                /**
                 * 所有请求添加token到header
                 * 在外面加一层判断，用于token未过期时，能够异步请求，不用阻塞在checkRefresh()
                 * 如果有更好方式再改改
                 */
                when {
                    refreshToken.isEmpty() || token.isEmpty() -> {
                        token = ServiceManager.getService(IAccountService::class.java).getUserTokenService()?.getToken()
                        refreshToken = ServiceManager.getService(IAccountService::class.java).getUserTokenService()?.getRefreshToken()
                        if (isTokenExpired()) {
                            checkRefresh(it)
                        } else {
                            it.proceed(it.request().newBuilder().header("Authorization", "Bearer $token").build())
                        }
                    }
                    isTokenExpired() -> {
                        checkRefresh(it)
                    }
                    else -> {
                        it.proceed(it.request().newBuilder().header("Authorization", "Bearer $token").build())
                    }
                } as Response
            })
        }.build()
    }

    //对token和refreshToken进行刷新
    @Synchronized
    private fun checkRefresh(chain: Interceptor.Chain): Response? {
        var response = chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build())
        /**
         * 刷新token条件设置为，已有refreshToken，并且已经过期，也可以后端返回特定到token失效code
         * 当第一个过期token请求接口后，改变token和refreshToken，防止同步refreshToken失效
         * 之后进入该方法的请求，token已经刷新
         */
        if (refreshToken.isNotEmpty() && isTokenExpired()) {
            takeIfNoException {
                ServiceManager.getService(IAccountService::class.java).getVerifyService().refresh(
                        onError = {
                            response.close()
                        },
                        action = { s: String ->
                            response.close()
                            response = chain.run { proceed(chain.request().newBuilder().header("Authorization", "Bearer $s").build()) }
                        }
                )
            }

        }
        return response
    }

    //检查token是否过期
    private fun isTokenExpired() = ServiceManager.getService(IAccountService::class.java).getVerifyService().isExpired()
}