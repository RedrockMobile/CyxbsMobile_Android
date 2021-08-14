package com.mredrock.cyxbs.course.event

/**
 * 此事件用于表示[android.support.v4.widget.SwipeRefreshLayout]是显示refresh圈还是取消。
 *
 * @param isRefresh true: 表示显示刷新的圆圈
 * false：表示取消
 *
 * Created by anriku on 2018/9/17.
 */
class RefreshEvent(val isRefresh: Boolean)