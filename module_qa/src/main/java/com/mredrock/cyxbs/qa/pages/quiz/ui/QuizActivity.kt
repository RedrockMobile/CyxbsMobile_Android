package com.mredrock.cyxbs.qa.pages.quiz.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.mredrock.cyxbs.common.component.CyxbsToast
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
        const val MAX_CONTENT_SIZE = 500
        const val MAX_SELECTABLE_IMAGE_COUNT = 9
        const val NOT_DRAFT_ID = "-1"
        const val FIRST_QUIZ = "cyxbs_quiz_is_first_time"
        const val FIRST_QUIZ_SP_KEY = "isFirstTimeQuiz"
        fun activityStart(fragment: Fragment, type: String, requestCode: Int) {
            fragment.startActivityForResult<QuizActivity>(requestCode, "type" to type)
        }
    }


    private var progressDialog: ProgressDialog? = null

    //    override val isFragmentActivity = false
    private var draftId = NOT_DRAFT_ID
    private var dynamicType: String = ""
    private var isFirstQuiz: Boolean = true
    private val exitDialog by lazy { createExitDialog() }
    private var isComment = ""
    private var replyId = ""
    private var postId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_quiz)

        initEditListener()

        isFirstQuiz = sharedPreferences(FIRST_QUIZ).getBoolean(FIRST_QUIZ_SP_KEY, true)
        if (!intent.getStringExtra("isComment").isNullOrEmpty()) {
            isComment = intent.getStringExtra("isComment")
            if (!intent.getStringExtra("commentContent").isNullOrEmpty()) {
                qa_edt_quiz_content.setText(intent.getStringExtra("commentContent"))
            }
            if (!intent.getStringExtra("replyId").isNullOrEmpty()) {
                replyId = intent.getStringExtra("replyId")
                nine_grid_view.gone()
            }
            if (!intent.getStringExtra("postId").isNullOrEmpty()) {
                postId = intent.getStringExtra("postId")
            }
        }

        initToolbar()
        initImageAddView()
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
                setResult(NEED_REFRESH_RESULT, Intent().apply { putExtra("text", "") })
                finish()
            }
        }

        viewModel.finishActivityEvent.observeNotNull {
            progressDialog?.dismiss()
            finish()
        }
    }


    //发布页标签单选
    @SuppressLint("SetTextI18n")
    private fun initTypeSelector() {
        viewModel.getAllCirCleData("问答圈", "test1")
        viewModel.allCircle.observe {
            if (!it.isNullOrEmpty()) {
                val chipGroup = findViewById<ChipGroup>(R.id.qa_layout_quiz_tag)
                for (topic in it.withIndex()) {
                    chipGroup.addView((layoutInflater.inflate(R.layout.qa_quiz_view_chip, chipGroup, false) as Chip).apply {
                        text = "# " + topic.value.topicName
                        setOnClickListener {
                            if (dynamicType == getTopicText(text.toString())) {
                                dynamicType = ""
                            } else {
                                dynamicType = getTopicText(text.toString())
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

    @SuppressLint("SetTextI18n")
    private fun initEditListener() {
        qa_edt_quiz_content.filters = arrayOf(
                object : InputFilter.LengthFilter(
                        MAX_CONTENT_SIZE) {}
        )
        qa_edt_quiz_content.doOnTextChanged { text, _, _, _ ->
            text?.let {
                qa_tv_edit_num.text = "${text.length}/$MAX_CONTENT_SIZE"
                if (text.length in 1..MAX_CONTENT_SIZE) {
                    qa_tv_toolbar_right.setBackgroundResource(qa_shape_send_dynamic_btn_blue_background)
                } else {
                    qa_tv_toolbar_right.setBackgroundResource(qa_shape_send_dynamic_btn_grey_background)
                }
            }
        }

    }


    @SuppressLint("SetTextI18n")
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
            if (!intent.getStringExtra("replyNickname").isNullOrEmpty()) {
                qa_tv_toolbar_title.text = "回复 @" + intent.getStringExtra("replyNickname")
            }
            qa_tv_choose_circle.visibility = View.GONE
            qa_layout_quiz_tag.visibility = View.GONE
            draftId = isComment
        } else {
            qa_tv_toolbar_title.text = getString(R.string.qa_quiz_toolbar_title_text)
        }
        qa_ib_toolbar_more.gone()
        qa_tv_toolbar_right.apply {
            visible()
            text = getString(R.string.qa_quiz_dialog_next)
            setOnClickListener {
                if (isComment == "") {
                    if (viewModel.checkTitleAndContent(dynamicType, qa_edt_quiz_content.text.toString())) {
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
                val imageListUri = ArrayList(LPhotoHelper.getSelectedPhotos(data)).map {
                    it.toString()
                }
                val imageListAbsolutePath = ArrayList<String>()
                imageListUri.forEach { imageListAbsolutePath.add(Uri.parse(it).getAbsolutePath(this)) }
                //为再次进入图库保存以前添加的图片，进行的逻辑
                if (viewModel.lastImageLiveData.size + imageListAbsolutePath.size <= 8)
                    viewModel.lastImageLiveData.addAll(imageListAbsolutePath)
                else {
                    CyxbsToast.makeText(this, "请重新选择图片，一次性只能够发布8张图片", Toast.LENGTH_SHORT).show()
                }
                viewModel.setImageList(viewModel.lastImageLiveData)
            }
            ViewImageCropActivity.DEFAULT_RESULT_CODE -> viewModel.setImageList(viewModel.imageLiveData.value!!.apply {
                set(viewModel.editingImgPos, data.getStringExtra(ViewImageCropActivity.EXTRA_NEW_PATH))
            })
        }
    }

    private fun createImageViewFromVector(drawable: Drawable) = RectangleView(this).apply {
        scaleType = ImageView.ScaleType.CENTER
        background = ContextCompat.getDrawable(this@QuizActivity, qa_shape_quiz_select_pic_empty_background)
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
        progressDialog?.show()
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

    override fun onBackPressed() {
        setResult(-1, Intent().apply { putExtra("text", qa_edt_quiz_content.text.toString()) })
        super.onBackPressed()
    }
}
