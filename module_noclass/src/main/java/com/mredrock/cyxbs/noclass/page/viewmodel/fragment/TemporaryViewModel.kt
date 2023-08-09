package com.mredrock.cyxbs.noclass.page.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.noclass.bean.Cls
import com.mredrock.cyxbs.noclass.bean.GroupDetail
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
                toast("网络异常")
                //下面是测试数据
                val noClassTemporarySearch = NoClassTemporarySearch(
                    status = 10000,
                    info = "success",
                    data = NoClassTemporarySearch.Data(
                        isExist = true,
                        types = listOf(
//                            "学生",
//                            "班级",
                            "分组"
                        ),
                        students = listOf(
//                            Student(
//                                classNum = "04082201",
//                                gender = "男",
//                                grade = "21",
//                                major = "数据科学与大数据技术",
//                                "喵喵",
//                                id = "2022231392"
//                            ),
//                            Student(
//                                classNum = "04082202",
//                                gender = "女",
//                                grade = "23",
//                                major = "大数据管理与应用",
//                                "喵喵",
//                                id = "2021241392"
//                            )
                        ),
                        `class` = Cls("04082201", listOf(
//                            Student(
//                                classNum = "04082204",
//                                gender = "男",
//                                grade = "23",
//                                major = "数据科学与大数据技术",
//                                "喵喵",
//                                id = "2022241392"
//                            ),
//                            Student(
//                                classNum = "04082203",
//                                gender = "女",
//                                grade = "24",
//                                major = "大数据管理与应用",
//                                "喵喵",
//                                id = "2021251392"
//                            )
                        ),"大数据实验班"),
                        group = GroupDetail("5555", listOf(
                            Student(
                                classNum = "04082201",
                                gender = "男",
                                grade = "21",
                                major = "数据科学与大数据技术",
                                "汪汪",
                                id = "2022211293"
                            ),
                            Student(
                                classNum = "04082202",
                                gender = "女",
                                grade = "23",
                                major = "大数据管理与应用",
                                "呱呱",
                                id = "2022211292"
                            )
                        ),"喵喵",true)
                    )
                )
                _searchAll.postValue(noClassTemporarySearch)
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