/*
* 操控 Git 钩子文件的脚本
* 使用教程：https://git-scm.com/book/zh/v2/%E8%87%AA%E5%AE%9A%E4%B9%89-Git-Git-%E9%92%A9%E5%AD%90
*
* 由于钩子文件应该放到 .git/hooks 文件下，并且要去掉 .sh 后缀，
* 但为了以后好修改，所以将钩子文件以 sh 文件的形式放在了根项目的 hooks 文件夹下，
* 通过该脚本将文件移动到 .git/hooks 文件夹下
*
* 注意：
* 1、该脚本应该是在根目录下的 build.gradle.kts 中使用
* 2、部分系统可能存在权限问题，无法删除文件
* */

val hooksFile = rootDir.absoluteFile.resolve("hooks")
val gitHookFile = rootDir.absoluteFile.resolve(".git").resolve("hooks")

fun moveHookFile() {
  println("正在移动 git 钩子文件：")
  
  hooksFile.listFiles()?.forEach { file ->
    val newFile = gitHookFile.resolve(file.name.substringBeforeLast(".sh"))
    if (newFile.exists()) {
      if (!newFile.delete()) {
        println("${newFile.absolutePath} 删除失败，大概率是 gradle 脚本没有权限删除旧的 hook 文件")
        return
      }
    }
    copy {
      from(file)
      into(newFile.parentFile)
      rename { it.substringBeforeLast(".sh") }
    }
    println("${file.name} 已复制到 .git/hooks 文件下")
  }
  println("git 钩子文件移动完毕")
}

// 将 根目录下的 hooks 移动到 .git/hooks 的 task
// 已生成了 group 为 hook，名字叫 git-hook-move 的任务
val moveTask = tasks.register("git-hook-move") {
  group = "hook"
  doFirst { moveHookFile() }
}
