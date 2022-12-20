package com.mredrock.cyxbs.lib.utils.utils.get

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 得到继承时写在父类 <> 中的泛型 Class 的工具类
 *
 * **你想找到的泛型必须在继承时明确, 不能为 T 这种不明确的类型, 如下所示**
 * ```
 * 简单的三个类
 * Class Test、Class Son、Class Parent
 *
 * 正确用法: Class Son : Parent<String>
 *     这样写就可以得到 Class<String>
 *
 * 错误用法: Class Son<T: Test> : Parent<T>
 *     这样写得到的是 T, 没错, 真的是 T, 这是无法得到传入时 T 的具体类型的
 * ```
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/22 20:11
 */
object GenericityUtils {
  
  /**
   * @param X 子类传入的具体类型
   * @param Y 父类中泛型上界
   */
  inline fun <X : Y, reified Y> getGenericClass(javaClass: Class<*>): Class<X> {
    return getGenericClass(javaClass, Y::class.java)
  }
  
  /**
   * @param X 子类传入的具体类型
   * @param Y 父类中泛型上界
   */
  @Suppress("UNCHECKED_CAST")
  fun <X : Y, Y> getGenericClass(javaClass: Class<*>, YClass: Class<Y>): Class<X> {
    getGenericTypesFromSuperClass(javaClass).forEach {
      val clazz = getClassFromType(it)
      if (clazz != null && YClass.isAssignableFrom(clazz)) {
        return clazz as Class<X>
      }
    }
    throw IllegalStateException("你父类的泛型为: ${javaClass.genericSuperclass}, 其中不存在 ${YClass.simpleName}")
  }
  
  /**
   * 根据索引拿到泛型的 Class
   *
   * **Note:** 不是很建议写死 index
   */
  @Suppress("UNCHECKED_CAST")
  fun <X> getGenericClass(javaClass: Class<*>, index: Int): Class<X> {
    return getClassFromType(getGenericTypesFromSuperClass(javaClass)[index])!! as Class<X>
  }
  
  private fun getGenericTypesFromSuperClass(javaClass: Class<*>): Array<Type> {
    val genericSuperclass = javaClass.genericSuperclass // 得到继承的父类填入的泛型（必须是具体的类型，不能是 T 这种东西）
    if (genericSuperclass is ParameterizedType) {
      return genericSuperclass.actualTypeArguments
    } else throw IllegalStateException("${javaClass.name} 不存在泛型！")
  }
  
  private fun getClassFromType(type: Type): Class<*>? {
    if (type is Class<*>) {
      return type
    } else if (type is ParameterizedType) { // 泛型中有泛型时并不为 Class<*>
      val rawType = type.rawType // 这时 rawType 一定是 Class<*>
      if (rawType is Class<*>) {
        return rawType
      }
    }
    return null
  }
}