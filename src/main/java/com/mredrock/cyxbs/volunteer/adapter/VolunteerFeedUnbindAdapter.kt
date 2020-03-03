package com.mredrock.cyxbs.volunteer.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.volunteer.R
import org.jetbrains.anko.layoutInflater

class VolunteerFeedUnbindAdapter : BaseFeedFragment.Adapter() {
    override fun onCreateView(context: Context, parent: ViewGroup): View =
            context.layoutInflater.inflate(R.layout.volunteer_discover_feed_unbound, parent, false)


}