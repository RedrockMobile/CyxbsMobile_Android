import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon

plugins {
    id ("module-manager")
}

dependApiAccount()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块