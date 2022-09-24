package com.mredrock.cyxbs.account

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mredrock.cyxbs.account.bean.*
import com.mredrock.cyxbs.account.bean.LoginParams
import com.mredrock.cyxbs.account.bean.RefreshParams
import com.mredrock.cyxbs.account.bean.TokenWrapper
import com.mredrock.cyxbs.account.utils.UserInfoEncryption
import com.mredrock.cyxbs.api.account.*
import com.mredrock.cyxbs.api.account.utils.Value
import com.mredrock.cyxbs.api.electricity.IElectricityService
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.api.volunteer.IVolunteerService
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.network.ApiException
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.service.impl
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

/**
 * Created By jay68 on 2019-11-12.
 */
@Route(path = ACCOUNT_SERVICE, name = ACCOUNT_SERVICE)
internal class AccountService : IAccountService {

    private val mUserService: IUserService = UserService()
    private val mUserStateService: IUserStateService = UserStateService()
    private val mUserEditorService: IUserEditorService = UserEditorService()
    private val mUserTokenSerVice: IUserTokenService = UserTokenSerVice()
    private val mUserInfoEncryption = UserInfoEncryption()

    private var user: UserInfo? = null
    @Volatile
    private var tokenWrapper: TokenWrapper? = null
    private var isTouristMode = false
    private lateinit var mContext: Context
    override fun init(context: Context) {
        this.mContext = context
        (mUserStateService as UserStateService).loginFromCache(context)
        isTouristMode = defaultSp.getBoolean(SP_IS_TOURIST, false)
    }

    override fun getUserService() = mUserService

    override fun getVerifyService() = mUserStateService

    override fun getUserEditorService() = mUserEditorService
    override fun getUserTokenService(): IUserTokenService = mUserTokenSerVice

    private fun bind(tokenWrapper: TokenWrapper) {
        //如果接口出问题，token or refreshToken为空就不要他被覆盖，以免出现未知的问题
        if(tokenWrapper.isEmptyData()){
            return
        }
        this.tokenWrapper = tokenWrapper
        //每次刷新存储到本地
        defaultSp.edit {
            putString(
                SP_KEY_USER_V2,
                mUserInfoEncryption.encrypt(Gson().toJson(tokenWrapper))
            )
        }
        //每次刷新的时候拿token请求一次个人信息，覆盖原来的
        ApiGenerator.getCommonApiService(ApiService::class)
            .getUserInfo("Bearer ${tokenWrapper.token}")
            .enqueue(object: Callback<ApiWrapper<UserInfo>> {
                override fun onResponse(
                    call: Call<ApiWrapper<UserInfo>>,
                    response: Response<ApiWrapper<UserInfo>>
                ) {
                    val userInfo = response.body()?.data //如果为空就不更新
                    userInfo?.let {
                        defaultSp.edit {
                            putString(SP_KEY_USER_INFO, mUserInfoEncryption.encrypt(Gson().toJson(userInfo)))
                        }
                        this@AccountService.user = userInfo
                        // 通知 StuNum 更新
                        (mUserService as UserService).emitStuNum(it.stuNum)
                    }
                }

                override fun onFailure(call: Call<ApiWrapper<UserInfo>>, t: Throwable) {
                    toast("个人信息无法更新")
                }
            })
    }

    inner class UserService : IUserService {

        override fun getStuNum() = user?.stuNum.orEmpty()

        override fun getNickname() = user?.nickname.orEmpty()

        override fun getAvatarImgUrl() = user?.photoSrc.orEmpty()

        override fun getPhotoThumbnailSrc() = user?.photoThumbnailSrc.orEmpty()

        override fun getIntroduction() = user?.introduction.orEmpty()

        override fun getPhone() = user?.phone.toString()

        override fun getQQ() = user?.qq.toString()

        override fun getGender() = user?.gender.orEmpty()

        override fun getRealName() = user?.realName.orEmpty()

        override fun getCollege() = user?.college.orEmpty()

        override fun getBirth() = user?.birthDay.orEmpty()

        override fun getRedid() = user?.redId.orEmpty()

        //用于刷新个人信息，请在需要的地方调用
        override fun refreshInfo() {
            tokenWrapper?.let { bind(it) }
        }
        
        // 发送学号给下游
        fun emitStuNum(stuNum: String?) {
            val value = Value(stuNum)
            stuNumState.onNext(value)
            stuNumEvent.onNext(value)
        }
    
        private val stuNumState = BehaviorSubject.create<Value<String>>()
        private val stuNumEvent = PublishSubject.create<Value<String>>()
    
        override fun observeStuNumState(): Observable<Value<String>> {
            return stuNumState.distinctUntilChanged()
        }
    
        override fun observeStuNumEvent(): Observable<Value<String>> {
            return stuNumEvent.distinctUntilChanged()
        }
    }

    inner class UserEditorService : IUserEditorService {
        override fun setNickname(nickname: String) {
            user?.nickname = nickname
        }

        override fun setAvatarImgUrl(avatarImgUrl: String) {
            user?.photoSrc = avatarImgUrl
        }

        override fun setIntroduction(introduction: String) {
            user?.introduction = introduction
        }

        override fun setPhone(phone: String) {
            user?.phone = phone
        }

        override fun setQQ(qq: String) {
            user?.qq = qq
        }
    }

    inner class UserStateService : IUserStateService {
        private val stateListeners: MutableList<IUserStateService.StateListener> = mutableListOf()
    
        @Deprecated(
            "该方法不好管理生命周期，更建议使用 observeStateFlow()",
            replaceWith = ReplaceWith("observeStateFlow()")
        )
        override fun addOnStateChangedListener(listener: (state: IUserStateService.UserState) -> Unit) {
            stateListeners.add(object : IUserStateService.StateListener {
                override fun onStateChanged(state: IUserStateService.UserState) {
                    listener.invoke(state)
                }
            })
        }
    
        @Deprecated(
            "该方法不好管理生命周期，更建议使用 observeStateFlow()",
            replaceWith = ReplaceWith("observeStateFlow()")
        )
        override fun addOnStateChangedListener(listener: IUserStateService.StateListener) {
            stateListeners.add(listener)
        }
    
        override fun removeStateChangedListener(listener: IUserStateService.StateListener) {
            stateListeners.remove(listener)
        }


        override fun removeAllStateListeners() {
            stateListeners.clear()
        }
    
        private val userStateState = BehaviorSubject.create<IUserStateService.UserState>()
        private val userStateEvent = PublishSubject.create<IUserStateService.UserState>()
    
        override fun observeUserStateState(): Observable<IUserStateService.UserState> {
            return userStateState.distinctUntilChanged()
        }
        override fun observeUserStateEvent(): Observable<IUserStateService.UserState> {
            return userStateEvent.distinctUntilChanged()
        }

        private fun notifyAllStateListeners(state: IUserStateService.UserState) {
            for (i in stateListeners) i.onStateChanged(state)
            userStateState.onNext(state)
            userStateEvent.onNext(state)
        }

        override fun isLogin() = tokenWrapper != null

        override fun isTouristMode(): Boolean = isTouristMode

        override fun isExpired(): Boolean {
            if (!isLogin()) {
                return true
            }
            val curTime = System.currentTimeMillis()
            val expiredTime = defaultSp.getLong(SP_KEY_TOKEN_EXPIRED, 0)
            //预留10s，防止一些奇怪的错误出现
            return curTime - expiredTime >= 10000L
        }

        override fun isRefreshTokenExpired(): Boolean {
            val curTime = System.currentTimeMillis()
            val expiredTime =
                defaultSp.getLong(SP_KEY_REFRESH_TOKEN_EXPIRED, 0)
            //当前时间比预期过期时间快10s，就过期了
            return curTime - expiredTime >= 10000L
        }

        fun loginFromCache(context: Context) {
            val encryptedTokenJson = defaultSp.getString(SP_KEY_USER_V2, "")
            val userInfo = defaultSp.getString(SP_KEY_USER_INFO, "")
            userInfo?.let {
                user = GsonBuilder().create()
                    .fromJson(mUserInfoEncryption.decrypt(userInfo), UserInfo::class.java)
                // 这里是从本地拿取数据，是第一次通知 StuNum 更新
                (mUserService as UserService).emitStuNum(user?.stuNum)
            }
            tokenWrapper = TokenWrapper.fromJson(
                mUserInfoEncryption.decrypt(
                    encryptedTokenJson
                )
            )
            val state = when {
                isLogin() -> IUserStateService.UserState.LOGIN
                else -> IUserStateService.UserState.NOT_LOGIN
            }
            Handler(Looper.getMainLooper()).post {
                notifyAllStateListeners(state)
            }
        }
        override fun refresh(): String? {
            val refreshToken = tokenWrapper?.refreshToken ?: return null
            val response = ApiGenerator.getCommonApiService(ApiService::class)
                .refresh(RefreshParams(refreshToken),mUserService.getStuNum()).execute()
            val body = response.body()
            if (body != null) {
                // 根据后端标准返回文档：https://redrock.feishu.cn/wiki/wikcnB9p6U45ZJZmxwTEu8QXvye
                if (body.status == 20004) {
                    toast("用户认证刷新失败，请重新登录")
                    return null
                }
            } else return null
            return body.data.let { data ->
                bind(data)
                isTouristMode = false
                defaultSp.edit {
                    putBoolean(SP_IS_TOURIST, isTouristMode)
                }
                Handler(Looper.getMainLooper()).post {
                    notifyAllStateListeners(IUserStateService.UserState.REFRESH)
                }
                defaultSp.edit {
                    putLong(
                        SP_KEY_REFRESH_TOKEN_EXPIRED,
                        System.currentTimeMillis() + SP_REFRESH_DAY
                    )
                    putLong(
                        SP_KEY_TOKEN_EXPIRED,
                        System.currentTimeMillis() + SP_TOKEN_TIME
                    )
                }
                data.token
            }
        }


        @MainThread
        override fun askLogin(context: Context, reason: String) {
            if (isLogin()) {
                return
            }

            MaterialDialog(context).show {
                title(R.string.account_whether_login)
                message(text = reason)
                positiveButton(R.string.account_login_now) {
                    if (!isLogin()) {
                        ILoginService::class.impl
                            .startLoginActivityReboot(context)
                    }
                }
                negativeButton(R.string.account_login_later) {
                    dismiss()
                }
                cornerRadius(16.dp2pxF)
            }

        }

        /**
         * 登录
         * @throws IllegalStateException
         */
        @WorkerThread
        override fun login(context: Context, uid: String, passwd: String) {
            val response = ApiGenerator.getCommonApiService(ApiService::class)
                .login(LoginParams(uid, passwd)).execute()
            //不同情况给用户不同的提示
            if (response.code() == 400) {
                // 22年 后端有 "student info fail" 和 "sign in failed" 两种状态，但我们直接给学号或者密码错误即可
                // 该异常已与下游约定，不可更改！！！
                throw IllegalStateException("authentication error")
            }
            if (response.body() == null) {
                throw HttpException(response)
            }
            val apiWrapper = response.body()
            //该字段涉及到Java的反射，kotlin的机制无法完全保证不为空，需要判断一下
            if (apiWrapper?.data != null) {
                bind(apiWrapper.data)
                isTouristMode = false
                defaultSp.edit {
                    putBoolean(SP_IS_TOURIST, isTouristMode)
                }
                Handler(Looper.getMainLooper()).post {
                    notifyAllStateListeners(IUserStateService.UserState.LOGIN)
                }
                defaultSp.edit {
                    putString(
                        SP_KEY_USER_V2,
                        mUserInfoEncryption.encrypt(Gson().toJson(apiWrapper.data))
                    )
                    putLong(
                        SP_KEY_REFRESH_TOKEN_EXPIRED,
                        System.currentTimeMillis() + SP_REFRESH_DAY
                    )
                    putLong(
                        SP_KEY_TOKEN_EXPIRED,
                        System.currentTimeMillis() + SP_TOKEN_TIME
                    )
                }
            } else {
                apiWrapper?.apply {
                    throw ApiException(status, info)
                }
            }
        }

        override fun logout(context: Context) {
            defaultSp.edit {
                putString(SP_KEY_USER_V2, "")
                putString(SP_KEY_USER_INFO, "")
                putLong(SP_KEY_REFRESH_TOKEN_EXPIRED, 0)
                putLong(SP_KEY_TOKEN_EXPIRED, 0)
            }
            ServiceManager(IElectricityService::class).clearSP()
            ServiceManager(IVolunteerService::class).clearSP()
            user = null
            // 通知 StuNum 更新
            (mUserService as UserService).emitStuNum(null)
            tokenWrapper = null
            Handler(Looper.getMainLooper()).post {
                notifyAllStateListeners(IUserStateService.UserState.NOT_LOGIN)
            }
        }

        //游客模式
        override fun loginByTourist() {
            isTouristMode = true
            defaultSp.edit {
                putBoolean(SP_IS_TOURIST, isTouristMode)
            }
            Handler(Looper.getMainLooper()).post {
                notifyAllStateListeners(IUserStateService.UserState.TOURIST)
            }
        }
    }

    inner class UserTokenSerVice : IUserTokenService {
        override fun getRefreshToken(): String {
            return tokenWrapper?.refreshToken ?: ""
        }

        override fun getToken(): String {
            return tokenWrapper?.token ?: ""
        }
    }
    
    companion object {
        // 是否是游客模式
        const val SP_IS_TOURIST = "is_tourist"
    
        //UserToken信息存储key
        const val SP_KEY_USER_V2 = "cyxbsmobile_user_v2"
    
        //User信息存储key
        const val SP_KEY_USER_INFO = "cyxbsmobile_user_info"
    
        //token失效时间
        const val SP_KEY_TOKEN_EXPIRED = "user_token_expired_time"
    
        //token 后端规定token2h过期，客户端规定1h55分过期，以防错误，时间戳
        const val SP_TOKEN_TIME = 6900000
    
        //refreshToken失效时间
        const val SP_KEY_REFRESH_TOKEN_EXPIRED = "user_refresh_token_expired_time"
    
        //refreshToken 后端规定45天过期，客户端规定44天过期，以防错误，时间戳
        const val SP_REFRESH_DAY = 3801600000
    }
}