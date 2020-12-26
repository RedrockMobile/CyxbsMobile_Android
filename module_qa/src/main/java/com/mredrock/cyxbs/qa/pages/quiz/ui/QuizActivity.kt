package com.mredrock.cyxbs.qa.pages.quiz.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
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
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.mredrock.cyxbs.common.config.QA_QUIZ
import com.mredrock.cyxbs.common.event.DynamicDraftEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.R.drawable.*
import com.mredrock.cyxbs.qa.beannew.Question
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.pages.quiz.QuizViewModel
import com.mredrock.cyxbs.qa.ui.activity.ViewImageCropActivity
import com.mredrock.cyxbs.qa.ui.widget.DraftDialog
import com.mredrock.cyxbs.qa.ui.widget.RectangleView
import com.mredrock.cyxbs.qa.utils.CHOOSE_PHOTO_REQUEST
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import kotlinx.android.synthetic.main.qa_activity_question_search.*
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

    private var progressDialog: ProgressDialog? = null
    override val isFragmentActivity = false
    private var draftId = NOT_DRAFT_ID
    private var dynamicType: String = ""
    private var isFirstQuiz: Boolean = true
    private val exitDialog by lazy { createExitDialog() }
    private var isComment = ""
    private var commentContent = ""
    private var replyId = ""
    private var postId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_quiz)
        isFirstQuiz = sharedPreferences(FIRST_QUIZ).getBoolean(FIRST_QUIZ_SP_KEY, true)
        if (!intent.getStringExtra("isComment").isNullOrEmpty()) {
            isComment = intent.getStringExtra("isComment")
            if (!intent.getStringExtra("commentContent").isNullOrEmpty())
                commentContent = intent.getStringExtra("commentContent")
            if (!intent.getStringExtra("replyId").isNullOrEmpty()){
                replyId = intent.getStringExtra("replyId")
            }
            if (!intent.getStringExtra("postId").isNullOrEmpty()){
                postId = intent.getStringExtra("postId")
            }
        }
        initToolbar()
        initImageAddView()
        initEditListener()
        initTypeSelector()

        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                if (draftId != NOT_DRAFT_ID && draftId != isComment) {
                    viewModel.deleteDraft(draftId)
                }
                setResult(NEED_REFRESH_RESULT)
                progressDialog?.dismiss()
                finish()
            }
        }

        viewModel.finishReleaseCommentEvent.observeNotNull {
            if (it) {
                setResult(NEED_REFRESH_RESULT)
                finish()
            }
        }

        viewModel.finishActivityEvent.observeNotNull {
            finish()
        }
    }


    //发布页标签单选
    @SuppressLint("SetTextI18n")
    private fun initTypeSelector() {
        val hashSet = HashSet<String>()
        viewModel.getAllCirCleData("问答圈", "test1")
        viewModel.allCircle.observe {
            if (!it.isNullOrEmpty()) {
                val chipGroup = findViewById<ChipGroup>(R.id.qa_layout_quiz_tag)
                for (topic in it.withIndex()) {
                    chipGroup.addView((layoutInflater.inflate(R.layout.qa_quiz_view_chip, chipGroup, false) as Chip).apply {
                        text = "# " + topic.value.topicName
                        setOnSingleClickListener {
                            if (hashSet.contains(getTopicText(text.toString()))) {

                                dynamicType = ""
                                hashSet.remove(getTopicText(text.toString()))
                            } else {
                                dynamicType = getTopicText(text.toString())
                                hashSet.add(getTopicText(text.toString()))
                            }
                        }
                    })
                }
            }
        }
    }

    //圈子名前加#，但是请求的时候不带#
    private fun getTopicText(text: String): String {
        val type = StringBuffer(text)
        type.delete(0, 2)
        return type.toString()
    }
    //动态监听内容文字变化

    private fun initEditListener() {
        qa_edt_quiz_content.addTextChangedListener(object : TextWatcher {
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
            ) = if (!TextUtils.isEmpty(charSequence) && charSequence.isNotEmpty()) {
                qa_tv_toolbar_right.setBackgroundResource(qa_shape_send_dynamic_btn_blue_background)
                qa_tv_edit_num.text = charSequence.length.toString() + "/500"

            } else {
                qa_tv_toolbar_right.setBackgroundResource(qa_shape_send_dynamic_btn_grey_background)
                qa_tv_edit_num.text = charSequence.length.toString() + "/500"
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }


    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener(View.OnClickListener {
            if (qa_edt_quiz_content.text.isNullOrEmpty() && draftId == NOT_DRAFT_ID || draftId == isComment) {
                finish()
                return@OnClickListener
            }
            exitDialog.show()
        })
        progressDialog = ProgressDialog(this)
        progressDialog?.apply {
            setMessage("加载中...")
            setCanceledOnTouchOutside(false)
        }
        if (isComment == "1") {
            qa_tv_toolbar_title.text = "发布评论"
            qa_tv_choose_circle.visibility = View.GONE
            qa_layout_quiz_tag.visibility = View.GONE
            draftId = isComment
            qa_edt_quiz_content.setText(commentContent)
        } else {
            qa_tv_toolbar_title.text = getString(R.string.qa_quiz_toolbar_title_text)
        }
        qa_ib_toolbar_more.gone()
        qa_tv_toolbar_right.apply {
            visible()
            text = getString(R.string.qa_quiz_dialog_next)
            setOnClickListener {
                if (isComment == "") {
                    val result = viewModel.submitTitleAndContent(dynamicType, qa_edt_quiz_content.text.toString())
                    if (result) {
                        progressDialog?.show()
                        viewModel.submitDynamic()
                    }
                } else {
                    progressDialog?.show()
                    viewModel.submitComment(postId, qa_edt_quiz_content.text.toString(), replyId)
                }
            }
        }
    }

    private fun initImageAddView() {
        nine_grid_view.addView(ContextCompat.getDrawable(this, qa_ic_add_images)?.let { createImageViewFromVector(it) })
        nine_grid_view.setOnItemClickListener { _, index ->
            if (index == nine_grid_view.childCount - 1) {
                this@QuizActivity.selectImageFromAlbum(MAX_SELECTABLE_IMAGE_COUNT)
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
            return if (qa_edt_quiz_content.text.isNullOrEmpty() && draftId == NOT_DRAFT_ID || draftId == isComment) {
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
            viewModel.addItemToDraft(qa_edt_quiz_content.text.toString(), dynamicType)
        } else {
            viewModel.updateDraftItem(qa_edt_quiz_content.text.toString(), draftId, dynamicType)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun loadDraft(event: DynamicDraftEvent) {
        if (intent.getStringExtra("type") != null) {
            EventBus.getDefault().removeStickyEvent(event)
            return
        }
        val json = String(Base64.decode(event.jsonString, Base64.DEFAULT))
        val question = Gson().fromJson(json, Question::class.java)
        qa_edt_quiz_content.setText(question.description)
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
            dismiss()
            finish()
        }, cancelListener = View.OnClickListener {
            dismiss()
        })
    }

    override fun onPause() {
        //防止内存泄漏
        exitDialog.dismiss()
        super.onPause()
    }
}
