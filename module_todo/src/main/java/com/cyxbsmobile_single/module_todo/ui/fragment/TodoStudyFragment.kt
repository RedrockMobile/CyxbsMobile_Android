package com.cyxbsmobile_single.module_todo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyxbsmobile_single.module_todo.R
import com.mredrock.cyxbs.lib.base.ui.BaseFragment

/**
 * description ：清单下面四个页面之一
 * author :TaiE
 * email : 1607869392@qq.com
 * date : 2024/8/11 20:16
 * version: 1.0
 */
class TodoStudyFragment: BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.todo_fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

    }


}