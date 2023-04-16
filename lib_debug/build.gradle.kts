plugins {
  id("module-manager")
}


dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiInit()
dependApiCrash()
dependApiAccount()

dependencies {
  
  // 依赖 LeakCanary，检查内存泄漏 https://github.com/square/leakcanary
  implementation("com.squareup.leakcanary:leakcanary-android:2.10")
  
  /**
   * 很牛逼的检测工具，debug 模式下摇一摇手机或者按三次手机中间顶部区域触发
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
   * 2、pandora-plugin 插件使用了会在 gradle 8.0 移除的 transform API，我的建议是你们 fork 下仓库，
   *   然后改了发一个 jitpack 依赖（发这个依赖很简单，不需要账号）
   * 3、Pandora 已停止维护，可以使用 doKit 进行代替
   */
  implementation("com.github.whataa:pandora:androidx_v2.1.0")
  
}

dependencies {
  /**
   * 比 Pandora 更牛逼的检测工具 DoKit
   *
   * 官网文档：https://xingyun.xiaojukeji.com/docs/dokit/#/androidGuide
   * github 仓库：https://github.com/didi/DoKit
   *
   * todo 截止 23年3/3，doKit 没有适配高版本的 gradle，所以插件引入会导致编译失败
   *  如果后面 doKit 适配高版本了，麻烦改下 doKit 版本
   *  issue: https://github.com/didi/DoKit/issues/1073
   *
   * 经过多次尝试后，我总结一下问题：
   * doKit 底层使用了 booster 框架，但是用的低版本 4.0.0，没有适配 AGP 7.3，会报找不到 getGlobalScope() 异常，
   * booster 框架是适配了 AGP 7.3 的，所以我直接依赖高版本的 booster 4.15.0，这时又报找不到 getMergedManifests() 方法，
   *
   * getGlobalScope() 找不到是 AGP 升级后移除了
   * getMergedManifests() 找不到是 booster 升级后把返回值改了 https://github.com/didi/booster/issues/330
   * ......
   *
   * todo 23/4/9: 因为编译太慢 + Pandora 更好用，所以注释
   */
//  implementation("io.github.didi.dokit:dokitx:${libs.versions.doKit.version.get()}")
  
  // 不知道 DoKit 干了什么，会导致前面的 dependAutoService() 中写的 compileOnly 失效
  // 只能单独依赖进来（除了 lib_debug 外其他模块不建议这样做）
  implementation(AutoService.autoService)
}
