package com.mredrock.cyxbs.qa.pages.answer.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
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

        fun activityStart(activity: FragmentActivity, qid: String, description: String, photoUrl: List<String>, requestCode: Int) {
            activity.startActivityForResult<AnswerActivity>(requestCode, "qid" to qid, "photoUrl" to photoUrl,
                    "description" to description)
        }
    }

    override val viewModelClass = AnswerViewModel::class.java

    override val isFragmentActivity = false

    private var draftId = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_answer)
        common_toolbar.init(getString(R.string.qa_answer_question_title), listener = View.OnClickListener {
            if (edt_answer_content.text.isNullOrEmpty()) {
                finish()
                return@OnClickListener
            }
            saveDraft()
            finish()
        })
        initView()
        initImageAddView()
        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                if (draftId != "-1") {
                    viewModel.deleteDraft(draftId)
                }
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val description = intent.getStringExtra("description")
        val photoUrl = intent.getStringArrayListExtra("photoUrl")
        tv_answer_question_description.text = description
        nine_grid_view_question.apply {
            setImages(photoUrl)
            setOnItemClickListener { _, index ->
                ViewImageActivity.activityStart(context, photoUrl[index])
            }
            visibility = View.GONE
        }
        //判断是否当前只有两行
        tv_answer_question_description.apply {
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    if (tv_answer_question_description.lineCount > 2 || photoUrl.size > 0) {
                        tv_answer_question_description.maxLines = 2
                        tv_answer_question_description.ellipsize = TextUtils.TruncateAt.END
                    } else {
                        tv_answer_question_detail_show_more.visibility = View.GONE
                    }
                    return false
                }

            })
        }

        tv_answer_question_detail_show_more.apply {
            setOnClickListener {
                //点击后展开，tv显示所有内容
                if (tv_answer_question_description.maxLines == 2) {
                    tv_answer_question_description.maxLines = Int.MAX_VALUE
                    val drawable = ContextCompat.getDrawable(this@AnswerActivity, R.drawable.qa_question_describe_show_more)
                    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                    nine_grid_view_question.visibility = View.VISIBLE
                } else {
                    tv_answer_question_description.maxLines = 2
                    val drawable = ContextCompat.getDrawable(this@AnswerActivity, R.drawable.qa_question_describe_show_more)
                    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                    nine_grid_view_question.visibility = View.GONE
                }
            }

        }

    }

    private fun initImageAddView() {
        nine_grid_view.addView(createImageView(BitmapFactory.decodeResource(resources, R.drawable.qa_ic_quiz_grid_add_img)))
        nine_grid_view.setOnItemClickListener { _, index ->
            if (index == nine_grid_view.childCount - 1) {
                this@AnswerActivity.selectImageFromAlbum(MAX_SELECTABLE_IMAGE_COUNT, viewModel.imageLiveData.value)
            } else {
                ViewImageActivity.activityStartForResult(this@AnswerActivity, viewModel.tryEditImg(index)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (edt_answer_content.text.isNullOrEmpty()) {
                return super.onKeyDown(keyCode, event)
            }
            saveDraft()
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun saveDraft() {
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
        if (content.pictures != null) {
            val list = content.pictures.split(",").toMutableList()
            if (list[0] != "") {
                viewModel.setImageList(arrayListOf<String>().apply { addAll(list) })
            }
        }
        viewModel.qid = event.targetId
        draftId = event.selfId
    }
}
