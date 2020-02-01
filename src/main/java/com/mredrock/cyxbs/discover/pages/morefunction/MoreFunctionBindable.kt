package com.mredrock.cyxbs.discover.pages.morefunction

import android.content.Context
import android.widget.ImageView
import com.mredrock.cyxbs.discover.R


class MoreFunctionBindable:Bindable {
    override fun onBindViewHolder(holder: MoreFunctionRvAdapter.MoreFunctionViewHolder, position: Int, adapter: MoreFunctionRvAdapter) {
        adapter.getFunctions()[position].activityStarter.startActivity()
    }

    override fun setMoreFunctionIcon(imageView: ImageView) {
        imageView.setImageResource(R.drawable.discover_ic_more_function_finish)
    }

    override fun settingClicked(adapter: MoreFunctionRvAdapter, context: Context,imageView: ImageView) {
        setMoreFunctionIcon(imageView)
    }


}