package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.utils.getSplashFile
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.main_view_stub_splash.*

/**
 * Created by Jovines.
 *
 * date : 2020/05/23 18:06
 * description : 节气以及引导页所在区域
 */

class SplashFragment : BaseFragment() {

    lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        return inflater.inflate(R.layout.main_view_stub_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
                .load(getSplashFile(requireContext()))
                .apply(RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(splash_view)
        object : CountDownTimer(3000, 1000) {
            override fun onFinish() {
                viewModel.splashVisibility.set(View.GONE)
            }

            override fun onTick(millisUntilFinished: Long) {
                requireActivity().runOnUiThread {
                    val str = "跳过 ${millisUntilFinished / 1000}"
                    main_activity_splash_skip.text = str
                }
            }
        }.start()
        main_activity_splash_skip.setOnClickListener {
            viewModel.splashVisibility.set(View.GONE)
        }
    }

}
