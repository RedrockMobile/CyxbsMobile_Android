package com.mredrock.cyxbs.affair.ui.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.affair.net.AffairApiService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrThrowApiException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/10 10:31
 */
class AddAffairViewModel : BaseViewModel() {

  private val _titleCandidates = MutableLiveData<List<String>>()
  val titleCandidates: LiveData<List<String>>
    get() = _titleCandidates

  fun refresh() {

  }

  fun addAffair() {

  }

  fun updateAffair() {

  }

  init {
    AffairApiService.INSTANCE.getTitleCandidate()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .mapOrThrowApiException()
      .safeSubscribeBy {
        _titleCandidates.value = it
      }
  }
}