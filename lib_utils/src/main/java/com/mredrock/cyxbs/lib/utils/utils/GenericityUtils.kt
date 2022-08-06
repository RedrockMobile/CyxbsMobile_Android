package com.mredrock.cyxbs.lib.utils.utils

import java.lang.RuntimeException
import java.lang.reflect.ParameterizedType

/**
 * 得到父类中泛型 Class 的工具类
 *
 * 注意：只能得到你继承时写在父类 <> 中的泛型
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/22 20:11
 */
object GenericityUtils {
  
  /**
   * 得到父类中的泛型 Class 对象
   * K 为泛型
   * E 为泛型的父类
   *
   * **你想找到的泛型必须在继承时明确, 不能为 T 这种不明确的类型, 如下所示**
   * ```
   *
   * 简单的三个类
   * Class Test、Class Son、Class Parent
   *
   * 正确用法: Class Son : Parent<String>
   *     这样写就可以得到 Class<String>
   *
   * 错误用法: Class Son<T: Test> : Parent<T>
   *     这样写得到的是 T, 没错, 真的是 T, 这是无法得到传入时 T 的具体类型的
   * ```
   * @param javaClass 传入调用该方法的类
   */
  @Suppress("UNCHECKED_CAST")
  inline fun <K : E, reified E> getGenericClassFromSuperClass(javaClass: Class<*>): Class<K> {
    val genericSuperclass = javaClass.genericSuperclass // 得到继承的父类填入的泛型（必须是具体的类型，不能是 T 这种东西）
    if (genericSuperclass is ParameterizedType) {
      val typeArguments = genericSuperclass.actualTypeArguments
      for (type in typeArguments) {
        if (type is Class<*> && E::class.java.isAssignableFrom(type)) {
          return type as Class<K>
        } else if (type is ParameterizedType) { // 泛型中有泛型时并不为 Class<*>
          val rawType = type.rawType // 这时 rawType 一定是 Class<*>
          if (rawType is Class<*> && E::class.java.isAssignableFrom(rawType)) {
            return rawType as Class<K>
          }
        }
      }
    }
    throw RuntimeException("你父类的泛型为: $genericSuperclass, 其中不存在 ${E::class.java.simpleName}")
  }
}