package com.mredrock.cyxbs.mine.page.feedback.edit.presenter

import android.text.TextWatcher
import android.widget.CompoundButton

/**
 * @Date : 2021/8/23   21:00
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
interface FeedbackEditContract {
    interface IPresenter: CompoundButton.OnCheckedChangeListener{

    }

    interface IVM{
        fun setEditDesNum(value:Int)
        fun setEditTitleNum(value: Int)
    }
}