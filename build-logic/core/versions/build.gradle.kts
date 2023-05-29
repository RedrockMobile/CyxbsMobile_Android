import org.gradle.configurationcache.extensions.capitalized

plugins {
  `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  api(libs.kotlin.gradlePlugin)
}













////////////////////////////////
//
//   生成依赖 api 模块代码的脚本
//            开始
//
////////////////////////////////
fun writeDependApi(classFile: File, code: (List<String>) -> String) {
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
  val file = classFile.parentFile.resolve(classFile.name.substringBeforeLast(".kt") + "_fun.kt")
  if (file.exists()) {
    val firstLine = file.bufferedReader().use {
      it.readLine()
    }
    if (firstLine == "// $fields") {
      // 未过时
      return
    }
  } else {
    file.createNewFile()
  }
  
  val head = """
    // $fields
    // 自动生成的代码，请不要修改 !!!
  """.trimIndent()
  // 过时了或未存在 file，就写入
  file.writeText(head + "\n" + code.invoke(fields))
}

// 生成 dependApi*()
writeDependApi(
  projectDir.resolve("src")
    .resolve("main")
    .resolve("kotlin")
    .resolve("api")
    .resolve("ApiDepend.kt")
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

// 生成 dependLib*()
writeDependApi(
  projectDir.resolve("src")
    .resolve("main")
    .resolve("kotlin")
    .resolve("lib")
    .resolve("LibDepend.kt")
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
////////////////////////////////
//
//   生成依赖 api 模块代码的脚本
//            结束
//
////////////////////////////////
