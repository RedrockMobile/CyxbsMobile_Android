package com.mredrock.cyxbs.qa.pages.quiz.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.QA_QUIZ
import com.mredrock.cyxbs.common.event.DraftEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.RewardSetDialog
import com.mredrock.cyxbs.qa.pages.quiz.QuizViewModel
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.utils.CHOOSE_PHOTO_REQUEST
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import kotlinx.android.synthetic.main.qa_activity_quiz.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_dialog_notice_exit_quiz.*
import kotlinx.android.synthetic.main.qa_dialog_notice_reward_not_enough.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.textColor
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
    private val exitDialog by lazy { createExitDialog() }
    val rewardNotEnoughDialog by lazy { createRewardNotEnoughDialog() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_quiz)
        initToolbar()
        initTagSelector()
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

    private fun initTagSelector() {
        var currentTag = 0
        val tagSelector = findViewById<LinearLayout>(R.id.layout_quiz_tag)
        val childView = Array(tagSelector.childCount) { tagSelector.getChildAt(it) as TextView }
        if (childView.isNotEmpty()) {
            viewModel.setTag(childView[currentTag].text.toString())
        }
        for ((index, i) in childView.withIndex()) {
            i.setOnClickListener { view ->
                childView[currentTag].apply {
                    textColor = ContextCompat.getColor(this@QuizActivity, R.color.qa_quiz_select_type_text_color)
                    background = ContextCompat.getDrawable(this@QuizActivity, R.drawable.qa_selector_quiz_type_default_select)

                }

                (view as TextView).apply {
                    textColor = ContextCompat.getColor(this@QuizActivity, R.color.qa_quiz_selected_type_text_color)
                    background = ContextCompat.getDrawable(this@QuizActivity, R.drawable.qa_selector_quiz_type_default_selected)
                }
                currentTag = index
                viewModel.setTag(view.text.toString())
            }
        }
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener(View.OnClickListener {
            if (edt_quiz_title.text.isNullOrEmpty() && edt_quiz_content.text.isNullOrEmpty()) {
                finish()
                return@OnClickListener
            }
            exitDialog.show()
        })
        qa_tv_toolbar_title.text = getString(R.string.qa_quiz_toolbar_title_text)
        qa_ib_toolbar_more.gone()
        qa_tv_toolbar_right.apply {
            visible()
            text = getString(R.string.qa_quiz_dialog_next)
            setOnClickListener {
                val result = viewModel.submitTitleAndContent(edt_quiz_title.text.toString(), edt_quiz_content.text.toString())
                if (result) {
                    RewardSetDialog(this@QuizActivity, viewModel.myRewardCount).apply {
                        onSubmitButtonClickListener = { time: String, reward: Int ->
                            if (viewModel.setDisAppearTime(time)) {
                                if (viewModel.quiz(reward)) {
                                    dismiss()
                                } else {
                                    rewardNotEnoughDialog.show()
                                }
                            }
                        }
                    }.show()
                }
            }
        }
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

    override fun getViewModelFactory(): QuizViewModel.Factory {
        return if (intent.getStringExtra("type") == null) {
            QuizViewModel.Factory(getString(R.string.qa_quiz_dialog_type_study))
        } else {
            QuizViewModel.Factory(intent.getStringExtra("type"))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitDialog.isShowing) {
                return super.onKeyDown(keyCode, event)
            }
            return if (edt_quiz_content.text.isNullOrEmpty() && edt_quiz_title.text.isNullOrEmpty()) {
                super.onKeyDown(keyCode, event)
            } else {
                exitDialog.show()
                false
            }
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

    private fun createExitDialog() = Dialog(this).apply {
        setContentView(R.layout.qa_dialog_notice_exit_quiz)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_save_and_exit.setOnClickListener {
            saveDraft()
            finish()
        }
        iv_cancel_exit.setOnClickListener { dismiss() }

    }

    private fun createRewardNotEnoughDialog() = Dialog(this).apply {
        setContentView(R.layout.qa_dialog_notice_reward_not_enough)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_quiz_reward_not_enough_confirm.setOnClickListener { dismiss() }
    }

    override fun onPause() {
        //防止内存泄漏
        exitDialog.dismiss()
        rewardNotEnoughDialog.dismiss()
        super.onPause()
    }
}
