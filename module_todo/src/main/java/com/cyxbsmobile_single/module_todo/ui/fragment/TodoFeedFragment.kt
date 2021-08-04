package com.cyxbsmobile_single.module_todo.ui.fragment

import android.os.Bundle
import android.view.View
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.common.ui.BaseFeedFragment

/**
 * Author: RayleighZ
 * Time: 2021-08-02 10:15
 */
class TodoFeedFragment: BaseFeedFragment<TodoViewModel>() {
    override var hasTopSplitLine = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onRefresh() {
//        TODO("Not yet implemented")
    }

    private fun init(){

    }


}