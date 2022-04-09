package com.mredrock.cyxbs.account

import android.content.Context
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mredrock.cyxbs.account.bean.*
import com.mredrock.cyxbs.account.bean.LoginParams
import com.mredrock.cyxbs.account.bean.RefreshParams
import com.mredrock.cyxbs.account.bean.TokenWrapper
import com.mredrock.cyxbs.account.utils.UserInfoEncryption
import com.mredrock.cyxbs.api.account.*
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.api.electricity.IElectricityService
import com.mredrock.cyxbs.api.main.MAIN_LOGIN
import com.mredrock.cyxbs.api.volunteer.IVolunteerService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.utils.extensions.*
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
    private var tokenWrapper: TokenWrapper? = null
    private var isTouristMode = false
    private lateinit var mContext: Context
    override fun init(context: Context) {
        this.mContext = context
        (mUserStateService as UserStateService).loginFromCache(context)
        isTouristMode = context.defaultSharedPreferences.getBoolean(IS_TOURIST, false)
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
        mContext.defaultSharedPreferences.editor {
            putString(
                SP_KEY_USER_V2,
                mUserInfoEncryption.encrypt(Gson().toJson(tokenWrapper))
            )
        }
        //每次刷新的时候拿token请求一次个人信息，覆盖原来的
        ApiGenerator.getCommonApiService(ApiService::class.java)
            .getUserInfo("Bearer ${tokenWrapper.token}")
            .enqueue(object: Callback<RedrockApiWrapper<UserInfo>> {
                override fun onResponse(
                    call: Call<RedrockApiWrapper<UserInfo>>,
                    response: Response<RedrockApiWrapper<UserInfo>>
                ) {
                    val userInfo = response.body()?.data //如果为空就不更新
                    userInfo?.let {
                        mContext.defaultSharedPreferences.editor {
                            putString(SP_KEY_USER_INFO, mUserInfoEncryption.encrypt(Gson().toJson(userInfo)))
                        }
                        this@AccountService.user = userInfo
                    }
                }

                override fun onFailure(call: Call<RedrockApiWrapper<UserInfo>>, t: Throwable) {
                    BaseApp.appContext.toast("个人信息无法更新")
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
        override fun addOnStateChangedListener(listener: (state: IUserStateService.UserState) -> Unit) {
            stateListeners.add(object : IUserStateService.StateListener {
                override fun onStateChanged(state: IUserStateService.UserState) {
                    listener.invoke(state)
                }
            })
        }

        override fun addOnStateChangedListener(listener: IUserStateService.StateListener) {
            stateListeners.add(listener)
        }

        override fun removeStateChangedListener(listener: IUserStateService.StateListener) {
            stateListeners.remove(listener)
        }


        override fun removeAllStateListeners() {
            stateListeners.clear()
        }

        private fun notifyAllStateListeners(state: IUserStateService.UserState) {
            for (i in stateListeners) i.onStateChanged(state)
        }

        override fun isLogin() = tokenWrapper != null

        override fun isTouristMode(): Boolean = isTouristMode

        override fun isExpired(): Boolean {
            if (!isLogin()) {
                return true
            }
            val curTime = System.currentTimeMillis()
            val expiredTime = mContext.defaultSharedPreferences.getLong(SP_KEY_TOKEN_EXPIRED, 0)
            //预留10s，防止一些奇怪的错误出现
            return curTime - expiredTime >= 10000L
        }

        override fun isRefreshTokenExpired(): Boolean {
            val curTime = System.currentTimeMillis()
            val expiredTime =
                mContext.defaultSharedPreferences.getLong(SP_KEY_REFRESH_TOKEN_EXPIRED, 0)
            //当前时间比预期过期时间快10s，就过期了
            return curTime - expiredTime >= 10000L
        }

        fun loginFromCache(context: Context) {
            val encryptedTokenJson = context.defaultSharedPreferences.getString(SP_KEY_USER_V2, "")
            val userInfo = context.defaultSharedPreferences.getString(SP_KEY_USER_INFO, "")
            userInfo?.let {
                user = GsonBuilder().create()
                    .fromJson(mUserInfoEncryption.decrypt(userInfo), UserInfo::class.java)
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
            context.runOnUiThread {
                notifyAllStateListeners(state)
            }
        }
        override fun refresh(onError: () -> Unit, action: (token: String) -> Unit) {
            val refreshToken = tokenWrapper?.refreshToken ?: return
            val response = ApiGenerator.getCommonApiService(ApiService::class.java)
                .refresh(RefreshParams(refreshToken),mUserService.getStuNum()).execute()
            if (response.body() == null) {
                //TODO: 与后端确认一下状态码
                if (response.code() == 400) {//确定是因为refreshToken失效引起的刷新失败
                    mContext.runOnUiThread {
                        toast(R.string.account_token_expired_tip)
                    }
                }
                onError.invoke()
                throw HttpException(response)
            }
            response.body()?.data?.let { data ->
                bind(data)
                mContext.runOnUiThread {
                    notifyAllStateListeners(IUserStateService.UserState.REFRESH)
                    isTouristMode = false
                    this.defaultSharedPreferences.editor {
                        putBoolean(IS_TOURIST, isTouristMode)
                    }
                }
                mContext.defaultSharedPreferences.editor {
                    putLong(
                        SP_KEY_REFRESH_TOKEN_EXPIRED,
                        System.currentTimeMillis() + SP_REFRESH_DAY
                    )
                    putLong(
                        SP_KEY_TOKEN_EXPIRED,
                        System.currentTimeMillis() + SP_TOKEN_TIME
                    )
                }
                action.invoke(data.token)
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
                        ARouter.getInstance().build(MAIN_LOGIN).navigation()
                    }
                }
                negativeButton(R.string.account_login_later) {
                    dismiss()
                }
                cornerRadius(res = R.dimen.common_corner_radius)
            }

        }

        /**
         * 登录
         * @throws IllegalStateException
         */
        @WorkerThread
        override fun login(context: Context, uid: String, passwd: String) {
            val response = ApiGenerator.getCommonApiService(ApiService::class.java)
                .login(LoginParams(uid, passwd)).execute()
            //不同情况给用户不同的提示
            if (response.code() == 400) {
                throw IllegalStateException("authentication error")
            }
            if (response.body() == null) {
                throw HttpException(response)
            }
            val apiWrapper = response.body()
            //该字段涉及到Java的反射，kotlin的机制无法完全保证不为空，需要判断一下
            if (apiWrapper?.data != null) {
                bind(apiWrapper.data)
                mContext.runOnUiThread {
                    notifyAllStateListeners(IUserStateService.UserState.LOGIN)
                    isTouristMode = false
                    this.defaultSharedPreferences.editor {
                        putBoolean(IS_TOURIST, isTouristMode)
                    }
                }
                context.defaultSharedPreferences.editor {
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
                    throw RedrockApiException(info, status)
                }
            }
        }

        override fun logout(context: Context) {
            context.defaultSharedPreferences.editor {
                putString(SP_KEY_USER_V2, "")
                putString(SP_KEY_USER_INFO, "")
                putLong(SP_KEY_REFRESH_TOKEN_EXPIRED, 0)
                putLong(SP_KEY_TOKEN_EXPIRED, 0)
            }
            ServiceManager.getService(IElectricityService::class.java).clearSP()
            ServiceManager.getService(IVolunteerService::class.java).clearSP()
            user = null
            tokenWrapper = null
            mContext.runOnUiThread {
                notifyAllStateListeners(IUserStateService.UserState.NOT_LOGIN)
            }
        }

        //游客模式
        override fun loginByTourist() {
            mContext.runOnUiThread {
                notifyAllStateListeners(IUserStateService.UserState.TOURIST)
                isTouristMode = true
                this.defaultSharedPreferences.editor {
                    putBoolean(IS_TOURIST, isTouristMode)
                }
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
}