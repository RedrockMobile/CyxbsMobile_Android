package com.mredrock.cyxbs.discover.map.ui.fragment.inner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.databinding.MapFragmentPlaceDetailContainerBinding
import com.mredrock.cyxbs.discover.map.ui.adapter.BannerViewAdapter
import com.mredrock.cyxbs.discover.map.ui.adapter.DetailAttributeRvAdapter
import com.mredrock.cyxbs.discover.map.ui.adapter.DetailTagRvAdapter
import com.mredrock.cyxbs.discover.map.util.BannerPageTransformer
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.map_fragment_place_detail_container.*


class PlaceDetailBottomSheetFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var mBinding: MapFragmentPlaceDetailContainerBinding
    private var isFavoritePlace = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.map_fragment_place_detail_container, container, false)
//        mBinding.lifecycleOwner = viewLifecycleOwner
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)


        /**
         * 初始化adapter和layoutManager
         */

        val bannerViewAdapter = context?.let { BannerViewAdapter(it, mutableListOf()) }
        map_banner_detail_image.adapter = bannerViewAdapter
        map_banner_detail_image.offscreenPageLimit = 3
        map_banner_detail_image.pageMargin = 15
        map_banner_detail_image.setPageTransformer(true, BannerPageTransformer())
        map_banner_detail_image.start()


        val flexBoxManager = FlexboxLayoutManager(context)
        flexBoxManager.flexWrap = FlexWrap.WRAP
        map_rv_detail_about_list.layoutManager = flexBoxManager
        val tagAdapter = context?.let { DetailTagRvAdapter(it, mutableListOf()) }
        map_rv_detail_about_list.adapter = tagAdapter

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        map_rv_detail_place_attribute.layoutManager = linearLayoutManager
        val attributeAdapter = context?.let { DetailAttributeRvAdapter(it, mutableListOf()) }
        map_rv_detail_place_attribute.adapter = attributeAdapter


        /**
         * 对要显示的内容监听
         */
        viewModel.placeDetails.observe(
                viewLifecycleOwner,
                Observer { t ->
                    //数据绑定
                    mBinding.placeDetails = t
                    //数据传给adapter
                    if (bannerViewAdapter != null) {
                        if (t.images != null) {
                            bannerViewAdapter.setList(t.images!!)
                            bannerViewAdapter.notifyDataSetChanged()
                            map_banner_detail_image.adapter = bannerViewAdapter
                            map_indicator_detail.setupWithViewPager(map_banner_detail_image)
                        } else {
                            bannerViewAdapter.setList(listOf())
                            bannerViewAdapter.notifyDataSetChanged()
                            map_banner_detail_image.adapter = bannerViewAdapter
                            map_indicator_detail.setupWithViewPager(map_banner_detail_image)
                        }
                    }
                    if (tagAdapter != null && t.tags != null) {
                        tagAdapter.setList(t.tags!!)
                        tagAdapter.notifyDataSetChanged()
                    }
                    if (attributeAdapter != null) {
                        if (t.placeAttribute != null) {
                            attributeAdapter.setList(t.placeAttribute!!)
                            attributeAdapter.notifyDataSetChanged()
                        } else {
                            attributeAdapter.setList(listOf())
                            attributeAdapter.notifyDataSetChanged()
                        }

                    }
                    setNickName()

                }
        )
        viewModel.collectList.observe(viewLifecycleOwner, Observer {
            setNickName()
        })
        /**
         * 添加点击事件
         */
        val onClickListener = View.OnClickListener {
            context?.doIfLogin("收藏") {
                //不再打开收藏编辑页面，故注释
                //viewModel.fragmentFavoriteEditIsShowing.value = true
                if (isFavoritePlace) {
                    viewModel.deleteCollect(viewModel.showingPlaceId)
                    map_iv_detail_favorite.setImageResource(R.drawable.map_ic_no_like)
                } else {
                    viewModel.addCollect(viewModel.placeDetails.value?.placeName
                            ?: "我的收藏", viewModel.showingPlaceId)
                    map_iv_detail_favorite.setImageResource(R.drawable.map_ic_like)
                }
                viewModel.bottomSheetStatus.postValue(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
        map_iv_detail_favorite.setOnClickListener(onClickListener)
        map_tv_detail_place_nickname.setOnClickListener(onClickListener)
        map_tv_detail_more.setOnClickListener {
            viewModel.fragmentAllPictureIsShowing.value = true
        }

        map_tv_detail_share.setOnClickListener {
            context?.let { it1 -> viewModel.sharePicture(it1, this) }

        }
    }


    private fun setNickName() {
        //判断是否收藏过该地点，如果收藏了则显示出收藏的nickname
        viewModel.collectList.value?.let {
            var isFavor: String? = null
            for (favoritePlace: FavoritePlace in it) {
                if (viewModel.showingPlaceId == favoritePlace.placeId) {
                    isFavor = favoritePlace.placeNickname
                }
            }
            if (isFavor != null) {
                map_iv_detail_favorite.setImageResource(R.drawable.map_ic_like)
                map_tv_detail_place_nickname.visible()
                isFavoritePlace = true
                //不显示地点备注名了，故注释掉
                //map_tv_detail_place_nickname.text = isFavor
            } else {
                map_iv_detail_favorite.setImageResource(R.drawable.map_ic_no_like)
                map_tv_detail_place_nickname.gone()
                isFavoritePlace = false
            }
        }
    }



}