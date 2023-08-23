package com.mredrock.cyxbs.noclass.page.viewmodel.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository


/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.viewmodel
 * @ClassName:      NoClassViewModel
 * @Author:         Yan
 * @CreateDate:     2022年08月11日 16:11:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */


class NoClassViewModel : BaseViewModel() {

    /**
     * 获取分组数据。但是接收者却在固定分组,这是由于批量添加在activity中，却要传递给固定分组的fragment中
     */
    //获得所有分组
    val groupList: LiveData<List<NoClassGroup>> get() = _groupList
    private val _groupList = MutableLiveData<List<NoClassGroup>>()

    /**
     * 课表界面每一项的人名数据
     */
    val busyNameList: LiveData<List<String>> get() = _busyNameList
    private val _busyNameList = MutableLiveData<List<String>>()

    /**
     * 仅获得索引之后的list
     */
    fun removeBusyName(index: Int) {
        if (busyNameList.value == null) return
        if (index < busyNameList.value!!.size) {
            Log.d("lx", "removeBusyName: index=${index}")
            val list = busyNameList.value!!.subList(index, busyNameList.value!!.size)
            _busyNameList.value = list
        }
    }

    /**
     * 首次设置list
     */
    fun setBusyNameList(busyNameList: List<String>) {
        _busyNameList.value = busyNameList
    }

    /**
     * 获得所有分组
     */
    fun getAllGroup() {
        NoClassRepository
            .getNoclassGroupDetail()
            .mapOrInterceptException {
                Log.e("ListNoclassApiError", it.toString())
                _groupList.postValue(
                    listOf(
                        NoClassGroup(
                            "1",
                            false,
                            listOf(
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
                            "分组最长十个字"
                        ),
                        NoClassGroup(
                            "2",
                            true,
                            listOf(
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
                            "真的是太棒了"
                        ),
                        NoClassGroup(
                            "3",
                            false,
                            listOf(
                                Student(
                                    classNum = "04082201",
                                    gender = "男",
                                    grade = "21",
                                    major = "数据科学与大数据技术",
                                    "喵喵",
                                    id = "2022231392"
                                ),
                                Student(
                                    classNum = "04082203",
                                    gender = "女",
                                    grade = "23",
                                    major = "大数据管理与应用",
                                    "喵喵",
                                    id = "2021241392"
                                )
                            ),
                            "站至终章"
                        ),
                        NoClassGroup(
                            "4",
                            false,
                            listOf(
                                Student(
                                    classNum = "04082201",
                                    gender = "男",
                                    grade = "21",
                                    major = "数据科学与大数据技术",
                                    "喵喵",
                                    id = "2022231392"
                                ),
                                Student(
                                    classNum = "04082203",
                                    gender = "女",
                                    grade = "23",
                                    major = "大数据管理与应用",
                                    "喵喵",
                                    id = "2021241392"
                                )
                            ),
                            "一二三四五六七八九十"
                        ),
                        NoClassGroup(
                            "5",
                            false,
                            listOf(
                                Student(
                                    classNum = "04082201",
                                    gender = "男",
                                    grade = "21",
                                    major = "数据科学与大数据技术",
                                    "喵喵",
                                    id = "2022231392"
                                ),
                                Student(
                                    classNum = "04082203",
                                    gender = "女",
                                    grade = "23",
                                    major = "大数据管理与应用",
                                    "喵喵",
                                    id = "2021241392"
                                )
                            ),
                            "amazing"
                        ),
                        NoClassGroup(
                            "6",
                            false,
                            listOf(
                                Student(
                                    classNum = "04082201",
                                    gender = "男",
                                    grade = "21",
                                    major = "数据科学与大数据技术",
                                    "喵喵",
                                    id = "2022231392"
                                ),
                                Student(
                                    classNum = "04082203",
                                    gender = "女",
                                    grade = "23",
                                    major = "大数据管理与应用",
                                    "喵喵",
                                    id = "2021241392"
                                )
                            ),
                            "孤注一掷"
                        ),
                        NoClassGroup(
                            "7",
                            false,
                            listOf(
                                Student(
                                    classNum = "04082201",
                                    gender = "男",
                                    grade = "21",
                                    major = "数据科学与大数据技术",
                                    "喵喵",
                                    id = "2022231392"
                                ),
                                Student(
                                    classNum = "04082203",
                                    gender = "女",
                                    grade = "23",
                                    major = "大数据管理与应用",
                                    "喵喵",
                                    id = "2021241392"
                                )
                            ),
                            "海阔天空"
                        ),
                        NoClassGroup(
                            "8",
                            false,
                            listOf(
                                Student(
                                    classNum = "04082201",
                                    gender = "男",
                                    grade = "21",
                                    major = "数据科学与大数据技术",
                                    "喵喵",
                                    id = "2022231392"
                                ),
                                Student(
                                    classNum = "04082203",
                                    gender = "女",
                                    grade = "23",
                                    major = "大数据管理与应用",
                                    "喵喵",
                                    id = "2021241392"
                                )
                            ),
                            "飞流"
                        ),
                        NoClassGroup(
                            "9",
                            false,
                            listOf(
//                                Student(
//                                    classNum = "04082201",
//                                    gender = "男",
//                                    grade = "21",
//                                    major = "数据科学与大数据技术",
//                                    "喵喵",
//                                    id = "2022231392"
//                                ),
//                                Student(
//                                    classNum = "04082203",
//                                    gender = "女",
//                                    grade = "23",
//                                    major = "大数据管理与应用",
//                                    "喵喵",
//                                    id = "2021241392"
//                                )
                            ),
                            "成长的道路纵有千般"
                        )
                    )
                )
            }.doOnError {
                Log.e("ListNoclassApiError", it.toString())
            }.safeSubscribeBy {
                _groupList.postValue(it)
            }
    }
}