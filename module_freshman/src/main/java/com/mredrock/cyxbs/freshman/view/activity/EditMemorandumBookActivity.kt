package com.mredrock.cyxbs.freshman.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseActivity
import com.mredrock.cyxbs.freshman.interfaces.ParseBean
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEditMemorandumBookModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityEditMemorandumBookPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityEditMemorandumBookView
import com.mredrock.cyxbs.freshman.presenter.ActivityEditMemorandumBookPresenter
import com.mredrock.cyxbs.freshman.util.event.MemorandumBookItemSelectedEvent
import com.mredrock.cyxbs.freshman.util.event.MemorandumBookItemUnSelectedEvent
import com.mredrock.cyxbs.freshman.view.adapter.EditMemorandumBookAdapter
import kotlinx.android.synthetic.main.freshman_activity_edit_memorandum_book.*
import kotlinx.android.synthetic.main.freshman_toolbar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Create by yuanbing
 * on 2019/8/9
 */
class EditMemorandumBookActivity :
        BaseActivity<IActivityEditMemorandumBookView, IActivityEditMemorandumBookPresenter,
                IActivityEditMemorandumBookModel>(), IActivityEditMemorandumBookView {
    override val isFragmentActivity: Boolean
        get() = false
    private val mSelectedMemorandumBookItem = mutableListOf<String>()
    private lateinit var mAdapter: EditMemorandumBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_edit_memorandum_book)

        initToolbar()
        initView()

        presenter?.getMemorandumBook()
    }

    private fun initView() {
        rv_edit_memorandum_book.layoutManager = LinearLayoutManager(this)
        mAdapter = EditMemorandumBookAdapter(this)
        rv_edit_memorandum_book.adapter = mAdapter
    }

    private fun initToolbar() {
        freshman_toolbar.init(
                title = "",
                listener = null
        )
        freshman_toolbar_title.text = resources.getString(
                R.string.freshman_add_enrollment_requiremnets_edit)

        freshman_toolbar_text_right.text = resources.getString(
                R.string.freshman_edit_memorandum_book_delete)
        freshman_toolbar_text_right.setOnClickListener {
            presenter?.deleteMemorandumBook(mSelectedMemorandumBookItem)
            freshman_toolbar_text_right.text = resources.getString(
                    R.string.freshman_edit_memorandum_book_delete)
        }

        freshman_toolbar_text_left.text = resources.getString(
                R.string.freshman_edit_memorandum_book_cancel)
        freshman_toolbar_text_left.setOnClickListener {
            if (mSelectedMemorandumBookItem.isNotEmpty()) {
                mSelectedMemorandumBookItem.clear()
                freshman_toolbar_text_right.text = resources.getString(
                        R.string.freshman_edit_memorandum_book_delete)
                mAdapter.clearAllSelectedItem()
            } else {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun getViewToAttach() = this

    override fun createPresenter() = ActivityEditMemorandumBookPresenter()

    override fun deleteSuccess() {
        presenter?.getMemorandumBook()
        mSelectedMemorandumBookItem.clear()
    }

    override fun showMemorandumBook(memorandumBook: List<ParseBean>) {
        mAdapter.refreshData(memorandumBook)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressLint("SetTextI18n")
    fun itemSelected(event: MemorandumBookItemSelectedEvent) {
        mSelectedMemorandumBookItem.add(event.name)
        freshman_toolbar_text_right.text =
                "${resources.getString(R.string.freshman_edit_memorandum_book_delete)}(" +
                        "${mSelectedMemorandumBookItem.size})"
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressLint("SetTextI18n")
    fun itemUnSelected(event: MemorandumBookItemUnSelectedEvent) {
        mSelectedMemorandumBookItem.remove(event.name)
        var count = ""
        if (mSelectedMemorandumBookItem.isNotEmpty()) count = "(${mSelectedMemorandumBookItem.size})"
        freshman_toolbar_text_right.text =
                "${resources.getString(R.string.freshman_edit_memorandum_book_delete)}$count"
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}