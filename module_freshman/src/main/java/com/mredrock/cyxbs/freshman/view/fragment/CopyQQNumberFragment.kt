package com.mredrock.cyxbs.freshman.view.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.config.BUNDLE_DATA
import com.mredrock.cyxbs.freshman.config.BUNDLE_NAME
import com.mredrock.cyxbs.freshman.util.event.OnCopyQQNumberSuccessEvent
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.act

/**
 * Create by yuanbing
 * on 2019/8/4
 */
class CopyQQNumberFragment : BaseFragment() {
    private lateinit var mClipboardManager: ClipboardManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mClipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val view = inflater.inflate(R.layout.freshman_fragment_copy_qq_number, container, false)
        initView(view)
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun initView(view: View) {
        val name: TextView = view.findViewById(R.id.tv_copy_qq_number_name)
        val data: TextView = view.findViewById(R.id.tv_copy_qq_number_data)
        val cancel: TextView = view.findViewById(R.id.tv_copy_qq_number_cancel)
        val copy: TextView = view.findViewById(R.id.tv_copy_qq_number_copy)

        name.text = arguments?.getString(BUNDLE_NAME)
        data.text = "QQ群：${arguments?.getString(BUNDLE_DATA)}"

        cancel.setOnClickListener { activity?.finish() }
        copy.setOnClickListener {
            val clipData = ClipData.newPlainText("", arguments?.getString(BUNDLE_DATA))
            mClipboardManager.primaryClip = clipData
            EventBus.getDefault().post(OnCopyQQNumberSuccessEvent())
        }
    }
}