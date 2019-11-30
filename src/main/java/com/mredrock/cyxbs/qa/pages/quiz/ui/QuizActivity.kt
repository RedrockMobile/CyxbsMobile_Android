package com.mredrock.cyxbs.qa.pages.quiz.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.mredrock.cyxbs.common.config.QA_QUIZ
import com.mredrock.cyxbs.common.event.DraftEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.quiz.QuizViewModel
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.RewardSetDialog
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.TagsEditDialog
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.TimePickDialog
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.utils.CHOOSE_PHOTO_REQUEST
import com.mredrock.cyxbs.qa.utils.getMaxLength
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import kotlinx.android.synthetic.main.qa_activity_quiz.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivityForResult
import top.limuyang2.photolibrary.activity.LPhotoPickerActivity

//todo 这个界面赶时间写得有点乱，记得优化一下

@Route(path = QA_QUIZ)
class QuizActivity : BaseViewModelActivity<QuizViewModel>() {
    companion object {
        const val MAX_SELECTABLE_IMAGE_COUNT = 6

        fun activityStart(fragment: Fragment, type: String, requestCode: Int) {
            fragment.startActivityForResult<QuizActivity>(requestCode, "type" to type)
        }
    }

    override val viewModelClass = QuizViewModel::class.java
    override val isFragmentActivity = false
    private var draftId = "-1"

    private val editTagDialog by lazy {
        TagsEditDialog(this).apply {
            onSureButtonClickedLister = { viewModel.setTag(it) }
        }
    }

    private val timePickDialog by lazy {
        TimePickDialog(this@QuizActivity).apply {
            onNextButtonClickListener = {
                val result = viewModel.setDisAppearTime(it)
                if (result) {
                    dismiss()
                    rewardSetDialog.show()
                }
            }
        }
    }

    private val rewardSetDialog by lazy {
        RewardSetDialog(this@QuizActivity, viewModel.myRewardCount).apply {
            onSubmitButtonClickListener = {
                if (viewModel.quiz(it)) {
                    dismiss()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_quiz)
        common_toolbar.init(getString(R.string.qa_quiz_title), listener = View.OnClickListener {
            if (edt_quiz_title.text.isNullOrEmpty() && edt_quiz_content.text.isNullOrEmpty()) {
                finish()
                return@OnClickListener
            }
            saveDraft()
            finish()
        })
        initImageAddView()
        viewModel.getMyReward() //优先初始化积分，避免用户等待

        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                if (draftId != "-1") {
                    viewModel.deleteDraft(draftId)
                }
                val data = Intent()
                data.putExtra("type", viewModel.type)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }



    private inline fun createTextWatcher(crossinline listener: (Editable) -> Unit) = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            listener.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    private fun initImageAddView() {
        nine_grid_view.addView(createImageView(BitmapFactory.decodeResource(resources, R.drawable.qa_ic_quiz_grid_add_img)))
        nine_grid_view.setOnItemClickListener { _, index ->
            if (index == nine_grid_view.childCount - 1) {
                this@QuizActivity.selectImageFromAlbum(MAX_SELECTABLE_IMAGE_COUNT, viewModel.imageLiveData.value)
            } else {
                ViewImageActivity.activityStartForResult(this@QuizActivity, viewModel.tryEditImg(index)
                        ?: return@setOnItemClickListener)
            }
        }

        viewModel.imageLiveData.observe { selectedImageFiles ->
            selectedImageFiles ?: return@observe
            viewModel.resetInvalid()
            //对view进行复用
            for (i in 0 until nine_grid_view.childCount - 1) {
                val view = nine_grid_view.getChildAt(i)
                if (i == nine_grid_view.childCount - 1) {
                    //保留添加图标
                    break
                } else if (i >= selectedImageFiles.size) {
                    //移除多出来的view
                    nine_grid_view.removeView(view)
                    continue
                }
                val bitmap = BitmapFactory.decodeFile(selectedImageFiles[i])
                if (bitmap != null) {
                    (view as ImageView).setImageBitmap(bitmap)
                    viewModel.checkInvalid(false)
                } else viewModel.checkInvalid(true)

            }
            //补充缺少的view
            selectedImageFiles.asSequence()
                    .filterIndexed { index, _ -> index >= nine_grid_view.childCount - 1 }
                    .forEach {
                        val bitmap = BitmapFactory.decodeFile(it)
                        if (bitmap != null) {
                            nine_grid_view.addView(createImageView(bitmap), nine_grid_view.childCount - 1)
                            viewModel.checkInvalid(false)
                        } else viewModel.checkInvalid(true)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.qa_quiz_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.qa_quiz_next) {
            val result = viewModel.submitTitleAndContent(edt_quiz_title.text.toString(), edt_quiz_content.text.toString())
            if (result) {
                timePickDialog.show()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getViewModelFactory(): QuizViewModel.Factory {
        return if (intent.getStringExtra("type") == null) {
            QuizViewModel.Factory(getString(R.string.qa_quiz_dialog_type_study))
        } else {
            QuizViewModel.Factory(intent.getStringExtra("type"))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (edt_quiz_content.text.isNullOrEmpty() && edt_quiz_title.text.isNullOrEmpty()) {
                return super.onKeyDown(keyCode, event)
            }
            LogUtils.d("cchanges", "on key back")
            saveDraft()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun saveDraft() {
        if (draftId == "-1") {
            viewModel.addItemToDraft(edt_quiz_title.text.toString(), edt_quiz_content.text.toString())
        } else {
            viewModel.updateDraftItem(edt_quiz_title.text.toString(), edt_quiz_content.text.toString(), draftId)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun loadDraft(event: DraftEvent) {
        if (intent.getStringExtra("type") != null) {
            EventBus.getDefault().removeStickyEvent(event)
            return
        }
        val question = Gson().fromJson(event.jsonString, Question::class.java)
        edt_quiz_title.setText(question.title)
        edt_quiz_content.setText(question.description)
        viewModel.type = question.kind
        if (question.tags.isNotEmpty()) viewModel.setTag(question.tags)
        if (question.photoThumbnailSrc != null) {
            val list = question.photoThumbnailSrc.split(",").toMutableList()
            if (list[0] != "") {
                viewModel.setImageList(arrayListOf<String>().apply { addAll(list) })
            }
        }
        draftId = event.selfId
    }
}
