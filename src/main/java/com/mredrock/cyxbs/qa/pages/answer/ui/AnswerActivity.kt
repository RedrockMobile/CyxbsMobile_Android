package com.mredrock.cyxbs.qa.pages.answer.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.mredrock.cyxbs.common.component.multi_image_selector.MultiImageSelectorActivity
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.answer.viewmodel.AnswerViewModel
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import kotlinx.android.synthetic.main.qa_activity_answer.*
import org.jetbrains.anko.startActivityForResult

class AnswerActivity : BaseViewModelActivity<AnswerViewModel>() {
    companion object {
        const val MAX_SELECTABLE_IMAGE_COUNT = 6

        fun activityStart(activity: FragmentActivity, qid: String, requestCode: Int) {
            activity.startActivityForResult<AnswerActivity>(requestCode, "qid" to qid)
        }
    }

    override val viewModelClass = AnswerViewModel::class.java

    override val isFragmentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_answer)
        common_toolbar.init(getString(R.string.qa_answer_question_title))
        initView()
        initImageAddView()
        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        tv_answer_content_counter.text = "300"
        edt_answer_content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                tv_answer_content_counter.text = "${300 - s.length}"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
    }

    private fun initImageAddView() {
        nine_grid_view.addView(createImageView(BitmapFactory.decodeResource(resources, R.drawable.qa_ic_quiz_grid_add_img)))
        nine_grid_view.setOnItemClickListener { _, index ->
            if (index == nine_grid_view.childCount - 1) {
                this@AnswerActivity.selectImageFromAlbum(AnswerActivity.MAX_SELECTABLE_IMAGE_COUNT, viewModel.imageLiveData.value)
            }
        }

        viewModel.imageLiveData.observeNotNull { selectedImageFiles ->
            //对view进行复用
            for (i in 0 until nine_grid_view.childCount - 1) {
                val view = nine_grid_view.getChildAt(i)
                if (i >= selectedImageFiles.size) {
                    //移除多出来的view
                    nine_grid_view.removeView(view)
                    continue
                }
                (view as ImageView).setImageBitmap(BitmapFactory.decodeFile(selectedImageFiles[i]))
            }
            //补充缺少的view
            selectedImageFiles.asSequence()
                    .filterIndexed { index, _ -> index >= nine_grid_view.childCount - 1 }
                    .forEach {
                        nine_grid_view.addView(createImageView(BitmapFactory.decodeFile(it)),
                                nine_grid_view.childCount - 1)
                    }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == MultiImageSelectorActivity.CHOOSE_REQUEST) {
            viewModel.setImageList(data?.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT))
        }
    }

    private fun createImageView(bitmap: Bitmap) = ImageView(this).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageBitmap(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.qa_answer_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.qa_answer_submit_answer) {
            viewModel.submitAnswer(edt_answer_content.text.toString())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getViewModelFactory() = AnswerViewModel.Factory(intent.getStringExtra("qid"))
}
