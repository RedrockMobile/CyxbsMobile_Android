// [base, config, utils, debug, course]
// 自动生成的代码，请不要修改 !!!
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.dependLibBase() {
  dependencies {
    "implementation"(project(LibDepend.base))
  }
}

fun Project.dependLibConfig() {
  dependencies {
    "implementation"(project(LibDepend.config))
  }
}

fun Project.dependLibUtils() {
  dependencies {
    "implementation"(project(LibDepend.utils))
  }
}

fun Project.dependLibDebug() {
  dependencies {
    "implementation"(project(LibDepend.debug))
  }
}

fun Project.dependLibCourse() {
  dependencies {
    "implementation"(project(LibDepend.course))
  }
}