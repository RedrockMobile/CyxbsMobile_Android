package com.mredrock.cyxbs.freshman.view.activity

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseActivity
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityAddMemorandumBookModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityAddMemorandumBookPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityAddMemorandumBookView
import com.mredrock.cyxbs.freshman.presenter.ActivityAddMemorandumBookPresenter
import kotlinx.android.synthetic.main.freshman_activity_add_memorandum_book.*
import kotlinx.android.synthetic.main.freshman_activity_add_memorandum_book.view.*
import kotlinx.android.synthetic.main.freshman_toolbar.*
import org.jetbrains.anko.px2sp
import org.jetbrains.anko.textColor

/**
 * Create by yuanbing
 * on 2019/8/9
 */
class AddMemorandumBookActivity : BaseActivity<IActivityAddMemorandumBookView, IActivityAddMemorandumBookPresenter, IActivityAddMemorandumBookModel>(), IActivityAddMemorandumBookView {
    override val isFragmentActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_add_memorandum_book)

        initToolbar()
    }

    private fun initToolbar() {
        freshman_toolbar.init(
                title = " ",
                listener = null
        )
        freshman_toolbar_title.text = resources.getString(R.string.freshman_add_memorandum_toolbar_title)

        freshman_toolbar_text_left.text = resources.getString(R.string.freshman_add_memorandum_book_toolbar_cancel)
        freshman_toolbar_text_left.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        freshman_toolbar_text_right.text = getString(R.string.freshman_add_memorandum_book_save)
        freshman_toolbar_text_right.setOnClickListener {
            val content = et_add_memorandum_book_input.text.toString()
            if (content.isNotBlank()) presenter?.saveMemorandumBook(content)
        }
    }

    override fun getViewToAttach() = this

    override fun createPresenter() = ActivityAddMemorandumBookPresenter()

    override fun saveSuccess() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}