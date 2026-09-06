//
//  AppDelegate.swift
//  CyxbsMobile2019_iOS
//
//  Created by SSR on 2023/9/1.
//  Copyright © 2023 Redrock. All rights reserved.
//

import UIKit
import XBSBugly
import CyxbsApplicationsMultiplatform

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    // 应用程序启动时调用的方法
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        // 版本迁移会清空整个 UserDefaults，必须在写入网络环境前完成。
        // 提前初始化也避免首次登录才触发清理，导致旧原生模块丢失 baseURL。
        _ = CacheManager.shared
        // 网络环境必须先于 CMP 初始化，避免恢复登录态时旧原生模块读取不到 baseURL。
        setupAlicloudSDK() // 设置网络环境和阿里云SDK
        IOSAppKt.doInitApp(impl: KmpInterfaceImpl()) // Kotlin Multiplatform 工程初始化
        setupWindow() // 设置应用程序窗口
        XBSBugly.buglyInit() // 设置bugly

        return true
    }
    
    // 当应用程序从后台进入前台时调用的方法
    func applicationWillEnterForeground(_ application: UIApplication) {
        //检查token是否过期
//         RYLoginViewController.checkToken(rootVC: window?.rootViewController)
    }
    
    // 当应用程序进入后台时调用的方法
    func applicationDidEnterBackground(_ application: UIApplication) {
        setupEnd() // 设置结束操作
    }
    
    // 当应用程序终止时调用的方法
    func applicationWillTerminate(_ application: UIApplication) {
        setupEnd() // 设置结束操作
    }
}

// MARK: - 设置

extension AppDelegate {
    
    // 设置应用程序窗口
    func setupWindow() {
        let rootVC = IOSAppKt.MainViewController() // 使用 CMP 主页
        // 扩展到安全区域以下显示
        rootVC.edgesForExtendedLayout = .all
        rootVC.extendedLayoutIncludesOpaqueBars = true

        // 用 CustomNavigationController 包一层，让 CMP 主页仍可跳转到体育打卡等原生页面。
        // 走标准 push。CustomNavigationController 自带 isNavigationBarHidden = true，
        // 且修过 isNavigationBarHidden 下系统会禁用边缘右滑返回手势的问题（root VC 上拒绝触发，
        // 避免空 pop 崩溃），与原版 iOS 各 tab 内的导航行为保持一致。
        let nav = CustomNavigationController(rootViewController: rootVC)

        window = UIWindow()
        window?.rootViewController = nav
        window?.makeKeyAndVisible()
    }
    
    // 设置阿里云SDK
    func setupAlicloudSDK() {
        APIConfig.current.apply(APIConfig.current.environment)
        AliyunConfig.ip(byHost: APIConfig.current.environment.host)
    }
    
    // 设置结束操作
    func setupEnd() {
        UserDefaultsManager.shared.latestOpenApp = Date()
    }
}
