import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.dependAutoService
import com.mredrock.cyxbs.convention.depend.dependRxjava
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
    id ("module-manager")
}

dependApiAccount()

dependLibUtils()

dependAutoService()
dependRxjava()