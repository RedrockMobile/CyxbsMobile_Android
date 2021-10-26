package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AuthenticationStatus
import com.mredrock.cyxbs.mine.network.model.Status
import com.mredrock.cyxbs.mine.page.mine.adapter.IdentityAdapter
import com.mredrock.cyxbs.mine.page.mine.adapter.StatusAdapter
import com.mredrock.cyxbs.mine.page.mine.callback.DiffCallBack
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity.onGetRedid
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.IdentityFragment.*
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel
import kotlinx.android.synthetic.main.mine_fragment_approve.view.*
import kotlinx.android.synthetic.main.mine_fragment_identify.*
import kotlinx.android.synthetic.main.mine_fragment_identify.view.*

class IdentityFragment(

) : BaseViewModelFragment<IdentityViewModel>(), onGetRedid {

    var oldList=mutableListOf<AuthenticationStatus.Data>()
    private var redid: String? = null
    private var isSelf=true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initListener()
        val view = inflater.inflate(R.layout.mine_fragment_identify, container, false)
        initData(view)

        return view
    }


    override fun onSuccesss(redid: String,isself:Boolean) {
        this.redid = redid
        this.isSelf=isself
      Log.e("wxtag自我刷新","(IdentityFragment.kt:51)->> redid$redid isself=$isSelf")
        refresh()
    }

    fun initListener() {
        (context as HomepageActivity).viewModel.redRockApiStatusDelete.observeForever {
            activity?.toast("删除身份成功!")
            viewModel.getAllIdentify(redid)
        }
    }

    fun initData(view: View) {
        val list = mutableListOf<AuthenticationStatus.Data>()
        viewModel.getAllIdentify(redid)
        viewModel.allIdentifies.observeForever {
            list.clear()
            it.data.authentication.forEach {
                list.add(it)
            }
            it.data.customization.forEach {
                list.add(it)
            }
            if (view.rv_identity.adapter==null){
                view.rv_identity.adapter = context?.let {
                    redid?.let { it1 -> IdentityAdapter(list, it, it1, isSelf) }
                }
                view.rv_identity.layoutAnimation=//入场动画
                    LayoutAnimationController(
                        AnimationUtils.loadAnimation(context,R.anim.rv_load_anim)
                    )
                view.rv_identity.layoutManager = LinearLayoutManager(context)
            }else{
                val diffResult = DiffUtil.calculateDiff(DiffCallBack(oldList, list), true)
                (view.rv_identity.adapter as StatusAdapter).list=list
                diffResult.dispatchUpdatesTo(view.rv_approve.adapter as StatusAdapter)
            }
            oldList.clear()
            oldList = list

        }
        /**
         * 身份信息发生错误的情况
         */
        viewModel.onErrorAction.observeForever {
            view.rv_identity.adapter = context?.let { it ->
                IdentityAdapter(list, it, redid, isSelf)
            }
            view.rv_identity.layoutManager = LinearLayoutManager(context)
        }
    }

    fun refresh(){
        viewModel.getAllIdentify(redid)
    }


}