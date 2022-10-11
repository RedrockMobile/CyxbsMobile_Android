import com.mredrock.cyxbs.convention.depend.dependAndroidKtx
import com.mredrock.cyxbs.convention.depend.dependAndroidView
import com.mredrock.cyxbs.convention.depend.dependLifecycleKtx
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  17:05
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

class ModuleManagerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        if (target.plugins.hasPlugin("com.android.application")) {
            throw RuntimeException("取消单模块调试才能使用多模块插件！")
        }

        // 这里面只依赖带有 internal 修饰的
        with(target) {
            dependAndroidView()
            dependAndroidKtx()
            dependLifecycleKtx()
        }
    }
}