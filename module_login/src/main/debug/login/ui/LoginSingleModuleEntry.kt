package login.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.BaseSingleModuleActivity

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/7 21:38
 */
@Route(path = "/single/login")
class LoginSingleModuleEntry : ISingleModuleEntry {

  override val isNeedLogin: Boolean
    get() = true

  override fun getPage(activity: BaseSingleModuleActivity): ISingleModuleEntry.Page {
    // isNeedLogin 为 true 则会主动启动 LoginActivity，所以就没有必要写其他代码
    return ISingleModuleEntry.ActionPage { null }
  }
}