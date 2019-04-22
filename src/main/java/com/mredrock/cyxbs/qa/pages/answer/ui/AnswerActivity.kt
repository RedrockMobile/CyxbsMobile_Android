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
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.mredrock.cyxbs.common.config.QA_ANSWER
import com.mredrock.cyxbs.common.event.DraftEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Content
import com.mredrock.cyxbs.qa.pages.answer.viewmodel.AnswerViewModel
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.utils.CHOOSE_PHOTO_REQUEST
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import kotlinx.android.synthetic.main.qa_activity_answer.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivityForResult
import top.limuyang2.photolibrary.activity.LPhotoPickerActivity

@Route(path = QA_ANSWER)
class AnswerActivity : BaseViewModelActivity<AnswerViewModel>() {
    companion object {
        const val MAX_SELECTABLE_IMAGE_COUNT = 6

        fun activityStart(activity: FragmentActivity, qid: String, requestCode: Int) {
            activity.startActivityForResult<AnswerActivity>(requestCode, "qid" to qid)
        }
    }

    override val viewModelClass = AnswerViewModel::class.java

    override val isFragmentActivity = false

    private var draftId = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_answer)
        common_toolbar.init(getString(R.string.qa_answer_question_title))
        initView()
        initImageAddView()
        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                if (draftId != "-1") {
                    viewModel.deleteDraft(draftId)
//                    EventBus.getDefault().post(FinishActivityEvent())
                }
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
            } else {
                ViewImageActivity.activityStartForResult(this@AnswerActivity, viewModel.tryEditImg(index)
                        ?: return@setOnItemClickListener)
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
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }

        when (requestCode) {
            CHOOSE_PHOTO_REQUEST -> viewModel.setImageList(LPhotoPickerActivity.getSelectedPhotos(data))
            ViewImageActivity.DEFAULT_RESULT_CODE -> viewModel.setImageList(viewModel.imageLiveData.value!!.apply {
                set(viewModel.editingImgPos, data.getStringExtra(ViewImageActivity.EXTRA_NEW_PATH))
            })
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

    override fun getViewModelFactory(): AnswerViewModel.Factory {
        return if (intent.getStringExtra("qid") == null) {
            AnswerViewModel.Factory("-1")
        } else {
            AnswerViewModel.Factory(intent.getStringExtra("qid"))
        }
    }

    override fun onPause() {
        super.onPause()
        if (draftId == "-1") {
            viewModel.addItemToDraft(edt_answer_content.text.toString())
        } else {
            viewModel.updateDraft(edt_answer_content.text.toString(), draftId)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun loadDraft(event: DraftEvent) {
        if (intent.getStringExtra("qid") != null) {
            EventBus.getDefault().removeStickyEvent(event)
            return
        }
        val content = Gson().fromJson(event.jsonString, Content::class.java)
        edt_answer_content.setText(content.title)
        viewModel.qid = event.targetId
        draftId = event.selfId
    }
}
