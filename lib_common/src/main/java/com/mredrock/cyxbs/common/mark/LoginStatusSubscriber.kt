package com.mredrock.cyxbs.common.mark

/**
 * @author Jovines
 * create 2020-09-24 10:01 AM
 * description: 登陆状态初始化操作订阅
 *              继承base的activity和fragment都可以实现该接口
 *              然后实现对应的方法即可对不同模式的初始化作出不同行为
 *
 *              activity是在AppCompatActivity.setContentView(View,LayoutParams)之后执行的
 *              稍微解释一下为什么要在super.setContentView(View,LayoutParams)之后执行
 *              因为这里必须在布局设置上去之后且OnCreate中执行才是最佳的（onStart不合适），所以这是最佳选择
 *
 *              fragment是在super.onViewCreated(view, savedInstanceState)执行时执行的
 *
 *              使用java8的接口默认实现对应的kotlin版本，见到默认实现不要觉得奇怪
 */
interface LoginStatusSubscriber {

    /**
     * 可以在这里处理相关初始化操作
     */
    fun initOnLoginMode() {}
    fun initOnTouristMode() {}

    /**
     * 可以在这里处理对应模式的页面摧毁的一些操作
     */
    fun destroyOnLoginMode() {}
    fun destroyOnTouristMode() {}
}