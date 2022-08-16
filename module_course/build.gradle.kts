import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.*

plugins {
    id("module-debug")
}

dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()
dependApiMain()

dependRoom()
dependRoomRxjava()
dependRxjava()
dependNetwork()
