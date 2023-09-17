package com.mredrock.cyxbs.noclass.page.viewmodel.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
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
     * 获得所有分组
     */
    fun getAllGroup() {
        NoClassRepository
            .getNoclassGroupDetail()
            .mapOrInterceptException {}.safeSubscribeBy {
                _groupList.postValue(it)
            }
    }
}