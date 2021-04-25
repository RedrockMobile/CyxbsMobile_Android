package com.mredrock.cyxbs.common.network

import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserStateService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.TOKEN_EXPIRE
import com.mredrock.cyxbs.common.config.getBaseUrl
import com.mredrock.cyxbs.common.network.cache.NetworkCache
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.takeIfNoException
import io.reactivex.Observable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * Created by AceMurder on 2018/1/24.
 */
object ApiGenerator {
    private const val DEFAULT_TIME_OUT = 10

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
        LogUtils.d("tokenTag", token)
        refreshToken = accountService.getUserTokenService().getRefreshToken()
        retrofit = Retrofit.Builder().apply {
            this.defaultConfig()
            configRetrofitBuilder {
                it.defaultConfig()
                it.configureOkHttp {
                    it.addInterceptor(TokenInterceptor())
                    it.addInterceptor(CacheInterceptor())
                }
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
//            if (isTouristMode()) {
//        getCommonApiService(clazz)
//    } else {
//        retrofit.create(clazz)
//    }

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
                            configureOkHttp {
                                it.addInterceptor(TokenInterceptor())
                            }
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

    //带token请求的OkHttp配置
    private fun OkHttpClient.Builder.configureOkHttp(config: (OkHttpClient.Builder) -> Unit): OkHttpClient {
        return this.apply {
            config.invoke(this)
        }.build()
    }

    fun tryProceed(request: Request, chain: Interceptor.Chain): Response? {
        return try {
            chain.proceed(request)
        } catch (e: Exception) {
            null
        }
    }

    //对token和refreshToken进行刷新
    @Synchronized
    private fun checkRefreshAndRequest(chain: Interceptor.Chain, expiredToken: String): Response? {
        Log.e("sandyzhang", "正在请求")
        var response: Response? = null
        try {
            response = chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build())
        } catch (e: Exception) {
            Log.e("sandyzhang", "出错1")
            e.printStackTrace()
            return null
        }

        /**
         * 刷新token条件设置为，已有refreshToken，并且已经过期，也可以后端返回特定到token失效code
         * 当第一个过期token请求接口后，改变token和refreshToken，防止同步refreshToken失效
         * 之后进入该方法的请求，token已经刷新
         * 2021-04版本中由于添加了自http状态码而来的token过期检测
         * 可能会出现多个接口因为token过期导致同时（虽然是顺序执行）的情况
         * 故要求在刷新时传递过期token过来，如果该过期token已经被刷新，就直接配置新token，不再刷新
         */
        Log.e("sandyzhang", "请求完成！！！执行刷新"+(response.isSuccessful))
        if (!response.isSuccessful || (refreshToken.isNotEmpty() && (isTokenExpired() || response.code == 403))) {
            takeIfNoException (action = {
                ServiceManager.getService(IAccountService::class.java).getVerifyService().refresh(
                        onError = {
                            response?.close()
                            Log.e("sandyzhang", "刷新失败")
                        },
                        action = { s: String ->
                            response?.close()
                            Log.e("sandyzhang", "刷新成功")
                            token = s
                        }
                )
            }, doOnException = {
                it.printStackTrace()
            })
            Log.e("sandyzhang", "重新请求")
            response = chain.run { proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build()) }
        }
        Log.e("sandyzhang", "刷新执行完成")

        return response
    }

    //检查token是否过期
    private fun isTokenExpired() = ServiceManager.getService(IAccountService::class.java).getVerifyService().isExpired()

    //是否是游客模式
    private fun isTouristMode() = ServiceManager.getService(IAccountService::class.java).getVerifyService().isTouristMode()

    class TokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            Log.e("sandyzhang", "进入！！！！！！！！！！！！！！！！！！！！")
            /**
             * 所有请求添加token到header
             * 在外面加一层判断，用于token未过期时，能够异步请求，不用阻塞在checkRefresh()
             */
            // 这个变量存 当前正在请求的url，的response
            var responseUrl = checkRefreshAndRequest(chain, token) // 这个方法无论是否刷新token成功，都会返回一个当前url的请求
//                    when {
//                        refreshToken.isEmpty() || token.isEmpty() -> {
//
//                            token = ServiceManager.getService(IAccountService::class.java).getUserTokenService().getToken()
//                            refreshToken = ServiceManager.getService(IAccountService::class.java).getUserTokenService().getRefreshToken()
//                            if (isTokenExpired()) {
//                                checkRefreshAndRequest(chain, token) // 这个方法无论是否刷新token成功，都会返回一个当前url的请求
//                            } else {
//                                chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build()) // 当前url的请求
//                            }
//                        }
//                        isTokenExpired() -> {
//                            checkRefreshAndRequest(chain, token) // 这个方法无论是否刷新token成功，都会返回一个当前url的请求
//                        }
//                        else -> {
//                            chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build()) // 当前url的请求
//                        }
//                    }

            Log.e("sandyzhang", "请求或刷新token再请求完成" + (responseUrl?.isSuccessful == true))
            // 上面是分情况讨论token可能的情况，并执行token刷新
            // 如果还是失败则再刷新一遍，以防情况：既没过期也不空，但是由于服务器原因导致token失效
            if (responseUrl?.code == TOKEN_EXPIRE || responseUrl?.isSuccessful != true) {
                responseUrl?.close()
                responseUrl = checkRefreshAndRequest(chain, token)
            }

            if (responseUrl?.isSuccessful != true) {
                Log.e("sandyzhang", "toast")
                responseUrl?.close()
                responseUrl = chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer $token").addHeader("UseCache", "true").build()) // 链式请求下一个Interceptor，也就是CacheInterceptor，要求返回缓存Response
            } else {
                saveCache(chain.request(), responseUrl)
            }

            return responseUrl
        }
    }

    class CacheInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            return if (chain.request().headers["UseCache"] != "true") {
                return chain.proceed(chain.request())
            } else {
                Log.e("sandyzhang", "UseCache" + getCache(chain.request()))

                val p = Observable.create<String> { it.onNext("使用缓存") }.setSchedulers().subscribe {
                    Toast.makeText(BaseApp.context, "使用缓存！", Toast.LENGTH_SHORT).show()
                }
                Response.Builder().code(200)
                        .message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .body(getCache(chain.request())?.toResponseBody("application/json;charset=UTF-8".toMediaTypeOrNull()))
                        .request(Request.Builder().url("http://localhost/").build())
                        .build()
            }
        }
    }

    fun getCache(request: Request): String? {
        val buffer2 = Buffer()
        request.body?.writeTo(buffer2)
        val contentType2 = request.body?.contentType()
        val charset2: Charset =
                contentType2?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
        val args = buffer2.readString(charset2)
        return NetworkCache.getCache("${request.url}?${args}")

    }

    fun saveCache(request: Request, response: Response) {
        val source = response.body?.source() ?: return
        source.request(Long.MAX_VALUE)
        val buffer = source.buffer
        val contentType = response.body?.contentType()
        val charset: Charset =
                contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8


        val buffer2 = Buffer()
        request.body?.writeTo(buffer2)
        val contentType2 = request.body?.contentType()
        val charset2: Charset =
                contentType2?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
        val args = buffer2.readString(charset2)

        NetworkCache.addCache("${request.url}?${args}", buffer.clone().readString(charset))
        Log.e(
                "sandyzhang6",
                "${request.url}?${args}" + "*" + buffer.clone().readString(charset) + " ==${buffer.size}"
        )
    }

}