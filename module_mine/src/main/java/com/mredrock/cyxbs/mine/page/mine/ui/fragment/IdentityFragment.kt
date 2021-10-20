package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity.onGetRedid
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.IdentityFragment.*
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel
import kotlinx.android.synthetic.main.mine_fragment_identify.*
import kotlinx.android.synthetic.main.mine_fragment_identify.view.*

class IdentityFragment(

) : BaseViewModelFragment<IdentityViewModel>(), onGetRedid {


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
        this.isSelf=isSelf
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
            it.data.authentication.forEach {
                list.add(it)
            }
            it.data.customization.forEach {
                list.add(it)
            }
            view.rv_identity.adapter = context?.let {
                redid?.let { it1 -> IdentityAdapter(list, it, it1, false) }
            }
            view.rv_identity.layoutManager = LinearLayoutManager(context)
        }
        /**
         * 身份信息发生错误的情况
         */
        viewModel.onErrorAction.observeForever {
            view.rv_identity.adapter = context?.let { it ->
                IdentityAdapter(list, it, redid, false)
            }
            view.rv_identity.layoutManager = LinearLayoutManager(context)
        }
    }

    fun refresh(){
        viewModel.getAllIdentify(redid)
    }


}