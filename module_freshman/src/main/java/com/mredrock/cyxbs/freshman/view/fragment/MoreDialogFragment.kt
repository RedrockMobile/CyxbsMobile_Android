package com.mredrock.cyxbs.freshman.view.fragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mredrock.cyxbs.freshman.R

/**
 * Create by roger
 * on 2019/8/6
 */
class MoreDialogFragment : DialogFragment() {

    private lateinit var mListener: MoreDialogListener
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)
        val inflater: LayoutInflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.freshman_dialog_more_activity, null)
        builder.setView(view)
        view.setOnClickListener { mListener.onDialogClick() }
        view.setOnLongClickListener {
            mListener.onDialogLongClick()
            true
        }
        return builder.create()

    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mListener = activity as MoreDialogListener

    }
}

interface MoreDialogListener {
    fun onDialogLongClick()
    fun onDialogClick()
}