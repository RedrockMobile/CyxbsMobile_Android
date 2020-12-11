package com.mredrock.cyxbs.qa.pages.dynamic.ui.activity

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicDetailViewModel
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.ui.widget.OptionalPopWindow
import com.mredrock.cyxbs.qa.utils.questionTimeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_fragment_dynamic_detail.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic.*

/**
 * @Author: sandyz987
 * @Date: 2020/11/27 23:07
 */

class DynamicDetailActivity : BaseViewModelActivity<DynamicDetailViewModel>() {

    companion object {
        fun activityStart(fragment: Fragment, dynamicItem: View, data: Dynamic) {
            fragment.apply {
                activity?.let {
                    val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(it, dynamicItem, "dynamicItem")
                    val intent = Intent(context, DynamicDetailActivity::class.java)
                    intent.putExtra("dynamicItem", data)
                    it.window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                    startActivity(intent, opt.toBundle())
                }
            }
        }
    }

    override val isFragmentActivity = false

    override fun getViewModelFactory() = DynamicDetailViewModel.Factory()


    lateinit var dynamic: Dynamic


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_fragment_dynamic_detail)
        window.enterTransition = Slide(Gravity.END).apply { duration = 500 }


        qa_tv_toolbar_title.text = resources.getText(R.string.qa_dynamic_detail_title_text)

        initDynamic()
        initReplyList()

        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        window.returnTransition = Slide(Gravity.END).apply { duration = 500 }
        super.onBackPressed()
    }

    private fun initDynamic() {
        dynamic = intent.getParcelableExtra("dynamicItem")
        qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener {
            OptionalPopWindow.Builder().with(this)
                    .addOptionAndCallback("删除") {
                        Toast.makeText(BaseApp.context, "点击了删除", Toast.LENGTH_SHORT).show()
                    }.addOptionAndCallback("关注") {
                        Toast.makeText(BaseApp.context, "点击了关注", Toast.LENGTH_SHORT).show()
                    }.addOptionAndCallback("举报") {
                        Toast.makeText(BaseApp.context, "点击了举报", Toast.LENGTH_SHORT).show()
                    }.show(it, OptionalPopWindow.AlignMode.RIGHT, 0)
        }
        qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
            qa_iv_dynamic_praise_count_image.toggle()
        }
        qa_iv_dynamic_avatar.setAvatarImageFromUrl(DynamicAdapter.PIC_URL_BASE + dynamic.avatar)
        qa_tv_dynamic_topic.text = dynamic.topic
        qa_tv_dynamic_nickname.text = dynamic.nickName
        qa_tv_dynamic_content.text = dynamic.content
        qa_tv_dynamic_praise_count.text = dynamic.praiseCount.toString()
        qa_tv_dynamic_comment_count.text = dynamic.commentCount.toString()
        qa_tv_dynamic_publish_at.text = questionTimeDescription(System.currentTimeMillis(), dynamic.publishTime.toString().toDate().time)
        //解决图片错乱的问题
        if (dynamic.pics.isNullOrEmpty())
            qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_NORMAL_SIZE)
        else {
            dynamic.pics.map {
                DynamicAdapter.PIC_URL_BASE + it
            }.apply {
                val tag = qa_dynamic_nine_grid_view.tag
                if (null == tag || tag == this) {
                    val tagStore = qa_dynamic_nine_grid_view.tag
                    qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_NORMAL_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                    qa_dynamic_nine_grid_view.tag = tagStore
                } else {
                    val tagStore = this
                    qa_dynamic_nine_grid_view.tag = null
                    qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                    qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_NORMAL_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                    qa_dynamic_nine_grid_view.tag = tagStore
                }
            }

        }
        qa_dynamic_nine_grid_view.setOnItemClickListener { _, index ->
            ViewImageActivity.activityStart(this, dynamic.pics.map { DynamicAdapter.PIC_URL_BASE + it }.toTypedArray(), index)
        }
    }


    private fun initReplyList() {
        collapsing_toolbar_layout.post {
            // 将回复布局的顶部重置到帖子底下
//            qa_ll_reply.top = collapsing_toolbar_layout.height
//            qa_ll_reply.invalidate()
        }
    }


}