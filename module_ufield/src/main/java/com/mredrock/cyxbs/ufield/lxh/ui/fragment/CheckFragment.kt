package com.mredrock.cyxbs.ufield.lxh.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lxh.adapter.MsgAdapter
import com.mredrock.cyxbs.ufield.lxh.viewmodel.MessageViewModel


class CheckFragment : BaseFragment() {
    private val mViewModel: MessageViewModel by activityViewModels()

    private val mHaveMessage by R.id.ufield_campaign_have_message.view<RelativeLayout>()

    private val mNoMessage by R.id.ufield_campaign_no_message.view<LinearLayoutCompat>()

    private val mRecyclerView by R.id.ufield_activity_campaign_rv_watch.view<RecyclerView>()

    private lateinit var adapter: MsgAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.checkMsg.observe(viewLifecycleOwner) {
            Log.d("hui", "onViewCreated:$it")
            if (it.isEmpty()) {
                mHaveMessage.gone()
                mNoMessage.visible()
            } else {
                adapter = MsgAdapter()
                mRecyclerView.adapter = adapter
                mRecyclerView.layoutManager = LinearLayoutManager(context)
                mHaveMessage.visible()
                adapter.submitList(it)
                mNoMessage.gone()
            }
        }
    }
}