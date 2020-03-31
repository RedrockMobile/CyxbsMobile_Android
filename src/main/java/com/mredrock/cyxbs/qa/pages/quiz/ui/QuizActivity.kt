package com.mredrock.cyxbs.qa.pages.quiz.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.mredrock.cyxbs.common.config.QA_QUIZ
import com.mredrock.cyxbs.common.event.QuestionDraftEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.quiz.QuizViewModel
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.RewardSetDialog
import com.mredrock.cyxbs.qa.ui.activity.ViewImageCropActivity
import com.mredrock.cyxbs.qa.ui.widget.CommonDialog
import com.mredrock.cyxbs.qa.utils.CHOOSE_PHOTO_REQUEST
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import com.mredrock.cyxbs.qa.utils.selected
import com.mredrock.cyxbs.qa.utils.unSelected
import kotlinx.android.synthetic.main.qa_activity_quiz.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivityForResult
import top.limuyang2.photolibrary.activity.LPhotoPickerActivity

@Route(path = QA_QUIZ)
class QuizActivity : BaseViewModelActivity<QuizViewModel>() {
    companion object {
        const val MAX_SELECTABLE_IMAGE_COUNT = 6
        const val NOT_DRAFT_ID = "-1"
        const val FIRST_QUIZ = "cyxbs_quiz_is_first_time"
        const val FIRST_QUIZ_SP_KEY = "isFirstTimeQuiz"
        const val QUIZ_TITLE_MAX = 12
        fun activityStart(fragment: Fragment, type: String, requestCode: Int) {
            fragment.startActivityForResult<QuizActivity>(requestCode, "type" to type)
        }
    }

    override val viewModelClass = QuizViewModel::class.java
    override val isFragmentActivity = false
    private var currentTypeIndex = 0
    private var draftId = NOT_DRAFT_ID
    private var questionType: String = ""
    private var isFirstQuiz: Boolean = true
    private val exitDialog by lazy { createExitDialog() }
    private val rewardNotEnoughDialog by lazy { createRewardNotEnoughDialog() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_quiz)
        isFirstQuiz = sharedPreferences(FIRST_QUIZ).getBoolean(FIRST_QUIZ_SP_KEY, true)
        initTypeSelector()
        initToolbar()
        initImageAddView()
        viewModel.getMyReward() //优先初始化积分，避免用户等待

        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                if (draftId != NOT_DRAFT_ID) {
                    viewModel.deleteDraft(draftId)
                }
                val data = Intent()
                data.putExtra("type", questionType)
                setResult(Activity.RESULT_OK, data)
                finish()
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }
        viewModel.finishActivityEvent.observeNotNull {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        edt_quiz_title.doAfterTextChanged {
            if (edt_quiz_title.text.toString().length == QUIZ_TITLE_MAX) {
                toast(R.string.qa_quiz_title_text_size_toast)
            }
        }
    }

    private fun initTypeSelector() {
        val tagSelector = findViewById<LinearLayout>(R.id.layout_quiz_tag)
        val childView = Array(tagSelector.childCount) { tagSelector.getChildAt(it) as TextView }
        if (childView.isNotEmpty()) {
            questionType = childView[currentTypeIndex].text.toString()
        }
        for ((index, i) in childView.withIndex()) {
            i.setOnClickListener { view ->
                childView[currentTypeIndex].unSelected()
                (view as TextView).selected()
                currentTypeIndex = index
                questionType = view.text.toString()
            }
        }
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener(View.OnClickListener {
            if (edt_quiz_title.text.isNullOrEmpty() && edt_quiz_content.text.isNullOrEmpty() && draftId == NOT_DRAFT_ID) {
                finish()
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
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
                viewModel.isAnonymous = questionType == getString(R.string.qa_quiz_select_type_anonymous)
                val result = viewModel.submitTitleAndContent(edt_quiz_title.text.toString(), edt_quiz_content.text.toString(), questionType)
                if (result) {
                    RewardSetDialog(this@QuizActivity, viewModel.myRewardCount, isFirstQuiz).apply {
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
        nine_grid_view.addView(ContextCompat.getDrawable(this, R.drawable.qa_quiz_add_picture_empty)?.let { createImageViewFromVector(it) })
        nine_grid_view.setOnItemClickListener { _, index ->
            if (index == nine_grid_view.childCount - 1) {
                this@QuizActivity.selectImageFromAlbum(MAX_SELECTABLE_IMAGE_COUNT, viewModel.imageLiveData.value)
            } else {
                ViewImageCropActivity.activityStartForResult(this@QuizActivity, viewModel.tryEditImg(index)
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
                    for (j in i until nine_grid_view.childCount - 1)
                        nine_grid_view.removeViewAt(i)
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
        if (resultCode == ViewImageCropActivity.DELETE_CODE && requestCode == ViewImageCropActivity.DEFAULT_RESULT_CODE) {
            viewModel.setImageList(viewModel.imageLiveData.value!!.apply {
                removeAt(viewModel.editingImgPos)
            })
        }
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            CHOOSE_PHOTO_REQUEST -> viewModel.setImageList(LPhotoPickerActivity.getSelectedPhotos(data))
            ViewImageCropActivity.DEFAULT_RESULT_CODE -> viewModel.setImageList(viewModel.imageLiveData.value!!.apply {
                set(viewModel.editingImgPos, data.getStringExtra(ViewImageCropActivity.EXTRA_NEW_PATH))
            })
        }
    }

    private fun createImageViewFromVector(drawable: Drawable) = ImageView(this).apply {
        scaleType = ImageView.ScaleType.CENTER
        background = ContextCompat.getDrawable(this@QuizActivity, R.drawable.qa_quiz_select_pic_empty_background)
        setImageDrawable(drawable)
    }

    private fun createImageView(bitmap: Bitmap) = ImageView(this).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageBitmap(bitmap)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitDialog.isShowing) {
                return super.onKeyDown(keyCode, event)
            }
            return if (edt_quiz_content.text.isNullOrEmpty() && edt_quiz_title.text.isNullOrEmpty() && draftId == NOT_DRAFT_ID) {
                super.onKeyDown(keyCode, event)
            } else {
                exitDialog.show()
                false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun saveDraft() {
        if (draftId == NOT_DRAFT_ID) {
            viewModel.addItemToDraft(edt_quiz_title.text.toString(), edt_quiz_content.text.toString(), questionType)
        } else {
            viewModel.updateDraftItem(edt_quiz_title.text.toString(), edt_quiz_content.text.toString(), draftId, questionType)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun loadDraft(event: QuestionDraftEvent) {
        if (intent.getStringExtra("type") != null) {
            EventBus.getDefault().removeStickyEvent(event)
            return
        }
        val json = String(Base64.decode(event.jsonString, Base64.DEFAULT))
        val question = Gson().fromJson(json, Question::class.java)
        edt_quiz_title.setText(question.title)
        edt_quiz_content.setText(question.description)
        val tagSelector = findViewById<LinearLayout>(R.id.layout_quiz_tag)
        val childView = Array(tagSelector.childCount) { tagSelector.getChildAt(it) as TextView }
        childView[currentTypeIndex].unSelected()
        when (question.kind) {
            getString(R.string.qa_quiz_select_type_study) -> currentTypeIndex = 0
            getString(R.string.qa_quiz_select_type_anonymous) -> currentTypeIndex = 1
            getString(R.string.qa_quiz_select_type_life) -> currentTypeIndex = 2
            getString(R.string.qa_quiz_select_type_others) -> currentTypeIndex = 3
        }
        childView[currentTypeIndex].selected()
        if (question.photoUrl.isNotEmpty()) {
            viewModel.setImageList(arrayListOf<String>().apply { addAll(question.photoUrl) })
        }
        draftId = event.selfId
    }

    private fun createExitDialog() = CommonDialog(this).apply {
        initView(icon = R.drawable.qa_ic_quiz_quit_edit
                , title = getString(R.string.qa_quiz_dialog_exit_text)
                , firstNotice = getString(R.string.qa_quiz_dialog_not_save_text)
                , secondNotice = null
                , buttonText = getString(R.string.qa_common_dialog_exit)
                , confirmListener = View.OnClickListener {
            saveDraft()
            dismiss()
        }
                , cancelListener = View.OnClickListener {
            dismiss()
        })
    }

    private fun createRewardNotEnoughDialog() = CommonDialog(this).apply {
        initView(icon = R.drawable.qa_ic_quiz_notice_reward_not_enough
                , title = getString(R.string.qa_quiz_reward_not_enough_text)
                , firstNotice = getString(R.string.qa_quiz_reward_more_text)
                , secondNotice = getString(R.string.qa_quiz_down_reward_get_more_text)
                , buttonText = getString(R.string.qa_quiz_dialog_sure)
                , confirmListener = View.OnClickListener {
            dismiss()
        }
                , cancelListener = null)
    }

    override fun onPause() {
        //防止内存泄漏
        exitDialog.dismiss()
        rewardNotEnoughDialog.dismiss()
        super.onPause()
    }
}
