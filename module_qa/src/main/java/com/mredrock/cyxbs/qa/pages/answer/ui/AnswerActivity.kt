package com.mredrock.cyxbs.qa.pages.answer.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.mredrock.cyxbs.common.config.QA_ANSWER
import com.mredrock.cyxbs.common.event.AnswerDraftEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Content
import com.mredrock.cyxbs.qa.pages.answer.viewmodel.AnswerViewModel
import com.mredrock.cyxbs.qa.ui.activity.ViewImageCropActivity
import com.mredrock.cyxbs.qa.ui.widget.CommonDialog
import com.mredrock.cyxbs.qa.utils.CHOOSE_PHOTO_REQUEST
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import kotlinx.android.synthetic.main.qa_activity_answer.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.limuyang2.photolibrary.LPhotoHelper

@Route(path = QA_ANSWER)
class AnswerActivity : BaseViewModelActivity<AnswerViewModel>(), EventBusLifecycleSubscriber {
    companion object {
        const val MAX_SELECTABLE_IMAGE_COUNT = 6
        const val NOT_DRAFT_ID = "-1"
        fun activityStart(activity: FragmentActivity, qid: String, description: String, photoUrl: List<String>, requestCode: Int) {
            activity.startActivityForResult<AnswerActivity>(requestCode, "qid" to qid, "photoUrl" to photoUrl,
                    "description" to description)
        }
    }

    override val viewModelClass = AnswerViewModel::class.java

    override val isFragmentActivity = false

    private var draftId: String = NOT_DRAFT_ID

    private val exitDialog by lazy { createExitDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_answer)
        initToolbar()
        if (intent.getStringExtra("description") != null && intent.getStringArrayListExtra("photoUrl") != null) {
            initView(intent.getStringExtra("description")
                    ?: "", intent.getStringArrayListExtra("photoUrl") ?: listOf<String>())
        }
        initImageAddView()
        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                if (draftId != NOT_DRAFT_ID) {
                    viewModel.deleteDraft(draftId)
                }
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        viewModel.backAndFinishActivityEvent.observeNotNull {
            finish()
        }
        viewModel.questionData.observeNotNull {
            initView(it.description, it.photoUrl)
        }
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener(View.OnClickListener {
            if (edt_answer_content.text.isNullOrEmpty() && draftId == NOT_DRAFT_ID) {
                finish()
                return@OnClickListener
            }
            exitDialog.show()
        })
        qa_tv_toolbar_title.text = getString(R.string.qa_answer_question_title)
        qa_ib_toolbar_more.gone()
        qa_tv_toolbar_right.apply {
            visible()
            text = getString(R.string.qa_answer_btn_text)
            setSingleOnClickListener {
                viewModel.submitAnswer(edt_answer_content.text.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView(description: String, photoUrl: List<String>) {
        tv_answer_question_description.text = description
        nine_grid_view_question.apply {
            setImages(photoUrl)
            setOnItemClickListener { _, index ->
                ViewImageCropActivity.activityStart(context, photoUrl[index])
            }
            gone()
        }
        //判断是否当前只有两行
        tv_answer_question_description.apply {
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    if (tv_answer_question_description.lineCount > 2 || photoUrl.isNotEmpty()) {
                        tv_answer_question_description.maxLines = 2
                        tv_answer_question_description.ellipsize = TextUtils.TruncateAt.END
                    } else {
                        tv_answer_question_detail_show_more.gone()
                    }
                    return false
                }

            })
        }

        tv_answer_question_detail_show_more.apply {
            setSingleOnClickListener {
                //点击后展开，tv显示所有内容
                if (tv_answer_question_description.maxLines == 2) {
                    tv_answer_question_description.maxLines = Int.MAX_VALUE
                    val drawable = ContextCompat.getDrawable(this@AnswerActivity, R.drawable.qa_ic_question_describe_show_more_up)
                    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                    nine_grid_view_question.visible()
                } else {
                    tv_answer_question_description.maxLines = 2
                    val drawable = ContextCompat.getDrawable(this@AnswerActivity, R.drawable.qa_ic_question_describe_show_more_down)
                    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                    nine_grid_view_question.gone()
                }
            }

        }

    }

    private fun initImageAddView() {
        nine_grid_view.addView(ContextCompat.getDrawable(this, R.drawable.qa_ic_quiz_add_picture_empty)?.let { createImageViewFromVector(it) })
        nine_grid_view.setOnItemClickListener { _, index ->
            if (index == nine_grid_view.childCount - 1) {
                this@AnswerActivity.selectImageFromAlbum(MAX_SELECTABLE_IMAGE_COUNT, viewModel.imageLiveData.value)
            } else {
                ViewImageCropActivity.activityStartForResult(this@AnswerActivity, viewModel.tryEditImg(index)
                        ?: return@setOnItemClickListener)
            }
        }

        viewModel.imageLiveData.observeNotNull { selectedImageFiles ->
            viewModel.resetInvalid()
            //对view进行复用
            for (i in 0 until nine_grid_view.childCount - 1) {
                val view = nine_grid_view.getChildAt(i)
                if (i >= selectedImageFiles.size) {
                    //移除多出来的view
                    nine_grid_view.removeView(view)
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

    private fun createImageViewFromVector(drawable: Drawable) = ImageView(this).apply {
        scaleType = ImageView.ScaleType.CENTER
        background = ContextCompat.getDrawable(this@AnswerActivity, R.drawable.qa_shape_quiz_select_pic_empty_background)
        setImageDrawable(drawable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CHOOSE_PHOTO_REQUEST -> {
                val imageListUri = ArrayList((LPhotoHelper.getSelectedPhotos(data)).map {
                    it.toString()
                })
                val imageListAbsolutePath = ArrayList<String>()
                imageListUri.forEach { imageListAbsolutePath.add(Uri.parse(it).getAbsolutePath(this)) }
                viewModel.setImageList(imageListAbsolutePath)
            }


            ViewImageCropActivity.DEFAULT_RESULT_CODE -> {
                viewModel.setImageList(viewModel.imageLiveData.value!!.apply {
                    removeAt(viewModel.editingImgPos)
                })
            }

        }
    }

    private fun createImageView(uri: Uri) = ImageView(this).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageURI(uri)
    }


    override fun getViewModelFactory(): AnswerViewModel.Factory {
        return if (intent.getStringExtra("qid") == null) {
            AnswerViewModel.Factory("-1")
        } else {
            AnswerViewModel.Factory(intent.getStringExtra("qid") ?: "")
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitDialog.isShowing) {
                return super.onKeyDown(keyCode, event)
            }
            return if (edt_answer_content.text.isNullOrEmpty() && draftId == NOT_DRAFT_ID) {
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
            viewModel.addItemToDraft(edt_answer_content.text.toString())
        } else {
            viewModel.updateDraft(edt_answer_content.text.toString(), draftId)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun loadDraft(event: AnswerDraftEvent) {
        if (intent.getStringExtra("qid") != null) {
            EventBus.getDefault().removeStickyEvent(event)
            return
        }
        draftId = event.selfId
        viewModel.qid = event.targetId
        val json = String(Base64.decode(event.jsonString, Base64.DEFAULT))
        val content = Gson().fromJson(json, Content::class.java)
        edt_answer_content.setText(content.title)
        viewModel.getQuestionInfo()
        if (!content.pictures.isNullOrEmpty()) {
            viewModel.setImageList(content.pictures)
        }
    }

    private fun createExitDialog() = CommonDialog(this).apply {
        initView(icon = R.drawable.qa_ic_quiz_quit_edit, title = getString(R.string.qa_answer_dialog_exit_text), firstNotice = getString(R.string.qa_answer_publish_dialog_exit_subtitle_text), secondNotice = null, buttonText = getString(R.string.qa_common_dialog_exit), confirmListener = View.OnClickListener {
            saveDraft()
            dismiss()
        }, cancelListener = View.OnClickListener {
            dismiss()
        })
    }
}