package com.mredrock.cyxbs.mine.page.feedback.edit.presenter

import android.text.TextWatcher

/**
 * @Date : 2021/8/23   21:00
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
interface FeedbackEditContract {
    interface IPresenter{

    }

    interface IVM{
        fun setEditDesNum(value:Int)
        fun setEditTitleNum(value: Int)
    }
}