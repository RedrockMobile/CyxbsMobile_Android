package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicDetailViewModel
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_fragment_dynamic_detail.*

/**
 * @Author: sandyz987
 * @Date: 2020/11/27 23:07
 */

class DynamicDetailFragment : BaseViewModelFragment<DynamicDetailViewModel>() {


    override fun getViewModelFactory() = DynamicDetailViewModel.Factory("迎新生")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.qa_fragment_dynamic_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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