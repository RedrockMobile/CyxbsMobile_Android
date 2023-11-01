import org.gradle.configurationcache.extensions.capitalized

plugins {
  `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.javaTarget.get()))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = libs.versions.kotlinJvmTarget.get()
  }
}

dependencies {
  api(libs.android.gradlePlugin)
  api(libs.kotlin.gradlePlugin)
  api(libs.ksp.gradlePlugin)
}













////////////////////////////////
//
//   生成依赖 api 模块代码的脚本
//            开始
//
////////////////////////////////
fun writeDependApi(classFile: File, outputFile: File, code: (List<String>) -> String) {
  // 获取变量名
  val fieldRegex = Regex("(?<=^  {0,4}( const )?val )[a-zA-Z]+")
  val lines = classFile.readLines()
  val fields = lines.mapNotNullTo(ArrayList()) {
    fieldRegex.find(it)?.groupValues?.first()
  }
  // 寻找所有 fun Project. 行，然后去掉已经生成的 field
  out@ for (line in lines) {
    if (line.startsWith("fun Project.")) {
      // 去掉开头是空格的
      for (field in fields) {
        if (line.contains(field.capitalized())) {
          fields.remove(field)
          continue@out
        }
      }
    }
  }
  outputFile.parentFile.mkdirs()
  if (!outputFile.exists()) {
    outputFile.createNewFile()
  }
  val head = """
    // 自动生成的代码，请不要修改 !!!
    // 请查看 versions 模块下的 build.gradle.kts
  """.trimIndent()
  outputFile.writeText(head + "\n" + code.invoke(fields))
}

// 创建一个 gradle task
val taskGenerateDependApi = tasks.register("generateDependApiFunction") {
  group = "cyxbs"
  val apiDependFile = projectDir.resolve("src")
    .resolve("main")
    .resolve("kotlin")
    .resolve("api")
    .resolve("ApiDepend.kt")
  // inputs 和 outputs 用于设置 task 缓存
  // https://segmentfault.com/a/1190000039212504
  inputs.file(apiDependFile)
  // 生成的文件在模块的 build 目录下
  val outputDir = project.layout.buildDirectory.dir(
    "generated/source/dependApi/${SourceSet.MAIN_SOURCE_SET_NAME}"
  )
  outputs.dir(outputDir)
  doLast {
    // 生成 dependApi*()
    writeDependApi(
      apiDependFile,
      outputDir.get().asFile.resolve("ApiDependFunction.kt")
    ) { list ->
      val import = """
        import org.gradle.api.Project
      """.trimIndent()
      val code = list.joinToString("\n\n", "\n\n") {
        """
          fun Project.dependApi${it.capitalized()}() {
            ApiDepend.$it.dependApiOnly(this)
          }
        """.trimIndent()
      }
      import + code
    }
  }
}

val taskGenerateDependLib = tasks.register("generateDependLibFunction") {
  group = "cyxbs"
  val libDependFile = projectDir.resolve("src")
    .resolve("main")
    .resolve("kotlin")
    .resolve("lib")
    .resolve("LibDepend.kt")
  inputs.file(libDependFile)
  val outputDir = project.layout.buildDirectory.dir(
    "generated/source/dependLib/${SourceSet.MAIN_SOURCE_SET_NAME}"
  )
  outputs.dir(outputDir)
  doLast {
    // 生成 dependLib*()
    writeDependApi(
      libDependFile,
      outputDir.get().asFile.resolve("LibDependFunction.kt")
    ) { list ->
      val import = """
        import org.gradle.api.Project
        import org.gradle.kotlin.dsl.dependencies
      """.trimIndent()
      val code = list.joinToString("\n\n", "\n\n") {
        """
          fun Project.dependLib${it.capitalized()}() {
          dependencies {
            "implementation"(project(LibDepend.$it))
          }
        }
        """.trimIndent()
      }
      import + code
    }
  }
}

// 添加进编译环境和依赖环境，在编译时会自动执行 task 生成对应代码
sourceSets {
  main {
    kotlin.srcDir(taskGenerateDependApi)
    kotlin.srcDir(taskGenerateDependLib)
  }
}

////////////////////////////////
//
//   生成依赖 api 模块代码的脚本
//            结束
//
////////////////////////////////
