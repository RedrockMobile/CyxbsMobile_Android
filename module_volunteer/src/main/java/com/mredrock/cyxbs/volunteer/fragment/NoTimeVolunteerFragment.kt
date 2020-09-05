package com.mredrock.cyxbs.volunteer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.volunteer.R

class NoTimeVolunteerFragment : BaseFragment() {

    internal var view: View? = null
    private val currentY: Float = 0.toFloat()
    private val moveBeginY: Float = 0.toFloat()
    private val passLengthY: Float = 0.toFloat()

    private val isEnd = true
    private val isRecord = false

    private var infoListener: RefreshInfoListener? = null
    private var listListener: RefreshListStatusListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.volunteer_fragment_volunteer_notime, container, false)
        return view
    }


    fun getListListener(): RefreshListStatusListener? {
        return listListener
    }

    fun setListListener(listListener: RefreshListStatusListener) {
        this.listListener = listListener
    }

    fun getInfoListener(): RefreshInfoListener? {

        return infoListener
    }

    fun setInfoListener(infoListener: RefreshInfoListener) {
        this.infoListener = infoListener
    }

    interface RefreshInfoListener {
        fun callRefreshAgain()
    }

    interface RefreshListStatusListener {
        fun checkListStatus(): Boolean
    }
}