package com.mredrock.cyxbs.discover.map.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.ui.activity.ShowPictureActivity
import com.mredrock.cyxbs.discover.map.ui.adapter.AllPictureRvAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel


class AllPictureFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var allPictureAdapter: AllPictureRvAdapter
    private val imageData = mutableListOf<String>()

    private val mTvAllPicture by R.id.map_tv_all_picture.view<TextView>()
    private val mRvAllPicture by R.id.map_rv_all_picture.view<RecyclerView>()
    private val mIvAllPictureBack by R.id.map_iv_all_picture_back.view<ImageView>()
    private val mTvAllPictureShare by R.id.map_tv_all_picture_share.view<TextView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_all_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        mTvAllPicture.alpha = 0f
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        mRvAllPicture.layoutManager = staggeredGridLayoutManager
        allPictureAdapter = context?.let { AllPictureRvAdapter(it, mutableListOf()) } ?: return

        allPictureAdapter.setOnItemClickListener(object : AllPictureRvAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
//                val transaction = manager?.beginTransaction()?.setCustomAnimations(
//                        R.animator.map_slide_from_right,
//                        R.animator.map_slide_to_left,
//                        R.animator.map_slide_from_left,
//                        R.animator.map_slide_to_right)
//                transaction?.add(R.id.map_root_all_picture, ShowPictureFragment::class.java, bundleOf(Pair("picturePosition", position)))
//                transaction?.addToBackStack("showPicture")?.commit()
                ShowPictureActivity.activityStart(activity, ArrayList(viewModel.placeDetails.value?.images?: listOf()), position)
            }

        })

        mRvAllPicture.adapter = allPictureAdapter

        mIvAllPictureBack.setOnSingleClickListener {
            viewModel.fragmentAllPictureIsShowing.value = false
        }


        mRvAllPicture.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, state: Int) {
                // 如果停止滑动
                if (state == SCROLL_STATE_IDLE) {
                    // 获取布局管理器
                    val layout = recyclerView.layoutManager as StaggeredGridLayoutManager
                    // 用来记录lastItem的position
                    // 由于瀑布流有多个列 所以此处用数组存储
                    val positions = IntArray(4)
                    // 获取lastItem的positions
                    /**
                     * 其他布局管理器可使用同样方式获取
                     */
                    layout.findLastVisibleItemPositions(positions)
                    for (i in positions.indices) {
                        /**
                         * 判断lastItem的底边到recyclerView顶部的距离
                         * 是否小于recyclerView的高度
                         * 如果小于或等于 说明滚动到了底部
                         */
                        if (layout.findViewByPosition(positions[i])?.bottom ?: 0 <= recyclerView.height) {
                            /**
                             * 此处实现业务逻辑
                             */
                            mTvAllPicture.animate().alpha(1f).duration = 500
                            return
                        } else {
                            mTvAllPicture.animate().alpha(0f).duration = 500
                        }
                    }
                }
                super.onScrollStateChanged(recyclerView, state)
            }
        })

        mTvAllPictureShare.setOnSingleClickListener {
            context?.let { it1 -> viewModel.sharePicture(it1, this) }
        }

    }

    override fun onResume() {
        super.onResume()
        if (viewModel.placeDetails.value?.images != null) {
            imageData.clear()
            imageData.addAll(viewModel.placeDetails.value?.images!!)
            allPictureAdapter.setList(viewModel.placeDetails.value?.images!!)
            allPictureAdapter.notifyDataSetChanged()
        }
    }


}