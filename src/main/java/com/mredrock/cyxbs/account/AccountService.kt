package com.mredrock.cyxbs.account

import android.content.Context
import android.util.Base64
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.mredrock.cyxbs.account.bean.LoginParams
import com.mredrock.cyxbs.account.bean.RefreshParams
import com.mredrock.cyxbs.account.bean.TokenWrapper
import com.mredrock.cyxbs.account.bean.User
import com.mredrock.cyxbs.account.utils.UserInfoEncryption
import com.mredrock.cyxbs.common.config.ACCOUNT_SERVICE
import com.mredrock.cyxbs.common.config.SP_KEY_REFRESH_TOKEN_EXPIRED
import com.mredrock.cyxbs.common.config.SP_KEY_USER_V2
import com.mredrock.cyxbs.common.config.SP_REFRESH_DAY
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.service.account.*
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.takeIfNoException
import org.jetbrains.anko.runOnUiThread
import retrofit2.HttpException

/**
 * Created By jay68 on 2019-11-12.
 */
@Route(path = ACCOUNT_SERVICE, name = ACCOUNT_SERVICE)
internal class AccountService : IAccountService {
    companion object {
        @JvmStatic
        val TAG = AccountService::class.java.name
    }

    private val mUserService: IUserService = UserService()
    private val mUserStateService: IUserStateService = UserStateService()
    private val mUserEditorService: IUserEditorService = UserEditorService()
    private val mUserTokenSerVice: IUserTokenService = UserTokenSerVice()
    private val mUserInfoEncryption = UserInfoEncryption()

    private var user: User? = null
    private var tokenWrapper: TokenWrapper? = null

    private lateinit var mContext: Context
    override fun init(context: Context) {
        this.mContext = context
        (mUserStateService as UserStateService).loginFromCache(context)
    }

    override fun getUserService() = mUserService

    override fun getVerifyService() = mUserStateService

    override fun getUserEditorService() = mUserEditorService
    override fun getUserTokenService(): IUserTokenService = mUserTokenSerVice
    private fun bind(tokenWrapper: TokenWrapper) {
        val encryptedUserJson = tokenWrapper.token.split(".")[0]
        this.tokenWrapper = tokenWrapper
        this.user = User.fromJson(String(Base64.decode(encryptedUserJson, Base64.DEFAULT)))
    }

    inner class UserService : IUserService {
        override fun getRedid() = user?.redid.orEmpty()

        override fun getStuNum() = user?.stuNum.orEmpty()

        override fun getNickname() = user?.nickname.orEmpty()

        override fun getAvatarImgUrl() = user?.avatarImgUrl.orEmpty()

        override fun getIntroduction() = user?.introduction.orEmpty()

        override fun getPhone() = user?.phone.orEmpty()

        override fun getQQ() = user?.qq.orEmpty()

        override fun getGender() = user?.gender.orEmpty()

        override fun getIntegral() = user?.integral ?: 0

        override fun getRealName() = user?.realName.orEmpty()

        override fun getCheckInDay() = user?.checkInDay ?: 0

        override fun getCollege() = user?.college.orEmpty()
    }

    inner class UserEditorService : IUserEditorService {
        override fun setNickname(nickname: String) {
            user?.nickname = nickname
        }

        override fun setAvatarImgUrl(avatarImgUrl: String) {
            user?.avatarImgUrl = avatarImgUrl
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

        override fun setIntegral(integral: Int) {
            user?.integral = integral
        }

        override fun setCheckInDay(checkInDay: Int) {
            user?.checkInDay = checkInDay
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

        override fun isExpired(): Boolean {
            if (!isLogin()) {
                return true
            }
            val curTime = System.currentTimeMillis()
            val expiredTime = takeIfNoException { user?.exp?.toLong() } ?: 0L
            //预留10s，防止一些奇怪的错误出现
            return expiredTime * 1000 - curTime <= 10000L
        }

        override fun isRefreshTokenExpired(): Boolean {
            val curTime = System.currentTimeMillis()
            val expiredTime = mContext.defaultSharedPreferences.getLong(SP_KEY_REFRESH_TOKEN_EXPIRED, 0)
            //当前时间比预期过期时间快10s，就过期了
            return curTime - expiredTime >= 10000L
        }

        fun loginFromCache(context: Context) {
            val encryptedTokenJson = context.defaultSharedPreferences.getString(SP_KEY_USER_V2, "")
            takeIfNoException { bind(TokenWrapper.fromJson(mUserInfoEncryption.decrypt(encryptedTokenJson))) }
            val state = when {
                isExpired() -> IUserStateService.UserState.EXPIRED
                isLogin() -> IUserStateService.UserState.LOGIN
                else -> IUserStateService.UserState.NOT_LOGIN
            }
            context.runOnUiThread {
                notifyAllStateListeners(state)
            }
        }

        override fun refresh(onError: () -> Unit, action: (token: String) -> Unit) {
            val refreshToken = tokenWrapper?.refreshToken ?: ""
            val response = ApiGenerator.getCommonApiService(ApiService::class.java).refresh(RefreshParams(refreshToken)).execute()
            if (response.body() == null) {
                onError.invoke()
                throw HttpException(response)
            }
            response.body()?.data?.let { data ->
                bind(data)
                mContext.runOnUiThread {
                    notifyAllStateListeners(IUserStateService.UserState.LOGIN)
                }
                mContext.defaultSharedPreferences.editor {
                    putString(SP_KEY_USER_V2, mUserInfoEncryption.encrypt(Gson().toJson(data)))
                    putLong(SP_KEY_REFRESH_TOKEN_EXPIRED, System.currentTimeMillis() + SP_REFRESH_DAY)
                }
                action.invoke(data.token)
            }
        }

        @MainThread
        override fun askLogin(context: Context, reason: String) {
            if (isLogin()) {
                return
            }
            MaterialDialog.Builder(context)
                    .title("是否登录?")
                    .content(reason)
                    .positiveText("马上去登录")
                    .negativeText("我再看看")
                    .onPositive { _, _ ->
                        if (!isLogin()) {
                            ARouter.getInstance().build("/main/login").navigation()
                        }
                    }.onNegative { dialog, _ ->
                        dialog.dismiss()
                    }.show()
        }

        /**
         * 登录
         * @throws IllegalStateException
         */
        @WorkerThread
        override fun login(context: Context, uid: String, passwd: String) {
            val response = ApiGenerator.getCommonApiService(ApiService::class.java).login(LoginParams(uid, passwd)).execute()
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
                }
                context.defaultSharedPreferences.editor {
                    putString(SP_KEY_USER_V2, mUserInfoEncryption.encrypt(Gson().toJson(apiWrapper.data)))
                    putLong(SP_KEY_REFRESH_TOKEN_EXPIRED, System.currentTimeMillis() + SP_REFRESH_DAY)
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
                putLong(SP_KEY_REFRESH_TOKEN_EXPIRED, 0)
            }
            user = null
            tokenWrapper = null
            mContext.runOnUiThread {
                notifyAllStateListeners(IUserStateService.UserState.NOT_LOGIN)
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