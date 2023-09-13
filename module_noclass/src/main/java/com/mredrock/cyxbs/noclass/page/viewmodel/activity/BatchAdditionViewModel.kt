package com.mredrock.cyxbs.noclass.page.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.NoClassBatchResponseInfo
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/19
 * @Description: 持有批量添加页面部分UI数据的viewModel
 *
 */
class BatchAdditionViewModel : BaseViewModel() {
    /**
     * 传入信息数据的检查结果
     */
    private val _infoCheckResult = MutableLiveData<NoClassBatchResponseInfo>()

    /**
     * 获取传入信息的检查结果 （事件）
     *
     * **tip**-该事件 与 点击获取检查结果的”行为“强关联
     */
    val getInfoCheckResult = _infoCheckResult.asShareFlow()


    /**
     * 被选中的重名学生列表, 列表元素类型为 “学号-姓名”对
     */
    private val _selectedSameNameStudents = MutableLiveData<List<Pair<String, String>>>()

    /**
     * 获取被选中的重名学生列表 （事件）
     *
     * **tip**-该事件 与 点击提交被选中的重名学生的”行为“强关联
     */
    val getSelectedSameNameStudents = _selectedSameNameStudents.asShareFlow()


    /**
     * 已经检查好的要进行批量添加的学生列表, 列表元素类型为 “学号-姓名”对
     */
    private val _batchAdditionStudents = MutableLiveData<List<Pair<String, String>>>()

    /**
     * 获取要进行批量添加的学生列表 （事件）
     *
     * **tip**-该事件 与 点击查询按钮按照既定流程完成相关操作的”行为“强关联
     */
    val getBatchAdditionStudents = _batchAdditionStudents.asShareFlow()

    /**
     * 获取传入信息的检查结果
     */
    fun getCheckUploadInfoResult(content: List<String>) {
        NoClassRepository
            .checkUploadInfo(content)
            .mapOrInterceptException {
                "服务器君没有理你~请检查网络连接".toast()

            }
            .safeSubscribeBy {
                _infoCheckResult.postValue(it)
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
     * 额外添加（在原有list基础上添加）已经检查好的学生list,后续可能会用上,暂作保留
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
     * 替换（提交）已经检查好的学生list
     * @param listData
     */
    fun setPreparedStudents(listData: List<Pair<String, String>>) {
        // 由于这里无法保证是在主线程提交的数据，故使用postValue
        _batchAdditionStudents.postValue(listData)
    }

}