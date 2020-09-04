package com.mredrock.cyxbs.volunteer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        view = inflater.inflate(R.layout.fragment_volunteer_notime, container, false)
        return view
    }

//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        if (isEnd)
//        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (listListener.checkListStatus() && !isRecord){
//                    isRecord = true;
//                    moveBeginY = motionEvent.getY();
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                    currentY = motionEvent.getY();
//                    passLengthY = currentY - moveBeginY;
//        }
//    }

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