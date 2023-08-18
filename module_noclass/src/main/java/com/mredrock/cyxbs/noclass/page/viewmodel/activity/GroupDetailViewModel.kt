package com.mredrock.cyxbs.noclass.page.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.Cls
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
import com.mredrock.cyxbs.noclass.bean.NoClassTemporarySearch
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.viewmodel
 * @ClassName:      GroupDetailViewModel
 * @Author:         Yan
 * @CreateDate:     2022年08月25日 04:35:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    具体分组页面ViewModel
 */
class GroupDetailViewModel : BaseViewModel() {

    private val _searchAll = MutableLiveData<NoClassTemporarySearch>()
    val searchAll get() = _searchAll

    /**
     * 组内删除成员 ，前面是要删除的学生的学号，后面是是否删除成功
     */
    private val _deleteMembers = MutableLiveData<Pair<String,Boolean>>()
    val deleteMembers get() = _deleteMembers

    //临时分组页面搜索全部
    fun getSearchAllResult(content: String) {
        NoClassRepository.searchAll(content)
            .mapOrInterceptException {
                toast("网络异常")
                //下面是测试数据
                val noClassTemporarySearch = NoClassTemporarySearch(

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
                            ),
                            Student(
                                classNum = "04082201",
                                gender = "男",
                                grade = "22",
                                major = "大数据管理与应用",
                                name = "威威",
                                id = "2022211294"
                            ),
                            Student(
                                classNum = "04082203",
                                gender = "男",
                                grade = "21",
                                major = "大数据管理与应用",
                                name = "陈晨",
                                id = "2022211295"
                            ),
                        ), name = "喵喵", isTop = true
                    )
                )
                _searchAll.postValue(noClassTemporarySearch)
            }.safeSubscribeBy {
                _searchAll.postValue(it)
            }
    }

    /**
     * 删除成员
     */
    fun deleteMembers(groupId: String, deleteStudent: Student) {
        // 目前组内管理一次仅删除一个，如日后有需要，可调用concatSet方法拼接，类似addMembers
        val deleteStuId = deleteStudent.id
        NoClassRepository.deleteNoclassGroupMember(groupId, deleteStuId).doOnError {
            toast("网络异常")
        }.safeSubscribeBy {
            _deleteMembers.postValue(deleteStuId to it.isSuccess())
        }
    }

}