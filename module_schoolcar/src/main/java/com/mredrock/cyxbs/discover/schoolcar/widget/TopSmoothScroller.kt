package com.mredrock.cyxbs.discover.schoolcar.widget

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

/**
 *@Author:SnowOwlet
 *@Date:2022/5/15 20:34
 *
 */
class TopSmoothScroller(context:Context): LinearSmoothScroller(context) {
  override fun getHorizontalSnapPreference(): Int {
    return SNAP_TO_START
  }

  override fun getVerticalSnapPreference(): Int {
    return SNAP_TO_START
  }
}