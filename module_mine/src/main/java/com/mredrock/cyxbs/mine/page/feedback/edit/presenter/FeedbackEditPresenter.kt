package com.mredrock.cyxbs.mine.page.feedback.edit.presenter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.mredrock.cyxbs.mine.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.edit.viewmodel.FeedbackEditViewModel
import org.w3c.dom.Text

/**
 * @Date : 2021/8/23   20:59
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackEditPresenter: BasePresenter<FeedbackEditViewModel>(), FeedbackEditContract.IPresenter {

    /**
     * 初始化数据
     */
    override fun fetch() {
        vm?.setEditDesNum(0)
        vm?.setEditTitleNum(12)
    }

    /**
     * 内容详情的监听器
     */
    inner class DesTextWatcher:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            vm?.setEditDesNum(p1+p3)
        }
        override fun afterTextChanged(p0: Editable?) {}
    }

    /**
     * 标题的监听器
     */
    inner class TitleTextWatcher:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            vm?.setEditTitleNum(12-p1-p3)
        }
        override fun afterTextChanged(p0: Editable?) {}
    }
}