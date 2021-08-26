package com.mredrock.cyxbs.mine.page.feedback.edit.presenter

import android.content.Intent
import android.net.Uri
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import com.mredrock.cyxbs.mine.page.feedback.adapter.rv.RvBinder

/**
 * @Date : 2021/8/23   21:00
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
interface FeedbackEditContract {
    interface IPresenter: CompoundButton.OnCheckedChangeListener{
        fun dealPic(data: Intent?)
        fun removePic(i: Int)
        fun getBinderList(
            uri: List<Uri>,
            contentListener: (View, Int) -> Unit,
            iconListener: (View, Int) -> Unit,
            clickListener: (View, Int) -> Unit
        ): MutableList<RvBinder<*>>
    }

    interface IVM{
        fun setEditDesNum(value:Int)
        fun setEditTitleNum(value: Int)
        fun setUris(value: List<Uri>)
    }
}