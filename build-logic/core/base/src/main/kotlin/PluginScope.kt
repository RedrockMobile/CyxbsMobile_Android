import org.gradle.api.Project

/**
 *@author ZhiQiang Tu
 *@time 2022/10/10  16:53
 *@signature 后来啊,渐行渐远渐无书...
 *@mail  2623036785@qq.com
 */

//不污染Project作用域
//仅对BasePlugin暴露扩展
class PluginScope(delegate:Project) : Project by delegate
