package com.mredrock.cyxbs.qa.pages.quiz.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.quiz.QuizViewModel
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.RewardSetDialog
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.SelectImageDialog
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.TagsEditDialog
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.TimePickDialog
import com.mredrock.cyxbs.qa.utils.getMaxLength
import com.mredrock.cyxbs.qa.utils.selectImageFromAlbum
import com.mredrock.cyxbs.qa.utils.selectImageFromCamera
import kotlinx.android.synthetic.main.qa_activity_quiz.*
import org.jetbrains.anko.startActivity

//todo 这个界面赶时间写得有点乱，记得优化一下

class QuizActivity : BaseViewModelActivity<QuizViewModel>() {
    companion object {
        const val MAX_SELECTABLE_IMAGE_COUNT = 6

        fun activityStart(context: Context, type: String) {
            context.startActivity<QuizActivity>("type" to type)
        }
    }

    override val viewModelClass = QuizViewModel::class.java
    override val isFragmentActivity = false

    private val selectImageDialog by lazy {
        SelectImageDialog(this).apply {
            selectImageFromAlbum = {
                this@QuizActivity.selectImageFromAlbum(QuizActivity.MAX_SELECTABLE_IMAGE_COUNT, viewModel.imageLiveData.value)
            }

            selectImageFromCamera = {
                this@QuizActivity.selectImageFromCamera()
            }
        }
    }

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
        common_toolbar.init(getString(R.string.qa_quiz_title))
        initEdtView()
        initImageAddView()
        initFooterView()
        viewModel.getMyReward() //优先初始化积分，避免用户等待

        viewModel.backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                finish()
            }
        }
    }

    private fun initEdtView() {
        val maxTitleLength = edt_quiz_title.getMaxLength()
        val maxContentLength = edt_quiz_content.getMaxLength()
        edt_quiz_title.addTextChangedListener(createTextWatcher {
            tv_quiz_title_left_count.text = (maxTitleLength - it.length).toString()
        })
        edt_quiz_content.addTextChangedListener(createTextWatcher {
            tv_quiz_content_left_count.text = (maxContentLength - it.length).toString()
        })
        tv_quiz_title_left_count.text = maxTitleLength.toString()
        tv_quiz_content_left_count.text = maxContentLength.toString()
        tv_quiz_anonymous.setOnClickListener {
            tv_quiz_anonymous.toggle()
            viewModel.isAnonymous = tv_quiz_anonymous.isChecked
        }
        tv_quiz_tags.setOnClickListener { editTagDialog.show() }

        viewModel.tagLiveData.observe {
            if (it.isNullOrBlank()) {
                return@observe
            }
            tv_quiz_tags.text = getString(R.string.qa_quiz_tag, it)
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
                selectImageDialog.show()
            }
        }

        viewModel.imageLiveData.observe { selectedImageFiles ->
            selectedImageFiles ?: return@observe
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
                val localMedia = selectedImageFiles[i]
                val path = localMedia.compressPath.takeIf { localMedia.isCompressed }
                        ?: localMedia.path
                (view as ImageView).setImageBitmap(BitmapFactory.decodeFile(path))
            }
            //补充缺少的view
            selectedImageFiles.asSequence()
                    .filterIndexed { index, _ -> index >= nine_grid_view.childCount - 1 }
                    .map { localMedia ->
                        localMedia.compressPath.takeIf { localMedia.isCompressed }
                                ?: localMedia.path
                    }.toList()
                    .forEach {
                        nine_grid_view.addView(createImageView(BitmapFactory.decodeFile(it)),
                                nine_grid_view.childCount - 1)
                    }
        }
    }

    private fun initFooterView() {
        iv_quiz_select_tag.setOnClickListener { editTagDialog.show() }
        iv_quiz_add_img.setOnClickListener { selectImageDialog.show() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            viewModel.setImageList(PictureSelector.obtainMultipleResult(data))

        } else if (requestCode == PictureConfig.REQUEST_CAMERA) {
            //fixme 调用相机拍照没有返回结果
            /*val localMedia = PictureSelector.obtainMultipleResult(data)[0]
            selectedImageFiles.add(localMedia)
            val path = localMedia.compressPath.takeIf { localMedia.isCompressed }
                    ?: localMedia.path
            nine_grid_view.addView(createImageView(BitmapFactory.decodeFile(path)),
                    nine_grid_view.childCount - 1)*/
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

    override fun getViewModelFactory() = QuizViewModel.Factory(intent.getStringExtra("type"))
}
