import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
    id("module-debug")
}

dependApiAccount()

dependPhotoView()
dependGlide()
dependRxjava()
dependNetwork()

dependLibBase()
dependLibUtils()
dependLibConfig()
