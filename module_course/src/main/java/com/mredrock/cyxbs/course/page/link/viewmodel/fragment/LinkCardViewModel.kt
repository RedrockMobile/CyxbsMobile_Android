package com.mredrock.cyxbs.course.page.link.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/10 11:09
 */
class LinkCardViewModel : BaseViewModel() {
  
  private val _linkStudent = MutableLiveData<LinkStuEntity?>(null)
  val linkStudent: LiveData<LinkStuEntity?>
    get() = _linkStudent
  
  fun deleteLinkStudent(entity: LinkStuEntity) {
    LinkRepository.deleteLinkStudent(entity)
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy {
        if (it) {
          toast("取消关联成功")
          _linkStudent.value = null
        } else {
          toast("取消关联失败")
        }
      }
  }
  
  init {
    LinkRepository.observeLinkStudent()
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy {
        if (it.isNotNull()) {
          _linkStudent.value = it
        } else {
          _linkStudent.value = null
        }
      }
  }
}