import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.api.dependApiLogin
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
  id("module-debug")
}

dependApiLogin()
dependApiAccount()

dependLibBase()
dependLibUtils()
dependLibConfig()

dependLibCommon() //需要用里面的R文件

dependRoomRxjava()
dependCoroutines()
dependRoom()
dependNetwork()
dependRxjava()
dependSmartRefreshLayout()
dependMaterialDialog()
dependAutoService()
dependencies {
  // 选择器
  implementation("com.github.gzu-liyujiang.AndroidPicker:WheelView:4.1.9")
}