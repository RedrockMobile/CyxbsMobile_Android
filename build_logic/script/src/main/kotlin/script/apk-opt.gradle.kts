package script

import com.smallsoho.mcplugin.image.Config

apply(plugin = "McImage")

configure<Config> {
    isCheckSize = true //是否检测图片大小，默认为true
    optimizeType = "ConvertWebp" //优化类型，可选"ConvertWebp"，"Compress"，转换为webp或原图压缩，默认Compress，使用ConvertWep需要min sdk >= 18.但是压缩效果更好
    maxSize = 1 * 2024f * 2024f //大图片阈值，default 1MB
    enableWhenDebug = false //debug下是否可用，default true
    isCheckPixels = true // 是否检测大像素图片，default true
    maxWidth = 2000 //default 1000 如果开启图片宽高检查，默认的最大宽度
    maxHeight = 2000 //default 1000 如果开启图片宽高检查，默认的最大高度
    whiteList = arrayOf("icon_launcher.png") //默认为空，如果添加，对图片不进行任何处理

    mctoolsDir = "$rootDir"
    isSupportAlphaWebp = false  //是否支持带有透明度的webp，default false,带有透明图的图片会进行压缩
    multiThread = true  //是否开启多线程处理图片，default true
    bigImageWhiteList = arrayOf() //默认为空，如果添加，大图检测将跳过这些图片
}