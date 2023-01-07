

import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  14:15
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class LibBasePlugin : BasePlugin() {

    override fun PluginScope.configure() {

        apply(plugin = "base.library")

        dependencies {
            "implementation"(Android.constraintlayout)
            "implementation"(Android.recyclerview)
            "implementation"(Android.cardview)
            "implementation"(Android.viewpager2)
            "implementation"(Android.material)
            "implementation"(Android.swiperefreshlayout)
            "implementation"(Android.flexbox)
        }

        dependencies {
            "implementation"(Android.`core-ktx`)
            "implementation"(Android.`collection-ktx`)
            "implementation"(Android.`fragment-ktx`)
            "implementation"(Android.`activity-ktx`)
        }

        dependencies {
            "implementation"(Lifecycle.`viewmodel-ktx`)
            "implementation"(Lifecycle.`livedata-ktx`)
            "implementation"(Lifecycle.`runtime-ktx`)
            "kapt"(Lifecycle.`lifecycle-compiler`)
        }

    }


}