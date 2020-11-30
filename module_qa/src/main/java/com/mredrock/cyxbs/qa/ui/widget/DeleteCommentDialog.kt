package com.mredrock.cyxbs.qa.ui.widget

import android.app.Dialog
import android.content.Context
import com.mredrock.cyxbs.qa.R

/**
 *@Date 2020-11-24
 *@Time 21:39
 *@author SpreadWater
 *@description  删除评论的dialog
 */
class DeleteCommentDialog(context: Context?,theme:Int):Dialog(context!!,theme) {
    companion object{
        private var deleteCommentDialog :DeleteCommentDialog?=null
        fun show(context: Context?){
            if (context==null)return
            if (deleteCommentDialog==null){
                deleteCommentDialog= DeleteCommentDialog(context, R.style.qa_transparent_dialog)
            }
//            deleteCommentDialog!!.setContentView()

        }
    }
}