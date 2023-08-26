package com.mredrock.cyxbs.noclass.page.viewmodel.fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.Cls
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
import com.mredrock.cyxbs.noclass.bean.NoClassTemporarySearch
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

class TemporaryViewModel : BaseViewModel() {

    private val _searchAll = MutableLiveData<ApiWrapper<NoClassTemporarySearch>>()
    val searchAll get() = _searchAll

    //临时分组页面搜索全部
    fun getSearchAllResult(content: String) {
        NoClassRepository.searchAll(content)
            .doOnError { Log.d("lx", "getSearchAllResult: $it")
                _searchAll.postValue(ApiWrapper(noClassTemporarySearch,10000,"success"))
            }
            .safeSubscribeBy {
                _searchAll.postValue(ApiWrapper(noClassTemporarySearch,10000,"success"))
            }
    }

    //下面是测试数据
    val noClassTemporarySearch = NoClassTemporarySearch(
        types = listOf(
//            "学生",
//                            "班级",
            "分组",
        ),
        students = listOf(
            Student(
                classNum = "04082201",
                gender = "男",
                grade = "21",
                major = "数据科学与大数据技术",
                "喵喵",
                id = "2022231392"
            ),
            Student(
                classNum = "04082202",
                gender = "女",
                grade = "23",
                major = "大数据管理与应用",
                "喵喵",
                id = "2021241392"
            )
        ),
        `class` = Cls(
            "04082201", listOf(
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
            ), "大数据实验班"
        ),
        group = NoClassGroup(
            "5555", members = listOf(
                Student(
                    classNum = "04082201",
                    gender = "男",
                    grade = "22",
                    major = "大数据管理与应用",
                    name = "张琳666",
                    id = "2022211293"
                ),
                Student(
                    classNum = "04082202",
                    gender = "男",
                    grade = "22",
                    major = "大数据管理与应用",
                    name = "周博帅哥真的是帅",
                    id = "2022211299"
                ),
                Student(
                    classNum = "04082202",
                    gender = "男",
                    grade = "22",
                    major = "大数据管理与应用",
                    name = "片落惊虹",
                    id = "2022211298"
                ),
                Student(
                    classNum = "04082202",
                    gender = "男",
                    grade = "22",
                    major = "大数据管理与应用",
                    name = "宛若游龙",
                    id = "2022211297"
                ),
                Student(
                    classNum = "04082202",
                    gender = "男",
                    grade = "22",
                    major = "大数据管理与应用",
                    name = "大卫天龙",
                    id = "2022211296"
                ),
                Student(
                    classNum = "04082202",
                    gender = "男",
                    grade = "22",
                    major = "大数据管理与应用",
                    name = "雷电法王",
                    id = "2022211295"
                ),

                Student(
                    classNum = "04082201",
                    gender = "男",
                    grade = "22",
                    major = "大数据管理与应用",
                    name = "大数据管理与应用一班",
                    id = "2022211294"
                ),
                Student(
                    classNum = "04082203",
                    gender = "男",
                    grade = "21",
                    major = "大数据管理与应用",
                    name = "夏洛特",
                    id = "2022211295"
                ),
            ), name = "喵喵", isTop = true
        )
    )


}