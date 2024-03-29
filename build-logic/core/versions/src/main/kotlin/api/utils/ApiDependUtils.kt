package api.utils

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
  
  // 不带实现类
  fun byNoImpl(): IApiDependUtils {
    isNoImpl = true
    return apiImplDependUtils
  }
  
  private val list = mutableListOf<String>()
  private val apiImplDependUtils = ApiImplDependUtils()
  private var isNoImpl = false
  
  init {
    _ApiWithImplMap[apiProjectPath] = apiImplDependUtils
  }
  
  // 专门用来保存实现模块路径的类
  private inner class ApiImplDependUtils: IApiDependUtils {
    
    val apiProjectPath: String
      get() = this@ApiDependUtils.apiProjectPath
    
    override fun add(path: String): IApiDependUtils {
      if (!isNoImpl) list.add(path)
      return this
    }
  
    override fun add(path: (String) -> String): IApiDependUtils {
      if (!isNoImpl) list.add(path.invoke(apiProjectPath))
      return this
    }
  
    override fun getImplPaths(): List<String> {
      return list
    }
  
    override fun dependApiOnly(project: Project) {
      if (list.isEmpty() && !isNoImpl) {
        throw RuntimeException("api 模块 $apiProjectPath 没得实现模块，你正确书写了吗？")
      }
      project.run {
        dependencies {
          "implementation"(project(apiProjectPath))
        }
      }
    }
  }
  
  companion object {
    private val _ApiWithImplMap = HashMap<String, IApiDependUtils>()
    
    /**
     * api 模块的 path 与 实现模块的路径
     */
    val apiWithImplMap: Map<String, IApiDependUtils>
      get() = _ApiWithImplMap
  }
  
  interface IApiDependUtils {
    /**
     * 添加实现模块
     */
    infix fun add(path: String): IApiDependUtils
  
    /**
     * 添加实现模块
     */
    infix fun add(path: (String) -> String): IApiDependUtils
  
    /**
     * 得到所有实现模块的 path
     */
    fun getImplPaths(): List<String>
  
    /**
     * 只依赖 api 模块
     */
    fun dependApiOnly(project: Project)
  }
}