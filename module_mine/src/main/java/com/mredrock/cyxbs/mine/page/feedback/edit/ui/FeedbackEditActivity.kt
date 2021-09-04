package com.mredrock.cyxbs.mine.page.feedback.edit.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.mredrock.cyxbs.common.ui.BaseMVPVMActivity
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityFeedbackEditBinding
import com.mredrock.cyxbs.mine.page.feedback.adapter.rv.RvAdapter
import com.mredrock.cyxbs.mine.page.feedback.edit.presenter.FeedbackEditPresenter
import com.mredrock.cyxbs.mine.page.feedback.edit.viewmodel.FeedbackEditViewModel
import com.mredrock.cyxbs.mine.page.feedback.utils.CHOOSE_FEED_BACK_PIC
import com.mredrock.cyxbs.mine.page.feedback.utils.FileUtils
import com.mredrock.cyxbs.mine.page.feedback.utils.selectImageFromAlbum
import com.mredrock.cyxbs.mine.page.feedback.utils.setSelectedPhotos
import java.io.File
import java.util.*

/**
 * @Date : 2021/8/23   20:52
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackEditActivity :
    BaseMVPVMActivity<FeedbackEditViewModel, MineActivityFeedbackEditBinding, FeedbackEditPresenter>() {

    /**
     * 初始化adapter
     */
    private val rvPicAdapter by lazy {
        RvAdapter()
    }

    /**
     * 得到P层
     */
    override fun createPresenter(): FeedbackEditPresenter = FeedbackEditPresenter(this)

    /**
     * 得到布局id
     */
    override fun getLayoutId(): Int = R.layout.mine_activity_feedback_edit

    /**
     * 提供viewModel
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.vm = viewModel
    }

    /**
     * 初始化view
     */
    override fun initView() {
        // TODO: 2021/8/23 自定义一个EditText 可实现以下功能：1.一键删除 2.选中时字体呈现不同颜色 3.超过字数限制会有提示动画
        binding?.apply {
            //对两个editText进行监听初始化
            etEditDescription.addTextChangedListener(presenter?.DesTextWatcher())
            etEditTitle.addTextChangedListener(presenter?.TitleTextWatcher())
            //对四个chip进行初始化
            chipOne.setOnCheckedChangeListener(presenter)
            chipTwo.setOnCheckedChangeListener(presenter)
            chipThree.setOnCheckedChangeListener(presenter)
            chipFour.setOnCheckedChangeListener(presenter)
            //对rv进行初始化
            rvBanner.apply {
                adapter = rvPicAdapter
                layoutManager = GridLayoutManager(this@FeedbackEditActivity, 3)
            }
        }
    }

    /**
     * 监听LiveData
     */
    override fun observeData() {
        viewModel.apply {
            //Change
            observePics(uris)
        }
    }

    /**
     *
     * 打开相册后用户筛选图片，最后返回到Activity更新选择的图片
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_FEED_BACK_PIC) {
            presenter?.dealPic(data)
        }
    }


    /**
     * 观察图片Uri并对rv_banner进行初始化操作
     */
    private fun observePics(uris: LiveData<List<Uri>>) {
        uris.observe({ lifecycle }) {
            val list = presenter?.getBinderList(it,
                { view, i ->
                    toast("内容被点击")
                }, { view, i ->
                    toast("删除被点击")
                    presenter?.removePic(i)
                }, { view, i ->
                    val list = ArrayList(viewModel?.uris?.value)
                    setSelectedPhotos(list)
                    toast("添加按钮被点击")
                    this@FeedbackEditActivity.selectImageFromAlbum(3)
                }
            )
            rvPicAdapter.submitList(list)
        }
    }

    /**
     * 初始化listener
     */
    override fun initListener() {
        binding?.apply {
            mineButton.setOnClickListener {
                vm?.uris?.value?.let {
                    presenter?.postFeedbackInfo(
                        productId = "1",
                        type = chipOne.text.toString(),
                        title = etEditTitle.text.toString(),
                        content = etEditDescription.text.toString(),
                        FileUtils.uri2File(this@FeedbackEditActivity, it[0])
                    )
                }
            }
        }
    }
}