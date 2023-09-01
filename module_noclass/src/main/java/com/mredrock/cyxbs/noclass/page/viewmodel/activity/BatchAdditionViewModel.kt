package com.mredrock.cyxbs.noclass.page.viewmodel.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.NoClassBatchResponseInfo
import com.mredrock.cyxbs.noclass.bean.NoclassGroupId
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/19
 * @Description:
 *
 */
class BatchAdditionViewModel : BaseViewModel() {
    /**
     * 传入信息数据的检查结果
     */
    val infoCheckResult: LiveData<NoClassBatchResponseInfo> get() = _infoCheckResult
    private val _infoCheckResult = MutableLiveData<NoClassBatchResponseInfo>()

    /**
     * 分组是否新建成功，返回-1为创建失败，其它为成功
     */
    val isCreateSuccess: LiveData<NoclassGroupId> get() = _isCreateSuccess
    private val _isCreateSuccess = MutableLiveData<NoclassGroupId>()

    /**
     * 被选中的重名学生列表, 列表元素类型为 “学号-姓名”对
     */
    val selectedSameNameStudents: LiveData<List<Pair<String, String>>> get() = _selectedSameNameStudents
    private val _selectedSameNameStudents = MutableLiveData<List<Pair<String, String>>>()


    /**
     * 已经检查好的要进行批量添加的学生列表, 列表元素类型为 “学号-姓名”对
     */
    val batchAdditionStudents: LiveData<List<Pair<String, String>>> get() = _batchAdditionStudents
    private val _batchAdditionStudents = MutableLiveData<List<Pair<String, String>>>()

    /**
     * 获取传入信息的检查结果
     */
    fun getCheckUploadInfoResult(content: List<String>) {
        NoClassRepository
            .checkUploadInfo(content)
            .mapOrInterceptException {
                toast("查询失败,假数据测试中,可用真实学号测试") // 请求异常
                Log.d("DataTest","批量查询失败，可利用真实学号测试后续功能，自行到拦截器处添加")
                // 给_checkResult传一些假数据测试
                val falseData = NoClassBatchResponseInfo(
                    isWrong = false,
                    errList = listOf(
//                        "张三",
//                        "田所浩二",
//                        "孙笑川",
//                        "Ikun",
//                        "大碗宽面"
                    ),
                    normal = listOf(
                        NoClassBatchResponseInfo.Normal("2022121391","张三"),
                        NoClassBatchResponseInfo.Normal("2022121392","李四"),
                        NoClassBatchResponseInfo.Normal("2021121393","王五")
                    ),
                    repeat = listOf(
                        NoClassBatchResponseInfo.Student("2021121393","黎明(假学号，会失败)","大数据管理与应用","114514"),
                        NoClassBatchResponseInfo.Student("2021121394","黎明(假学号，会失败)","大数据管理与应用","114514"),
                        NoClassBatchResponseInfo.Student("2021121395","李明(假学号，会失败)","大数据管理与应用","114514"),
                        NoClassBatchResponseInfo.Student("2021121396","李明(假学号，会失败)","大数据管理与应用","114514"),
                    )
                )
                _infoCheckResult.postValue(falseData)

            }
            .safeSubscribeBy {
                _infoCheckResult.postValue(it)
            }
    }

    /**
     * 新建分组（上传分组）
     */
    fun postNoclassGroup(
        name: String,
        stuNums: String
    ) {
        NoClassRepository
            .postNoclassGroup(name, stuNums)
            .mapOrInterceptException {
                toast("网络异常") // 请求异常
            }.doOnError {
                _isCreateSuccess.postValue(NoclassGroupId(-1))
                Log.e("ListGroupError", it.toString())
            }.safeSubscribeBy {
                _isCreateSuccess.postValue(it)
            }
    }

    /**
     * 设置被选中的重名学生list
     * @param listData
     */
    fun setSelectedStudents(listData: List<Pair<String, String>>) {
        _selectedSameNameStudents.value = listData
    }

    /**
     * 添加已经检查好的学生list
     * @param listData
     */
    fun addPreparedStudents(listData: List<Pair<String, String>>) {
        _batchAdditionStudents.apply {
            value = if (value.isNullOrEmpty()) {
                listData
            } else {
                value!!.plus(listData)
            }
        }
    }

    /**
     * 替换已经检查好的学生list
     * @param listData
     */
    fun setPreparedStudents(listData: List<Pair<String, String>>) {
        _batchAdditionStudents.value = listData
    }

}