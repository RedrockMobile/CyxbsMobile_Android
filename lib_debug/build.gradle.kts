import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.dependAutoService
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
  id("module-manager")
//  id("me.ele.lancet") // CodeLocator 所需要的插件
}

dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()
dependAutoService()

dependencies {
  
  // 依赖 LeakCanary，检查内存泄漏 https://github.com/square/leakcanary
  implementation("com.squareup.leakcanary:leakcanary-android:2.9.1")
  
  /**
   * 很牛逼的检测工具，debug 模式下摇一摇手机触发
   *
   * 支持功能：
   * 1、网络请求监听
   * 2、View 树查看（还可以随意移动 View 的位置）
   * 3、崩溃记录
   * 4、SP 文件查看
   * 5、Room 数据查看
   * 6、更多请看：https://www.wanandroid.com/blog/show/2526
   *
   * 注意：
   * 1、摇一摇手机后会出现一个小条，那个小条是可以左右滑动的滑动后有更多功能
   * 2、为了防止其他非开发人员使用，请在 lib_debug 中注册你的学号
   * 3、单模块调试时可以不用注册，直接摇一摇触发
   */
  implementation("com.github.whataa:pandora:androidx_v2.1.0")
  
  /**
   * 进字节的学长在用的代码调试工具，请先在插件送搜索下载 CodeLocator
   * 进字节的某位学长表示：如果没有这个插件，我连抖音代码的新需求都不知道从何入手
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
   */
  val codeLocatorVersion = "2.0.0"
//  implementation("com.bytedance.tools.codelocator:codelocator-core:$codeLocatorVersion")
  // 下面这个是高级功能，目前无法正常使用，如果需要使用基础功能的话，取消上面这个注释即可
//  implementation("com.bytedance.tools.codelocator:codelocator-lancet-all:$codeLocatorVersion")
  // CodeLocator 问题比较多，会疯狂抛被抓的异常出来，影响其他异常，加上目前高级功能无法使用，所以暂时先注释
}
