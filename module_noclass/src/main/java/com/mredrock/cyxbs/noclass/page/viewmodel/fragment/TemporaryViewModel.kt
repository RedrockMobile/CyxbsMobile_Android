package com.mredrock.cyxbs.noclass.page.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.Cls
import com.mredrock.cyxbs.noclass.bean.GroupDetail
import com.mredrock.cyxbs.noclass.bean.NoClassTemporarySearch
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

class TemporaryViewModel : BaseViewModel(){

    private val _searchAll = MutableLiveData<NoClassTemporarySearch>()
    val searchAll get() = _searchAll

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
//                                name = "喵喵",
//                                id = "2022241392"
//                            ),
//                            Student(
//                                classNum = "04082203",
//                                gender = "女",
//                                grade = "24",
//                                major = "大数据管理与应用",
//                                name = "喵喵",
//                                id = "2021251392"
//                            )
                        ),"大数据实验班"),
                        group = GroupDetail("5555", listOf(
                            Student(
                                classNum = "04082201",
                                gender = "男",
                                grade = "22",
                                major = "大数据管理与应用",
                                name = "张琳",
                                id = "2022211293"
                            ),
                            Student(
                                classNum = "04082202",
                                gender = "男",
                                grade = "22",
                                major = "大数据管理与应用",
                                name = "周博",
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

}