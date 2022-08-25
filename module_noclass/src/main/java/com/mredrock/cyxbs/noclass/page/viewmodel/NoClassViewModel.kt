package com.mredrock.cyxbs.noclass.page.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.mapOrCatchApiException
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.net.NoclassApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


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

    init {
        getNoclassGroupDetail()
    }

    /**
     * 没课约所有分组详情界面的 Livedata
     */
    val groupDetail : LiveData<List<NoclassGroup>> get() = _groupDetail
    private val _groupDetail = MutableLiveData<List<NoclassGroup>>()

    /**
     * 获得全部分组数据
     */
    fun getNoclassGroupDetail(){
        NoclassApiService
            .INSTANCE
            .getGroupAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrCatchApiException {
                Log.e("ListNoclassApiError",it.toString())
            }.doOnError {
                Log.e("ListNoclass",it.toString())
                _groupDetail.postValue(emptyList())
            }.safeSubscribeBy {
                _groupDetail.postValue(it)
                Log.e("ListNoclass",it.toString())
            }
    }


}