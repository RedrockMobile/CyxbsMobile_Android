package com.mredrock.cyxbs.lib.utils.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.utils.BuildConfig
import com.mredrock.cyxbs.lib.utils.UtilsApplicationWrapper
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.lib.utils.utils.LogLocal
import com.mredrock.cyxbs.lib.utils.utils.LogUtils
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

/**
 * # 用法
 * ## 命名规则
 * XXXApiService 接口，命名规则，以 ApiService 结尾
 *
 * ## 接口模板
 * ```
 * interface XXXApiService {
 *
 *     @GET("/aaa/bbb")
 *     fun getXXX(): Single<ApiWrapper<Bean>>
 *     // 统一使用 ApiWrapper 或 ApiStatus 包装，注意 Bean 类要去掉 data 字段，不然会报 json 错误
 *
 *     companion object {
 *         val INSTANCE by lazy {
 *             ApiGenerator.getXXXApiService(XXXApiService::class)
 *         }
 *     }
 * }
 *
 * 或者：
 * interface XXXApiService : IApi
 *
 * 使用：
 * XXXApiService::class.impl
 *     .getXXX()
 * ```
 *
 * ## 示例代码
 * ```
 * ApiService.INSTANCE.getXXX()
 *     .subscribeOn(Schedulers.io())  // 线程切换
 *     .observeOn(AndroidSchedulers.mainThread())
 *     .mapOrInterceptException {     // 当 errorCode 的值不为成功时抛错，并拦截异常
 *         // 这里面可以使用 DSL 写法，更多详细用法请看该方法注释
 *     }
 *     .safeSubscribeBy {            // 如果是网络连接错误，则这里会默认处理
 *         // 成功的时候
 *         // 如果是仓库层，请使用 unsafeSubscribeBy()
 *     }
 * ```
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 22:30
 */
object ApiGenerator {
    // 用于 debug 时保存网络请求，因为异常触发时进程被摧毁，Pandora 记录的请求会被清空
    val apiResultList by lazy {
        CopyOnWriteArrayList<ApiResult>()
    }

    class ApiResult(var request: Request) {
        var response: Response? = null
        var stackTrace: String? = null // 直接保存 throwable 对象的话，不会被记录下来，不知道为什么
    }

    private const val DEFAULT_TIME_OUT = 10

    private var retrofit: Retrofit //统一添加了token到header
    private var commonRetrofit: Retrofit // 未添加token到header

    private val mAccountService = IAccountService::class.impl

    //init对两种公共的retrofit进行配置
    init {
        //添加监听得到登录后的token和refreshToken,应用于初次登录或重新登录
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

    /**
     * 带 token 的请求
     */
    fun <T : Any> getApiService(clazz: KClass<T>): T = if (isTouristMode()) {
        getCommonApiService(clazz)
    } else {
        retrofit.create(clazz.java)
    }
    /**
     * 带 token 的请求，适配lib_common模块
     */
    fun <T> getApiService(clazz: Class<T>): T = if (isTouristMode()) {
        getCommonApiService(clazz)
    } else {
        retrofit.create(clazz)
    }

    /**
     * 不带 token 的请求
     */
    fun <T : Any> getCommonApiService(clazz: KClass<T>): T {
        return commonRetrofit.create(clazz.java)
    }
    /**
     * 不带 token 的请求，适配老模块lib_common
     */
    fun <T> getCommonApiService(clazz: Class<T>): T {
        return commonRetrofit.create(clazz)
    }

    fun createSelfRetrofit(
        tokenNeeded: Boolean,
        retrofitConfig: ((Retrofit.Builder) -> Retrofit.Builder)? = null,
        okHttpClientConfig: ((OkHttpClient.Builder) -> OkHttpClient.Builder)? = null
    ): Retrofit {
        return createSelfRetrofit(retrofitConfig, okHttpClientConfig, tokenNeeded)
    }

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
    fun createSelfRetrofit(
        retrofitConfig: ((Retrofit.Builder) -> Retrofit.Builder)? = null,
        okHttpClientConfig: ((OkHttpClient.Builder) -> OkHttpClient.Builder)? = null,
        tokenNeeded: Boolean
    ): Retrofit {
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
                LogUtils.d("OKHTTP", message)
                LogLocal.log("OKHTTP", "OKHTTP$message")
            }
            logging.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(Interceptor {
                it.proceed(
                    it.request().newBuilder()
                        .addHeader("APPVersion", BuildConfig.VERSION_NAME)
                        .build()
                )
            })
            addInterceptor(logging)
            //这里是在debug模式下方便开发人员简单确认 http 错误码 和 url(magipoke开始切的)
            if (BuildConfig.DEBUG) {
                addInterceptor(Interceptor {
                    val request = it.request()
                    Log.d("OKHTTP", "OKHTTP${request.body}")

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
        if (BuildConfig.DEBUG) {
            addInterceptor {
                // 专门用于收集信息的拦截器
                val request = it.request()
                val apiResult = ApiResult(request.newBuilder().build())
                apiResultList.add(apiResult)
                try {
                    it.proceed(request).also { response ->
                        // 如果请求成功了就记录新的 request
                        apiResult.request = response.request.newBuilder().build()
                        apiResult.response = response.newBuilder().build()
                    }
                } catch (e: Exception) {
                    apiResult.stackTrace = e.stackTraceToString()
                    throw e
                }
            }
        }
    }


    //不带token请求的OkHttp配置
    private fun OkHttpClient.Builder.configureCommonOkHttp(): OkHttpClient {
        return this.apply {
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
                        .addHeader("version", BuildConfig.VERSION_NAME)
                        .build()
                )
            })
            /**
             * 连接失败时切换备用url的Interceptor
             * 一旦切换，只有重启app才能切回来（因为如果请求得到的url不是原来的@{link getBaseUrl()}，则切换到新的url，而以后访问都用这个新的url了）
             * 放在tokenInterceptor上游的理由是：因为那里面还有token刷新机制，无法判断是否真正是因为服务器的原因请求失败
             */
            interceptors().add(BackupInterceptor)


            interceptors().add(Interceptor {

                if (!mAccountService.getVerifyService().isLogin()) {
                    // 未登录直接请求，有些人对于不需要 token 的请求也使用了这个
                    return@Interceptor it.proceed(it.request())
                }

                /**
                 * 所有请求添加token到header
                 *
                 * 在 ApiWrapper 中会根据 status 判断是否过期，过期就会赋值为过期的时间戳，
                 * 然后这里 isTokenExpired() 会返回 true，最后触发刷新
                 */
                if (isTokenExpired()) {
                    checkRefresh(it, mAccountService.getUserTokenService().getToken())
                } else it.proceedWithToken()
            })
        }.build()
    }

    private val mReentrantLock = ReentrantLock()

    //对token进行刷新
    private fun checkRefresh(chain: Interceptor.Chain, expiredToken: String): Response {
        mReentrantLock.withLock {
            val token = mAccountService.getUserTokenService().getToken()
            // 判断之前的过期 token 是否跟现在的 token 一样
            if (expiredToken == token) {
                // 一样的话说明需要刷新 token
                mAccountService.getVerifyService().refresh()
            }
            // 如果本来网络就是崩的，一堆请求会堵在这里刷新 token，但这种情况本来就会因为网络问题而全部请求失败，
            // 所以不用管这种情况（万一有个请求刷新成功了?）
        }
        return chain.proceedWithToken()
    }

    private fun Interceptor.Chain.proceedWithToken(
        block: (Request.Builder.() -> Unit)? = null
    ): Response {
        val token = mAccountService.getUserTokenService().getToken()
        Log.d("lx", "token: $token")
        return proceed(
            request()
                .newBuilder()
                .header("Authorization", "Bearer $token")
                .also { block?.invoke(it) }
                .build()
        )
    }

    object BackupInterceptor : Interceptor {

        @Volatile
        private var mBackupUrl: String? = null

        private var mLastToastTime = 0L

        override fun intercept(chain: Interceptor.Chain): Response {

            // 如果切换过url，则直接用这个url请求
            val backupUrl = mBackupUrl
            if (backupUrl != null) {
                return useBackupUrl(backupUrl, chain)
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
                                UtilsApplicationWrapper.application,
                                "dev 请求异常, 请查看 Pandora",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                END_POINT_REDROCK_PROD -> {
                    val url = getBackupUrl()
                    mBackupUrl = url
                    useBackupUrl(url, chain)
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


        private val mLock = ReentrantLock()

        private fun getBackupUrl(): String {
            val backupUrl = mBackupUrl
            if (backupUrl != null) {
                return backupUrl
            }
            return mLock.withLock {
                val url = mBackupUrl
                if (url != null) url // 如果 mBackupUrl 不为 null 则说明前一个线程已经请求到了容灾地址
                else {
                    val okHttpClient = OkHttpClient()
                    val request: Request = Request.Builder()
                        .url(BASE_NORMAL_BACKUP_GET)
                        .build()
                    val call = okHttpClient.newCall(request)
                    val json = call.execute().body?.string()
                    val backupUrlStatus = Gson().fromJson<ApiWrapper<BackupUrlStatus>>(
                        json,
                        object : TypeToken<ApiWrapper<BackupUrlStatus>>() {}.type
                    )
                    backupUrlStatus.data.baseUrl
                }
            }
        }

        private fun useBackupUrl(backupUrl: String, chain: Interceptor.Chain): Response {
            val newUrl: HttpUrl = chain.request().url
                .newBuilder()
                .scheme("https")
                .host(backupUrl)
                .build()
            val builder: Request.Builder = chain.request().newBuilder()
            return chain.proceed(builder.url(newUrl).build())
        }

        data class BackupUrlStatus(
            @SerializedName("base_url")
            val baseUrl: String
        )
    }

    //是否是游客模式
    private fun isTouristMode() =
        ServiceManager(IAccountService::class).getVerifyService().isTouristMode()

    //检查token是否过期
    private fun isTokenExpired() =
        ServiceManager(IAccountService::class).getVerifyService().isExpired()
}

/**
 * 实现该接口后后直接使用这种写法：
 * ```
 * ApiService::class.api
 *   .getXXX()
 * ```
 */
interface IApi {
    companion object {
        internal val MAP = HashMap<KClass<out IApi>, IApi>()
        internal val MAP_COMMON = HashMap<KClass<out IApi>, IApi>()
    }
}

/**
 * 带 token 的请求
 */
@Suppress("UNCHECKED_CAST")
val <I : IApi> KClass<I>.api: I
    get() = IApi.MAP.getOrPut(this) {
        ApiGenerator.getApiService(this)
    } as I

/**
 * 不带 token 的请求
 */
@Suppress("UNCHECKED_CAST")
val <I : IApi> KClass<I>.commonApi: I
    get() = IApi.MAP_COMMON.getOrPut(this) {
        ApiGenerator.getCommonApiService(this)
    } as I