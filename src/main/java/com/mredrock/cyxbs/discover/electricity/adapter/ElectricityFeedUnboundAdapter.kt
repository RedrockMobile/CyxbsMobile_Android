package com.mredrock.cyxbs.discover.electricity.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.electricity.R
import org.jetbrains.anko.layoutInflater

class ElectricityFeedUnboundAdapter : BaseFeedFragment.Adapter() {
    override fun onCreateView(context: Context, parent: ViewGroup): View =
            context.layoutInflater.inflate(R.layout.electricity_discover_feed_unbound, parent, false)
}