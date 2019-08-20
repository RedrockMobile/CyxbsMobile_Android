package com.mredrock.cyxbs.freshman.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.DormitoryAndCanteenMessage
import com.mredrock.cyxbs.freshman.util.GlideImageLoader
import com.youth.banner.Banner

class DormitoryAndCanteenFragment(var data: DormitoryAndCanteenMessage) : BaseFragment() {
    private lateinit var mPhotos: Banner
    private lateinit var mPlace: TextView
    private lateinit var mDescription: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.freshman_fragment_campus_guidelines_photos, container, false)
        mPhotos = view.findViewById(R.id.banner_campus_guidelines_photos)
        mPlace = view.findViewById(R.id.tv_campus_guidelines_place_photos)
        mDescription = view.findViewById(R.id.tv_campus_guidelines_description_photos)
        initView()
        return view
    }

    private fun initView() {
        mPhotos.isAutoPlay(true)
                .setImageLoader(GlideImageLoader)
                .setImages(data.photo)
                .start()
        mPlace.text = data.name
        mDescription.text = data.detail
    }
}