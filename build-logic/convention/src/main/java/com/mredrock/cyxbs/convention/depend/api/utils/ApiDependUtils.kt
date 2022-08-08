package com.mredrock.cyxbs.convention.depend.api.utils

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/1 18:41
 */
class ApiDependUtils(val apiProjectPath: String) {
  
  fun by(path: String): IApiDependUtils {
    list.add(path)
    return apiImplDependUtils
  }
  
  private val list = mutableListOf<String>()
  private val apiImplDependUtils = ApiImplDependUtils()
  
  init {
    _ApiWithImplMap[apiProjectPath] = apiImplDependUtils
  }
  
  private inner class ApiImplDependUtils: IApiDependUtils {
    
    val apiProjectPath: String
      get() = this@ApiDependUtils.apiProjectPath
    
    override fun add(path: String): IApiDependUtils {
      list.add(path)
      return this
    }
  
    override fun add(path: (String) -> String): IApiDependUtils {
      list.add(path.invoke(apiProjectPath))
      return this
    }
  
    override fun getImplPaths(): List<String> {
      return list
    }
  
    override fun dependApiOnly(project: Project) {
      if (list.isEmpty()) {
        throw RuntimeException("api 模块 $apiProjectPath 没得实现模块，你正确书写了吗？")
      }
      project.run {
        dependencies {
          "implementation"(project(apiProjectPath))
        }
      }
    }
  
    override fun dependApiImplOnly(project: Project, filter: (String) -> Boolean): List<String> {
      val dependList = mutableListOf<String>()
      project.run {
        dependencies {
          getImplPaths().forEach {
            if (it.isNotBlank() && filter.invoke(it)) {
              "implementation"(project(it))
              dependList.add(it)
            }
          }
        }
      }
      return dependList
    }
  }
  
  companion object {
    private val _ApiWithImplMap = HashMap<String, IApiDependUtils>()
    
    /**
     * api 模块的 path 与 实现模块的路径
     */
    val sApiWithImplMap: Map<String, IApiDependUtils>
      get() = _ApiWithImplMap
  }
  
  interface IApiDependUtils {
    infix fun add(path: String): IApiDependUtils
    infix fun add(path: (String) -> String): IApiDependUtils
    fun getImplPaths(): List<String>
    fun dependApiOnly(project: Project)
    fun dependApiImplOnly(project: Project, filter: (String) -> Boolean = { true }): List<String>
  }
}