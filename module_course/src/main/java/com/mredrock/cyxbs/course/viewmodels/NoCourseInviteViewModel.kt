package com.mredrock.cyxbs.course.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.network.CourseApiService
import io.reactivex.Observable

/**
 * Created by anriku on 2018/10/2.
 */
class NoCourseInviteViewModel(private val mStuNumList: List<String> = mutableListOf(),
                              val nameList: List<String> = mutableListOf()) : ViewModel() {


    //用于存储所有学生的课程。key代表学生在mStuNumList中的index，value代表对应的学生的课程。
    val studentsCourseMap = ObservableField<Map<Int, List<Course>>>()
    private val mCourseApiService = ApiGenerator.getApiService(CourseApiService::class.java)

    /**
     * 此方法用于获取各个学生的学期课表
     */
    fun getCourses(onFinish: (() -> Unit)? = null) {
        val studentsCourseMap = mutableMapOf<Int, List<Course>>()
        //依次请求所有的学生的课程。
        Observable.fromIterable(mStuNumList)
                .flatMap {
                    mCourseApiService.getCourse(it)
                }
                .setSchedulers()
                .errorHandler()
                .safeSubscribeBy(onNext = {
                    it.data?.let { courses ->
                        studentsCourseMap[mStuNumList.indexOf(it.stuNum)] = courses
                    }
                }, onComplete = {
                    this.studentsCourseMap.set(studentsCourseMap)
                    onFinish?.invoke()
                }, onError = {
                    onFinish?.invoke()
                })
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val mStuNumList: List<String>, private val mNameList: List<String>) :
            ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NoCourseInviteViewModel(mStuNumList, mNameList) as T
        }
    }
}