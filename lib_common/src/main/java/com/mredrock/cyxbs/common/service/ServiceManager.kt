package com.mredrock.cyxbs.common.service

import com.alibaba.android.arouter.launcher.ARouter

/**
 * Created By jay68 on 2019/10/12.
 *
 * 对服务获取的封装，便于以后修改为其他依赖注入的框架（ARouter这方面性能稍弱），建议都通过该文件提供的方法获取服务，
 * 不采用@Autowired的方式，便于以后更换实现。<br>
 * 使用方法：<br>
 *     1. 在service包中创建对应的服务接口并继承IProvider接口，命名请加上代表接口的I前缀和Service后缀，例如IAccountService；
 *     2. 创建该接口的实现类，命名尽量只去掉I即可，然后加上路由注解，路由地址统一写到RoutingTable中，例如AccountService；
 *     3. 通过ServiceManager.getService(IXxxService::java.class)的方式获取实现类。
 */

object ServiceManager {
    /**
     * 通过类型搜索对应服务，建议优先使用此方式
     */
    fun <T> getService(serviceClass: Class<T>): T = ARouter.getInstance().navigation(serviceClass)

    /**
     * 通过Path搜索服务，当同一种类型的服务有多个实现时只能使用该方式获取
     *
     * @param servicePath 实现类的路由地址
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getService(servicePath: String) = ARouter.getInstance().build(servicePath).navigation() as T
}