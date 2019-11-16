package com.mredrock.cyxbs.account

import android.content.Context
import android.util.Base64
import androidx.annotation.WorkerThread
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.mredrock.cyxbs.account.bean.LoginParams
import com.mredrock.cyxbs.account.bean.TokenWrapper
import com.mredrock.cyxbs.account.bean.User
import com.mredrock.cyxbs.account.utils.UserInfoEncryption
import com.mredrock.cyxbs.common.config.ACCOUNT_SERVICE
import com.mredrock.cyxbs.common.config.SP_KEY_USER_V2
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserEditorService
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.service.account.IUserStateService
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.takeIfNoException
import com.mredrock.cyxbs.common.utils.extensions.editor
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

    private val mUserInfoEncryption = UserInfoEncryption()

    private var user: User? = null
    private var tokenWrapper: TokenWrapper? = null

    override fun init(context: Context) {
        (mUserStateService as UserStateService).loginFromCache(context)
    }

    override fun getUserService() = mUserService

    override fun getVerifyService() = mUserStateService

    override fun getUserEditorService() = mUserEditorService

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
        override fun addOnStateChangedListener(listener: (state: IUserStateService.UserState) -> Unit) {
            //todo 后面再实现该方法，用于替代以前用EventBus实现的OnStateChangeEvent事件
        }

        override fun removeStateChangedListener(listener: (state: IUserStateService.UserState) -> Unit) {
            //todo 后面再实现该方法，用于替代以前用EventBus实现的OnStateChangeEvent事件
        }

        override fun removeAllStateListeners() {
            //todo 后面再实现该方法，用于替代以前用EventBus实现的OnStateChangeEvent事件
        }

        private fun notifyAllStateListeners(state: IUserStateService.UserState) {
            //todo 后面再实现该方法，用于替代以前用EventBus实现的OnStateChangeEvent事件
        }

        override fun isLogin() = tokenWrapper != null

        override fun isExpired(): Boolean {
            if (!isLogin()) {
                return true
            }
            val curTime = System.currentTimeMillis()
            val expiredTime = takeIfNoException { user?.exp?.toLong() } ?: 0L
            //预留10s，防止一些奇怪的错误出现
            return expiredTime - curTime >= 10000L
        }

        fun loginFromCache(context: Context) {
            val encryptedTokenJson = context.defaultSharedPreferences.getString(SP_KEY_USER_V2, "")
            takeIfNoException { bind(TokenWrapper.fromJson(mUserInfoEncryption.decrypt(encryptedTokenJson))) }
            val state = when {
                isExpired() -> IUserStateService.UserState.EXPIRED
                isLogin() -> IUserStateService.UserState.LOGIN
                else -> IUserStateService.UserState.NOT_LOGIN
            }
            notifyAllStateListeners(state)
        }

        @WorkerThread
        override fun login(context: Context, uid: String, passwd: String) {
            val response = ApiGenerator.getApiService(ApiService::class.java).login(LoginParams(uid, passwd)).execute()
            if (response.body() == null) {
                throw HttpException(response)
            }
            val apiWrapper = response.body()!!
            //该字段涉及到Java的反射，kotlin的机制无法完全保证不为空，需要判断一下
            if (apiWrapper.data != null) {
                bind(apiWrapper.data)
                notifyAllStateListeners(IUserStateService.UserState.LOGIN)
                context.defaultSharedPreferences.editor {
                    putString(SP_KEY_USER_V2, mUserInfoEncryption.encrypt(Gson().toJson(apiWrapper.data)))
                    //todo 适配老版本User使用，适配完成后删除
                    putString("SP_KEY_ID_NUM", passwd)
                }
            } else {
                throw RedrockApiException(apiWrapper.info, apiWrapper.status)
            }
        }

        override fun logout(context: Context) {
            context.defaultSharedPreferences.editor {
                putString(SP_KEY_USER_V2, "")
                //todo 适配老版本User使用，适配完成后删除
                putString("SP_KEY_ID_NUM", "")
            }
            user = null
            tokenWrapper = null
            notifyAllStateListeners(IUserStateService.UserState.NOT_LOGIN)
        }
    }
}