import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
    id("module-manager")
}

dependApiAccount()
dependApiCourse()
dependApiAffair()

dependLibBase()
dependLibUtils()
dependLibConfig()

dependRoom()
dependRxjava()
dependNetwork()
dependRoomRxjava()


