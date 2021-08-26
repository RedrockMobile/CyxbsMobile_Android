package com.mredrock.cyxbs.mine.page.feedback.history.list

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHistoryListBinding
import com.mredrock.cyxbs.mine.page.feedback.adapter.RvListAdapter
import com.mredrock.cyxbs.mine.page.feedback.adapter.rv.RvAdapter
import com.mredrock.cyxbs.mine.page.feedback.adapter.rv.RvBinder
import com.mredrock.cyxbs.mine.page.feedback.base.ui.BaseMVPVMActivity
import com.mredrock.cyxbs.mine.page.feedback.history.detail.HistoryDetailActivity
import com.mredrock.cyxbs.mine.page.feedback.history.list.adapter.PicBannerBinderAdd
import com.mredrock.cyxbs.mine.page.feedback.history.list.adapter.PicBannerBinderPic
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.History
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.Pic
import com.mredrock.cyxbs.mine.page.feedback.utils.CHOOSE_FEED_BACK_PIC
import com.mredrock.cyxbs.mine.page.feedback.utils.selectImageFromAlbum
import com.mredrock.cyxbs.mine.page.feedback.utils.setSelectedPhotos
import java.util.*

class HistoryListActivity :
    BaseMVPVMActivity<HistoryListViewModel, MineActivityHistoryListBinding, HistoryListPresenter>() {

    /**
     * 初始化两个adapter
     */
    private val rvAdapter by lazy {
        RvListAdapter()
    }

    private val rvPicAdapter by lazy {
        RvAdapter()
    }

    /**
     * 获取P层
     */
    override fun createPresenter(): HistoryListPresenter = HistoryListPresenter()

    /**
     * 获取布局信息
     */
    override fun getLayoutId(): Int = R.layout.mine_activity_history_list

    /**
     * 初始化view
     */
    override fun initView() {
        binding?.apply {
            vm = viewModel
            rvHistoryList.apply {
                adapter = rvAdapter
                layoutManager = LinearLayoutManager(this@HistoryListActivity)
            }
            rvBanner.apply {
                adapter = rvPicAdapter
                layoutManager = GridLayoutManager(this@HistoryListActivity, 3)
            }
        }
    }

    /**
     * 初始化listener
     */
    override fun initListener() {
        rvAdapter.setOnItemClickListener(
            object : RvListAdapter.ItemClickListener {
                var tag: Long = 0L
                override fun clicked(view: View, data: History) {
                    //防止多次点击
                    val current = System.currentTimeMillis()
                    if (current - tag < 500) return
                    tag = current
                    presenter?.savedState(data)
                    toast(data.toString())
                    val intentExtra = Intent(this@HistoryListActivity,
                        HistoryDetailActivity::class.java).putExtra("id", data.id)
                    startActivity(intentExtra)
                }

            }
        )
        binding?.includeToolBar?.fabBack?.setOnSingleClickListener {
            finish()
        }
    }

    /**
     * 监听事件
     */
    override fun observeData() {
        viewModel.apply {
            observeRvList(listData)
    ///////////////////////////////////////
            observePics(uris)
        }
    }

    ///////////////////////////////////////
    private fun observePics(uris: LiveData<List<Uri>>) {
        uris.observe({lifecycle}) {
            val list = mutableListOf<RvBinder<*>>().apply {
                //添加上传的图片
                addAll(
                    it.map {
                        Pic(it)
                    }.map {
                        PicBannerBinderPic(it).apply {
                            setOnContentClickListener { view, i ->
                                toast("内容被点击")
                            }
                            setOnIconClickListener { view, i ->
                                toast("删除被点击")
                                presenter?.removePic(i)
                            }
                        }
                    }
                )
                //添加Add的图标
                if (size < 3) {
                    add(
                        PicBannerBinderAdd().apply {
                            setClickListener { view, i ->
                                val list = ArrayList(viewModel?.uris?.value)
                                setSelectedPhotos(list)
                                toast("添加按钮被点击")
                                this@HistoryListActivity.selectImageFromAlbum(3)
                            }
                        }
                    )
                }
            }
            rvPicAdapter.submitList(list)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_FEED_BACK_PIC) {
            presenter?.dealPic(data)
        }
    }

    private fun observeRvList(listData: LiveData<List<History>>) {
        listData.observe({ lifecycle }) {
            rvAdapter.submitList(it)
        }
    }

}