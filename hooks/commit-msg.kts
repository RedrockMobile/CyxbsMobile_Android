#!/usr/bin/env kscript
import java.io.File
import java.util.regex.Pattern
import kotlin.system.exitProcess

val commitMessage = File(args[0]).readText()
println(commitMessage)
val typeList = listOf("fix", "feature", "optimize", "release", "style")
val matcher = Pattern.compile("\\[([a-z]+)\\](.|\n)+(description[:：](.|\n)+)*")
    .matcher(commitMessage)
if (matcher.matches()) {
    //检查title
    if (typeList.find { matcher.group(1) == it } == null) {
        print("你所填写的type没有在提交规范type中，请确认是否为一下其一：")
        for ((i, type) in typeList.withIndex()) {
            if (i % 3 == 0) println()
            print("${i + 1}.$type ")
        }
        exitProcess(1)
    }
} else {
    println(
        "请检查commit msg是否正确，标准格式为：\n" +
                "***************************\n" +
                "[type] title\n" +
                "\n" +
                "description:\n" +
                "***************************\n" +
                "其中description为可选，较简单的提交可只写type和title"
    )
    exitProcess(2)
}