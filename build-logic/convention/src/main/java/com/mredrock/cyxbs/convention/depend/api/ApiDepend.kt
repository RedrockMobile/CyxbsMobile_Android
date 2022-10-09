package com.mredrock.cyxbs.convention.depend.api

import org.gradle.api.Project
import com.mredrock.cyxbs.convention.depend.api.utils.ApiDependUtils

/**
 * 为什么要把 api 模块单独写出来？
 *
 * 因为单模块调试时 ARouter 如果不把实现模块一起拿来编译，就会报空指针，
 * 但谁是 api 模块的实现模块是不能被定义的（虽然目前只有父模块是实现模块），
 * 所以为了单模块调试，需要统一 api 模块的依赖写法
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/30 17:52
 */
object ApiDepend {
  
  /*
  * 这里写 api 模块以及该 api 模块的实现模块
  * 写法如下：
  * val test = ":module_test:api_test" by parent add ":module_xxx"
  *                                    ↑    ↑     ↑        ↑
  * by:     通过 by 来连接实现模块的 path
  * parent: 如果父模块是实现模块，则使用该方式可直接添加
  * add:    用于连接多个实现模块，比如后面写的 module_xxx，就是 api_test 的另一个实现模块
  * */
  
  // 下面的顺序尽量根据模块的排序来写
  val account = ":lib_account:api_account" by parent
  val protocol = ":lib_protocol:api_protocol" by parent
  val update = ":lib_update:api_update" by parent
  val electricity = ":module_electricity:api_electricity" by parent
  val login = ":module_login:api_login" by parent
  val sport = ":module_sport:api_sport" by parent
  val store = ":module_store:api_store" by parent
  val todo = ":module_todo:api_todo" by parent
  val volunteer = ":module_volunteer:api_volunteer" by parent
  val widget = ":module_widget:api_widget" by parent
  val mine = ":module_mine:api_mine" by parent
  val course = ":module_course:api_course" by parent
  val affair = ":module_affair:api_affair" by parent
  
  private infix fun String.by(implPath: String): ApiDependUtils.IApiDependUtils = by { implPath }
  private infix fun String.by(implPath: String.() -> String): ApiDependUtils.IApiDependUtils {
    return ApiDependUtils(this)
      .by(implPath.invoke(this))
  }
  
  private val parent: String.() -> String
    get() = { substringBeforeLast(":") }
}

fun Project.dependApiAccount() {
  ApiDepend.account.dependApiOnly(this)
}

fun Project.dependApiProtocol() {
  ApiDepend.protocol.dependApiOnly(this)
}

fun Project.dependApiUpdate() {
  ApiDepend.update.dependApiOnly(this)
}

fun Project.dependApiElectricity() {
  ApiDepend.electricity.dependApiOnly(this)
}

fun Project.dependApiLogin() {
  ApiDepend.login.dependApiOnly(this)
}

fun Project.dependApiStore() {
  ApiDepend.store.dependApiOnly(this)
}

fun Project.dependApiTodo() {
  ApiDepend.todo.dependApiOnly(this)
}

fun Project.dependApiVolunteer() {
  ApiDepend.volunteer.dependApiOnly(this)
}

fun Project.dependApiWidget() {
  ApiDepend.widget.dependApiOnly(this)
}

fun Project.dependApiSport() {
  ApiDepend.sport.dependApiOnly(this)
}

fun Project.dependApiMine(){
  ApiDepend.mine.dependApiOnly(this)
}

fun Project.dependApiCourse(){
  ApiDepend.course.dependApiOnly(this)
}

fun Project.dependApiAffair(){
  ApiDepend.affair.dependApiOnly(this)
}