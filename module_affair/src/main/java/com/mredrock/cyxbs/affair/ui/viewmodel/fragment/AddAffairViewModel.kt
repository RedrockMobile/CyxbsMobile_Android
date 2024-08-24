package com.mredrock.cyxbs.affair.ui.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.affair.bean.TodoListPushWrapper
import com.mredrock.cyxbs.affair.model.AffairRepository
import com.mredrock.cyxbs.affair.net.AffairApiService
import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch

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

  fun addAffair(
    time: Int,
    title: String,
    content: String,
    atWhatTime: List<AffairEntity.AtWhatTime>,
  ) {
    AffairRepository.addAffair(time, title, content, atWhatTime)
      .safeSubscribeBy {
        "添加成功".toast()
      }
  }

  fun addTodo(pushWrapper: TodoListPushWrapper) {
    AffairRepository.addTodo(pushWrapper)
      .safeSubscribeBy {
        "加入待办".toast()
      }
  }

  init {
    AffairApiService.INSTANCE.getTitleCandidate()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .mapOrInterceptException {
        // 网络请求失败就发送这个默认显示
        emitter.onSuccess(listOf("自习", "值班", "考试", "英语", "开会", "作业", "补课", "实验", "复习", "学习"))
      }.safeSubscribeBy {
        _titleCandidates.value = it
      }
  }
}