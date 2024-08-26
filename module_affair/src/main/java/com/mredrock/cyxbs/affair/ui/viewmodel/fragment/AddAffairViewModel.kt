package com.mredrock.cyxbs.affair.ui.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.affair.bean.Todo
import com.mredrock.cyxbs.affair.bean.TodoListPushWrapper
import com.mredrock.cyxbs.affair.model.AffairRepository
import com.mredrock.cyxbs.affair.net.AffairApiService
import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.config.config.SchoolCalendar
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.util.Calendar

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

  fun addTodo(todo: Todo) {
    val pushWrapper = TodoListPushWrapper(
      listOf(todo),
      getLastSyncTime()
    )
    AffairRepository.addTodo(pushWrapper)
      .safeSubscribeBy {
        it.data.syncTime.apply {
          val date = SchoolCalendar.getFirstMonDayOfTerm()
          val year = date?.get(Calendar.YEAR)
          val month = date?.get(Calendar.MONTH)?.plus(1)
          val day = date?.get(Calendar.DAY_OF_MONTH)
          "$year-$month-$day".toast()
          setLastSyncTime(this)
        }
      }
  }
  /**
   * 得到和设置本地最后同步的时间戳
   */
  private fun getLastSyncTime(): Long =
    appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)

  private fun setLastSyncTime(syncTime: Long) {
    appContext.getSp("todo").edit().apply {
      putLong("TODO_LAST_SYNC_TIME", syncTime)
      commit()
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