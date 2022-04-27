package com.redrock.module_notification.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.redrock.module_notification.R

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 16:39.
 *
 */
class DialogBuilder(private val context: Context) {

    private var onPositiveClick: (() -> Unit)? = null
    private var onNegativeClick: (() -> Unit)? = null
    private lateinit var inflater: LayoutInflater
    private lateinit var dialog: Dialog
    private lateinit var view: View

    @SuppressLint("InflateParams", "ResourceType")
    fun buildDialog(
        @LayoutRes LayoutRes: Int,
        width: Int,
        height: Int
    ) :DialogBuilder{
        val builder = android.app.AlertDialog.Builder(context)
        inflater = LayoutInflater.from(context)
        view = inflater.inflate(12, null)
        dialog = builder.create()

        dialog.show()

        dialog.window?.apply {
            setContentView(view)
            //make sure that the res view background will work right
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            //set window attrs
            val attributes = this.attributes
            attributes?.width = width
            attributes?.height = height
            this.attributes = attributes
        }

        return this
    }

    /**
     * 设置点击事件，提供要设置点击事件的View的id
     */
    fun setClickEvent(viewId: Int, clickEvent: (() -> Unit)):DialogBuilder {
        val clickView = view.findViewById<View>(viewId)

        clickView.setOnClickListener {
            clickEvent.invoke()
        }

        return this
    }

    /**
     * 设置让dialog dismss的view的点击事件
     * 之所以不用setClickEvent是因为没有dialog的上下文，记得好像有一种方法可以让高阶函数有上下文，下次再研究吧！
     */
    fun setCancelEvent(viewId:Int):DialogBuilder{
        val clickView = view.findViewById<View>(viewId)

        clickView.setOnClickListener {
            dialog.dismiss()
        }

        return this
    }

    fun setWindowAnim(res:Int):DialogBuilder{
        dialog.window?.setWindowAnimations(res)
        return this
    }

}