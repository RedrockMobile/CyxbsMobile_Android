package com.mredrock.cyxbs.qa.pages.quiz.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.config.QA_QUIZ
import com.mredrock.cyxbs.common.event.QuestionDraftEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.R.drawable.*
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.CircleLabelAdapter
import com.mredrock.cyxbs.qa.pages.quiz.QuizViewModel
import com.mredrock.cyxbs.qa.ui.activity.ViewImageCropActivity
import com.mredrock.cyxbs.qa.ui.widget.DraftDialog
import com.mredrock.cyxbs.qa.ui.widget.RectangleView
import com.mredrock.cyxbs.qa.utils.CHOOSE_PHOTO_REQUEST
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import kotlinx.android.synthetic.main.qa_activity_quiz.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.limuyang2.photolibrary.LPhotoHelper

@Route(path = QA_QUIZ)
class QuizActivity : BaseViewModelActivity<QuizViewModel>(), EventBusLifecycleSubscriber {
    companion object {
        const val MAX_SELECTABLE_IMAGE_COUNT = 8
        const val NOT_DRAFT_ID = "-1"
        const val FIRST_QUIZ = "cyxbs_quiz_is_first_time"
        const val FIRST_QUIZ_SP_KEY = "isFirstTimeQuiz"
        fun activityStart(fragment: Fragment, type: String, requestCode: Int) {
            fragment.startActivityForResult<QuizActivity>(requestCode, "type" to type)
        }

    }

    override val isFragmentActivity = false
    private var draftId = NOT_DRAFT_ID
    private var dynamicType: String = ""
    private var isFirstQuiz: Boolean = true
    private val exitDialog by lazy { createExitDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_quiz)
        val titles = resources.getStringArray(R.array.qa_quiz_reward_explain_title)
        val contents = resources.getStringArray(R.array.qa_quiz_reward_explain_content)
        isFirstQuiz = sharedPreferences(FIRST_QUIZ).getBoolean(FIRST_QUIZ_SP_KEY, true)
        initToolbar()
        initImageAddView()
        initEditListener()
        val circleLabelData = ArrayList<String>()
        circleLabelData.add("#校园周边")
        circleLabelData.add("#海底捞")
        circleLabelData.add("#学习")
        circleLabelData.add("#运动")
        circleLabelData.add("#兴趣")
        circleLabelData.add("#问答")
        circleLabelData.add("#其他")
        val circleLabelAdapter = CircleLabelAdapter(context, mutableListOf()).apply {
            onLabelClickListener = {
                dynamicType = it
            }
        }
        val flexBoxManager = FlexboxLayoutManager(context)
        flexBoxManager.flexWrap = FlexWrap.WRAP
        rv_label_list.layoutManager = flexBoxManager
        rv_label_list.adapter = circleLabelAdapter
        circleLabelAdapter.setList(circleLabelData)
        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                if (draftId != NOT_DRAFT_ID) {
                    viewModel.deleteDraft(draftId)
                }
                val data = Intent()
                data.putExtra("type", dynamicType)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
        viewModel.finishActivityEvent.observeNotNull {
            finish()
        }

        tv_is_checked_to_show_origin_image.apply {
            isChecked = false
            setOnSingleClickListener {
                isChecked = !isChecked
            }
        }
    }

    private fun initEditListener() {
        edt_quiz_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
            ) {
            }

            override fun onTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
            ) {
                if (!TextUtils.isEmpty(charSequence) && charSequence.length > 2) {
                    qa_tv_toolbar_right.setBackgroundResource(qa_shape_send_dynamic_btn_blue_background)
                    tv_edit_num.text = charSequence.length.toString() + "/500"

                } else {
                    qa_tv_toolbar_right.setBackgroundResource(qa_shape_send_dynamic_btn_grey_background)
                    tv_edit_num.text = charSequence.length.toString() + "/500"
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }


    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener(View.OnClickListener {
            if (edt_quiz_content.text.isNullOrEmpty() && draftId == NOT_DRAFT_ID) {
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
                val result = viewModel.submitTitleAndContent(dynamicType, edt_quiz_content.text.toString())
                if (result) {

                }
            }
        }
    }

    private fun initImageAddView() {
        nine_grid_view.addView(ContextCompat.getDrawable(this, qa_ic_add_images)?.let { createImageViewFromVector(it) })
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
                if (selectedImageFiles[i].isNotEmpty()) {
                    (view as ImageView).setImageURI(Uri.parse(selectedImageFiles[i]))
                    viewModel.checkInvalid(false)
                } else viewModel.checkInvalid(true)

            }
            //补充缺少的view
            selectedImageFiles.asSequence()
                    .filterIndexed { index, _ -> index >= nine_grid_view.childCount - 1 }
                    .forEach {
                        if (it.isNotEmpty()) {
                            nine_grid_view.addView(createImageView(Uri.parse(it)), nine_grid_view.childCount - 1)
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
            CHOOSE_PHOTO_REQUEST -> {
                val imageListUri = ArrayList((LPhotoHelper.getSelectedPhotos(data)).map {
                    it.toString()
                })
                val imageListAbsolutePath = ArrayList<String>()
                imageListUri.forEach { imageListAbsolutePath.add(Uri.parse(it).getAbsolutePath(this)) }
                viewModel.setImageList(imageListAbsolutePath)
            }
            ViewImageCropActivity.DEFAULT_RESULT_CODE -> viewModel.setImageList(viewModel.imageLiveData.value!!.apply {
                set(viewModel.editingImgPos, data.getStringExtra(ViewImageCropActivity.EXTRA_NEW_PATH))
            })
        }
    }

    private fun createImageViewFromVector(drawable: Drawable) = RectangleView(this).apply {
        scaleType = ImageView.ScaleType.CENTER
        background = ContextCompat.getDrawable(this@QuizActivity, R.drawable.qa_shape_quiz_select_pic_empty_background)
        setImageDrawable(drawable)
    }

    private fun createImageView(uri: Uri) = RectangleView(this).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageURI(uri)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitDialog.isShowing) {
                return super.onKeyDown(keyCode, event)
            }
            return if (edt_quiz_content.text.isNullOrEmpty() && draftId == NOT_DRAFT_ID) {
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
            viewModel.addItemToDraft(edt_quiz_content.text.toString(), dynamicType)
        } else {
            viewModel.updateDraftItem(edt_quiz_content.text.toString(), draftId, dynamicType)
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
        edt_quiz_content.setText(question.description)
        draftId = event.selfId
        if (!question.photoUrl.isNullOrEmpty()) {
            viewModel.setImageList(arrayListOf<String>().apply { addAll(question.photoUrl) })
        }
    }

    private fun createExitDialog() = DraftDialog(this).apply {
        initView(title = getString(R.string.qa_quiz_dialog_exit_text), saveText = "保存", noSaveText = "不保存", cancelText = "取消", saveListener = View.OnClickListener {
            saveDraft()
            dismiss()
        }, noSaveListener = View.OnClickListener {
            noSaveDraft()
            dismiss()
        }, cancelListener = View.OnClickListener {
            dismiss()
        })
    }

    private fun noSaveDraft() {
        finish()
    }

    override fun onPause() {
        //防止内存泄漏
        exitDialog.dismiss()
        super.onPause()
    }
}
