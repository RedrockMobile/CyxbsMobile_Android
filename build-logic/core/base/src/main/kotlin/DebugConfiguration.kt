import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

/**
 * 该类用于设置一些 debug 时需要的属性和配置
 *
 * @author 985892345
 * 2023/3/4 16:24
 */
object DebugConfiguration {
  
  fun initApplication(project: Project) {
    project.initApplicationInternal()
  }
  
  private fun Project.initApplicationInternal() {
    if (!gradle.startParameter.taskNames.any { it.contains("Release") }) {
      // Pandora 插件使用了会在 gradle 8.0 移除的 transform API，
      // 我的建议是你们 fork 下仓库，然后改了发一个 jitpack 依赖（发这个依赖很简单，不需要账号）
//      apply(plugin = "pandora-plugin")
//      apply(plugin = "me.ele.lancet") // CodeLocator 高级功能所需要的插件，目前无法使用
      /**
       * todo 截止 23年3/3，doKit 没有适配高版本的 gradle，所以插件引入会导致编译失败
       *  如果后面 doKit 适配高版本了，麻烦改下 doKit 版本
       *  更详细的原因可以看 build.gradle 里面写的注释
       */
//      apply(plugin = "com.didi.dokit")
//      extensions.configure<DoKitExt> {
//        //通用设置
//        comm {
//          //地图经纬度开关
//          gpsSwitch = true
//          //网络开关
//          networkSwitch = true
//          //大图开关
//          bigImgSwitch = true
//          //webView js 抓包
//          webViewSwitch = false
//        }
//      }
    }
    dependencies {
      "debugImplementation"(project(LibDepend.debug))
    
      /**
       * 字节在用的代码调试工具，请先在插件市场中搜索下载 CodeLocator
       *
       * 功能很多，常用的几个功能：
       * 1、定位事件分发链上所有的 View
       * 2、极其强大的跳转至对应代码的功能
       *
       * 注意：每次使用时记得先按第一个手掌的功能抓取当前界面，不然你会发现找不到对应 View
       *
       * 仓库地址：https://github.com/bytedance/CodeLocator
       *
       * 使用文档：https://github.com/bytedance/CodeLocator/blob/main/how_to_use_codelocator_zh.md
       *
       * 22.7.30：
       * 在做了尝试后，目前因为 LaunchX 太老而无法使用跳转功能，所以目前 CodeLocator 只能拿来定位事件分发和普通使用，基础功能也还行
       *
       * 23.3.4
       * 又去看了一下，有人升级了 LaunchX：https://github.com/eleme/lancet/issues/69，但仍然无法集成进来
       */
      val codeLocatorVersion = "2.0.0"
      // CodeLocator 问题比较多，会疯狂抛被抓的异常出来，加上目前高级功能无法使用，所以暂时先注释
//      "debugImplementation"("com.bytedance.tools.codelocator:codelocator-core:$codeLocatorVersion")
      // 下面这个是高级功能，目前无法正常使用，如果需要使用基础功能的话，取消上面这个注释即可
//      "debugImplementation"("com.bytedance.tools.codelocator:codelocator-lancet-all:$codeLocatorVersion")
    }
  }
}