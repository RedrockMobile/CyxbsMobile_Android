package com.mredrock.cyxbs.qa.pages.quiz.ui

import android.os.Build
import androidx.annotation.RequiresApi
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
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
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.QA_QUIZ
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.mine.network.model.DynamicDraft
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.R.drawable.*
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.pages.quiz.QuizViewModel
import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleDetailActivity
import com.mredrock.cyxbs.qa.ui.activity.ViewImageCropActivity
import com.mredrock.cyxbs.qa.ui.widget.DraftDialog
import com.mredrock.cyxbs.qa.ui.widget.RectangleView
import com.mredrock.cyxbs.qa.utils.CHOOSE_PHOTO_REQUEST
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import kotlinx.android.synthetic.main.qa_activity_quiz.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import top.limuyang2.photolibrary.LPhotoHelper

@Route(path = QA_QUIZ)
class QuizActivity : BaseViewModelActivity<QuizViewModel>() {

    companion object {
        const val MAX_CONTENT_SIZE = 500
        const val MAX_SELECTABLE_IMAGE_COUNT = 8
        const val NOT_DRAFT = "0"
        const val UPDATE_DRAFT = "1"
        fun activityStart(fragment: Fragment, type: String, requestCode: Int) {
            fragment.startActivityForResult<QuizActivity>(requestCode, "type" to type)
        }
    }

    private var progressDialog: ProgressDialog? = null
    private var topicType: String = ""
    private val exitDialog by lazy { createExitDialog() }
    private var isComment = ""
    private var draftId = ""
    private var backId = "1"
    private var replyId = ""
    private var postId = ""
    private var currentTypeIndex = 0
    private val topicMap = HashMap<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_quiz)
        initEditListener()
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
        viewModel.draft.observe { draft ->
            if (draft != null) {
                loadDraft(draft)
            }
        }
        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                if (viewModel.isReleaseSuccess) {
                    topicMap[topicType]?.let { id ->
                        CircleDetailActivity.activityStartFormQuiz(
                                this,
                                id
                        )
                    }
                    progressDialog?.dismiss()
                    finish()
                } else {
                    setResult(NEED_REFRESH_RESULT)
                    progressDialog?.dismiss()
                    finish()
                }
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
        viewModel.getAllCirCleData()
        viewModel.getImageLimits()
        viewModel.allCircle.observe {
            if (!it.isNullOrEmpty()) {
                val chipGroup = findViewById<ChipGroup>(R.id.qa_layout_quiz_tag)
                for (topic in it.withIndex()) {
                    topicMap[topic.value.topicName] = topic.value.topicId
                    chipGroup?.addView(
                            (layoutInflater.inflate(
                                    R.layout.qa_quiz_view_chip,
                                    chipGroup,
                                    false
                            ) as Chip).apply {
                                text = "# " + topic.value.topicName
                                setOnClickListener {
                                    topicType = if (topicType == getTopicText(text.toString())) {
                                        //当第二次点击时值为零，表示未选择圈子
                                        "0"
                                    } else {
                                        getTopicText(text.toString())
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
                        MAX_CONTENT_SIZE
                ) {}
        )
        qa_edt_quiz_content.doOnTextChanged { text, _, _, _ ->
            text?.let {
                qa_tv_edit_num.text = "${text.length}/$MAX_CONTENT_SIZE"
                if (text.length in 1..MAX_CONTENT_SIZE) {
                    qa_tv_toolbar_right.setBackgroundResource(
                            qa_shape_send_dynamic_btn_blue_background
                    )
                } else {
                    qa_tv_toolbar_right.setBackgroundResource(
                            qa_shape_send_dynamic_btn_grey_background
                    )
                }
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener(View.OnClickListener {
            if (qa_edt_quiz_content.text.isNullOrEmpty() || backId == isComment) {
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
            backId = isComment
        } else {
            qa_tv_toolbar_title.text = getString(R.string.qa_quiz_toolbar_title_text)
        }
        qa_ib_toolbar_more.gone()
        qa_tv_toolbar_right.apply {
            visible()
            text = getString(R.string.qa_quiz_dialog_next)
            setOnClickListener {
                if (isComment == "") {
                    if (viewModel.checkTitleAndContent(
                                    topicType,
                                    qa_edt_quiz_content.text.toString()
                            )
                    ) {
                        progressDialog?.show()
                        viewModel.deleteDraft()
                        viewModel.submitDynamic()
                    }
                } else {
                    if (qa_edt_quiz_content.text.toString().isBlank()) {
                        toast(R.string.qa_hint_content_empty)
                        return@setOnClickListener
                    }
                    progressDialog?.show()
                    viewModel.submitComment(postId, qa_edt_quiz_content.text.toString(), replyId)
                }
            }
        }
    }

    private fun initImageAddView() {
        nine_grid_view.addView(
                ContextCompat.getDrawable(this, qa_ic_add_photo)?.let { createImageViewFromVector(it) })
        nine_grid_view.setOnItemClickListener { _, index ->
            if (index == nine_grid_view.childCount - 1) {
                //如果达到选择图片的上限，就ban掉不允许添加图片
                if (nine_grid_view.childCount <= MAX_SELECTABLE_IMAGE_COUNT) {
                    this@QuizActivity.selectImageFromAlbum(MAX_SELECTABLE_IMAGE_COUNT)
                } else {
                    BaseApp.context.toast("已达图片数上限")
                }
            } else {
                ViewImageCropActivity.activityStartForResult(
                        this@QuizActivity, viewModel.tryEditImg(index)
                        ?: return@setOnItemClickListener
                )
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //大图片压缩加载
                        (view as ImageView).setImageBitmap(Uri.parse(selectedImageFiles[i]).bmSizeStandardizing(context = this.applicationContext))
                    } else

                        viewModel.checkInvalid(false)
                } else viewModel.checkInvalid(true)

            }
            //补充缺少的view
            selectedImageFiles.asSequence()
                    .filterIndexed { index, _ -> index >= nine_grid_view.childCount - 1 }
                    .forEach {
                        if (it.isNotEmpty()) {
                            nine_grid_view.addView(
                                    createImageView(Uri.parse(it)),
                                    nine_grid_view.childCount - 1
                            )
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
                imageListUri.forEach {
                    imageListAbsolutePath.add(
                            Uri.parse(it).getAbsolutePath(this)
                    )
                }
                //为再次进入图库保存以前添加的图片，进行的逻辑
                if (viewModel.lastImageLiveData.size + imageListAbsolutePath.size <= 8)
                    viewModel.lastImageLiveData.addAll(imageListAbsolutePath)
                else {
                    CyxbsToast.makeText(
                            this,
                            getString(R.string.qa_choose_image_tips),
                            Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.setImageList(viewModel.lastImageLiveData)
            }
            ViewImageCropActivity.DEFAULT_RESULT_CODE -> viewModel.setImageList(viewModel.imageLiveData.value!!.apply {
                set(
                        viewModel.editingImgPos,
                        data.getStringExtra(ViewImageCropActivity.EXTRA_NEW_PATH)
                )
            })
        }
    }

    private fun createImageViewFromVector(drawable: Drawable) = RectangleView(this).apply {
        scaleType = ImageView.ScaleType.CENTER
        background =
                ContextCompat.getDrawable(this@QuizActivity, qa_shape_quiz_select_pic_empty_background)
        setImageDrawable(drawable)
    }


    private fun createImageView(uri: Uri) = RectangleView(this).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        val bitMap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            uri.bmSizeStandardizing(context = this.context)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        setImageBitmap(bitMap)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitDialog.isShowing) {
                return super.onKeyDown(keyCode, event)
            }
            return if (qa_edt_quiz_content.text.isNullOrEmpty() || backId == isComment) {
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
        viewModel.updateDraftItem(draftId, qa_edt_quiz_content.text.toString(), topicType)
    }


    private fun loadDraft(draft: DynamicDraft) {
        draftId = if (draft.isDraft == 1) {
            UPDATE_DRAFT
        } else {
            NOT_DRAFT
        }
        qa_edt_quiz_content.setText(draft.content)
        //草稿保存了上一次选择圈子的状态，重新添加上
        val chipGroup = findViewById<ChipGroup>(R.id.qa_layout_quiz_tag)
        val childView = Array(chipGroup.childCount) { chipGroup.getChildAt(it) as Chip }
        val type = draft.type
        if (type.isNotEmpty()) {
            //避免空指针
            topicType = type
            if (topicMap[type]?.isNotEmpty() == true) {
                currentTypeIndex = topicMap[type]?.toInt()!!
                childView[currentTypeIndex - 1].isChecked = true
            }
        }
        LogUtils.d("Gibson", "when load, draft = $draft, image")
        if (!draft.images.isNullOrEmpty()) {
            viewModel.setImageList(arrayListOf<String>().apply { addAll(draft.images) })
        } else {//表示草稿中并没有图像，就直接清空
            LogUtils.d("Gibson", "refresh imageList")
            viewModel.imageLiveData.value = null
        }
    }

    private fun createExitDialog() = DraftDialog(this).apply {
        initView(
                title = getString(R.string.qa_quiz_dialog_exit_text),
                saveText = "保存",
                noSaveText = "不保存",
                cancelText = "取消",
                saveListener = View.OnClickListener {
                    saveDraft()
                    dismiss()
                },
                noSaveListener = View.OnClickListener {
                    viewModel.deleteDraft()
                    dismiss()
                    finish()
                },
                cancelListener = View.OnClickListener {
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