package com.mredrock.cyxbs.noclass.page.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.bean.NoClassTemporarySearch
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.bean.toSpareTime
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository
import io.reactivex.rxjava3.core.Observable

class TemporaryViewModel : BaseViewModel(){

    private val _searchAll = MutableLiveData<NoClassTemporarySearch>()
    val searchAll get() = _searchAll

    /**
     * 没课时段
     */
    val noclassData : LiveData<HashMap<Int, NoClassSpareTime>> get() = _noclassData
    private val _noclassData : MutableLiveData<HashMap<Int, NoClassSpareTime>> = MutableLiveData()

    //临时分组页面搜索全部
    fun getSearchAllResult(content : String){
        NoClassRepository.searchAll(content)
            .mapOrInterceptException {

            }.safeSubscribeBy {
                _searchAll.postValue(it)
            }
    }

    fun getLessons(stuNumList: List<String>,members : List<Student>){
        val studentsLessons =  mutableMapOf<Int,List<ILessonService.Lesson>>()
        Observable.fromIterable(stuNumList)
            .flatMap {
                ILessonService::class.impl
                    .getStuLesson(it)
                    .toObservable()
            }
            .safeSubscribeBy (
                onNext = {
                    //将课程对应学号的索引 对应的studentsLessons设置为it  []里面是获取学号对应传入list的索引的
                    studentsLessons[stuNumList.indexOf(it[0].stuNum)] = it
                },
                onComplete = {
                    //将new的studentsLessons变成空闲时间对象
                    _noclassData.postValue(studentsLessons.toSpareTime().apply {
                        val mMap = hashMapOf<String,String>()
                        members.forEach {
                            //学号和姓名的映射表
                            mMap[it.id] = it.name
                        }
                        forEach {
                            it.value.mIdToNameMap = mMap
                        }
                    })
                },
                onError = {
                    toast("网络似乎开小差了~")
                }
            )
    }

}