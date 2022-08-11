package com.mredrock.cyxbs.convention.project

import com.mredrock.cyxbs.convention.depend.dependAndroidBase
import com.mredrock.cyxbs.convention.depend.dependBugly
import com.mredrock.cyxbs.convention.project.base.BaseLibraryProject
import org.gradle.api.Project

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/10
 * @Description:
 */
class LibCrash(project: Project) : BaseLibraryProject(project) {
    override fun initProject() {
        dependBugly()
        dependAndroidBase()
    }
}