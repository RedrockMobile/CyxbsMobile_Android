import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Action

/**
 *@author ZhiQiang Tu
 *@time 2022/10/10  17:16
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

fun PluginScope.androidLib(configure: Action<LibraryExtension>) {
    extensions.configure("android", configure)
}

fun PluginScope.androidApp(configure: Action<ApplicationExtension>) {
    extensions.configure("android", configure)
}