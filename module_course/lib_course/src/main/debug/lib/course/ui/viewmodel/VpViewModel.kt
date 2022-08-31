package lib.course.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import lib.course.data.LessonData
import lib.course.network.LessonApiService

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:40
 */
class VpViewModel : BaseViewModel() {
  
  val selfLessons: SharedFlow<Map<Int, List<LessonData>>> get() = _selfLessons
  private val _selfLessons = MutableSharedFlow<Map<Int, List<LessonData>>>(replay = 1)
  
  init {
    LessonApiService::class.api
      .getStuLesson("2020214988")
      .subscribeOn(Schedulers.io())
      .doOnSuccess { SchoolCalendarUtil.updateFirstCalendar(it.nowWeek) }
      .map { it.toLessonData() }
      .map { it.groupBy { data -> data.week } }
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy {
        viewModelScope.launch {
          _selfLessons.emit(it)
        }
      }
  }
}