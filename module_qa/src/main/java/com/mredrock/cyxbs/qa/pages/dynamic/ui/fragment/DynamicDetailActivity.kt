package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicDetailViewModel
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_fragment_dynamic_detail.*

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
                    startActivity(intent, opt.toBundle())
                }
            }
        }
    }

    override val isFragmentActivity = false

    override fun getViewModelFactory() = DynamicDetailViewModel.Factory("迎新生")


    var dynamic: Dynamic? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_fragment_dynamic_detail)

        dynamic = intent.getParcelableExtra("dynamicItem")

        qa_tv_toolbar_title.text = resources.getText(R.string.qa_dynamic_detail_title_text)
        initReplyList()
    }


    private fun initReplyList() {
        collapsing_toolbar_layout.post {
            // 将回复布局的顶部重置到帖子底下
//            qa_ll_reply.top = collapsing_toolbar_layout.height
//            qa_ll_reply.invalidate()
        }
    }


}