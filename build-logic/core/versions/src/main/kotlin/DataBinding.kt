import com.android.build.api.dsl.ApplicationBuildFeatures
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryBuildFeatures
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * 使用 DataBinding
 *
 * @author 985892345
 * 2023/7/16 16:02
 */
object DataBinding {
  const val version = "8.0.2"

  // https://mvnrepository.com/artifact/androidx.databinding/databinding-runtime
  val `databinding-runtime` = "androidx.databinding:databinding-runtime:$version"
  // https://mvnrepository.com/artifact/androidx.databinding/databinding-ktx
  val `databinding-ktx` = "androidx.databinding:databinding-ktx:$version"
}

/**
 * 使用 DataBinding
 * @param isOnlyDepend 是否只依赖而不开启 DataBinding，默认开启 DataBinding
 */
fun Project.useDataBinding(isOnlyDepend: Boolean = false) {
  if (!isOnlyDepend) {
    // kapt 按需引入
    apply(plugin = "org.jetbrains.kotlin.kapt")
    extensions.configure(CommonExtension::class.java) {
      buildFeatures {
        when (this) {
          is LibraryBuildFeatures -> dataBinding = true // com.android.library 插件的配置
          is ApplicationBuildFeatures -> dataBinding = true // com.android.application 插件的配置
        }
      }
    }
  }
  dependencies {
    "implementation"(DataBinding.`databinding-runtime`)
    "implementation"(DataBinding.`databinding-ktx`)
  }
}