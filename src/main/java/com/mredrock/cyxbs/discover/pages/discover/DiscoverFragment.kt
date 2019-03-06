package com.mredrock.cyxbs.discover.pages.discover

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.*
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_ENTRY
import com.mredrock.cyxbs.common.event.DiscoverOptionIconClickEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.discover.KEY_SP_HIDING_DISCOVER
import com.mredrock.cyxbs.discover.KEY_SP_HIDING_DISCOVER_UPDATE
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.SP_HIDING_DISCOVER
import com.mredrock.cyxbs.discover.pages.hiding.DiscoverHidingActivity
import com.mredrock.cyxbs.discover.utils.RollViewAdapter
import com.mredrock.cyxbs.discover.utils.RollerView
import kotlinx.android.synthetic.main.discover_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by zxzhu
 *   2018/9/7.
 *   发现
 */
@Route(path = DISCOVER_ENTRY)
class DiscoverFragment : BaseViewModelFragment<DiscoverViewModel>() {

    private var mListHide: MutableList<Boolean> = mutableListOf()
    private var mAdapter: DiscoverMainRvAdapter? = null

    private val menuListener = MenuItem.OnMenuItemClickListener {
        when (it?.itemId) {
            R.id.discover_manager -> {
                startActivity<DiscoverHidingActivity>()
            }
        }
        return@OnMenuItemClickListener false
    }

    override val viewModelClass = DiscoverViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.discover_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRollerView(discover_rollerView)
        initRv()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        activity?.menuInflater?.inflate(R.menu.discover_main_menu, menu)
        menu?.getItem(0)?.setOnMenuItemClickListener(menuListener)
        super.onPrepareOptionsMenu(menu)
    }

    private fun initRv() {
        mListHide = mutableListOf(true, true, true, true,
                true, true, true, true, true, true, true)
        val sp = context!!.sharedPreferences(SP_HIDING_DISCOVER)
        for (i in 0..10) {
            mListHide[i] = sp.getBoolean("$KEY_SP_HIDING_DISCOVER$i", true)
        }
        mAdapter = DiscoverMainRvAdapter(context!!)
        mAdapter!!.refreshData(mListHide)
        discover_rv_main.layoutManager = GridLayoutManager(context, 3)
        discover_rv_main.adapter = mAdapter
    }

    override fun onResume() {
        checkDiscoverHiding()
        super.onResume()
    }

    private fun checkDiscoverHiding() {
        val sp = context!!.sharedPreferences(SP_HIDING_DISCOVER)
        //有更新才刷新
        val hasUpdate = sp.getBoolean(KEY_SP_HIDING_DISCOVER_UPDATE, false)
        if (!hasUpdate) return
        for (i in 0..10) {
            mListHide[i] = sp.getBoolean("$KEY_SP_HIDING_DISCOVER$i", true)
        }
        sp.editor {
            putBoolean(KEY_SP_HIDING_DISCOVER_UPDATE, false)
            commit()
        }
        refreshAdapter()
    }

    private fun refreshAdapter() {
        mAdapter!!.refreshData(mListHide)
    }

    private fun setupRollerView(rollerView: RollerView) {
        viewModel.getRollInfos()

        rollerView.setAdapter(RollViewAdapter(context!!, intArrayOf(R.drawable.img_discover_cqupt1,
                R.drawable.img_discover_cqupt2,
                R.drawable.img_discover_cqupt3,
                R.drawable.img_discover_cqupt1,
                R.drawable.img_discover_cqupt2,
                R.drawable.img_discover_cqupt3)))

        viewModel.rollInfos.observe(this, Observer { rollerView.setAdapter(RollViewAdapter(context!!, it)) })
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onDiscoverOptionIconClickEvent(event: DiscoverOptionIconClickEvent) {
        startActivity<DiscoverHidingActivity>()
    }
}