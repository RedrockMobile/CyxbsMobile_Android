package com.mredrock.cyxbs.qa.pages.quiz.ui.dialog

import android.content.Context
import android.graphics.Color
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.ViewGroup
import org.jetbrains.anko.*

/**
 * Created By jay68 on 2018/11/12.
 */
class SelectImageDialog(context: Context) : BottomSheetDialog(context) {
    var selectImageFromAlbum: () -> Unit = {}
    var selectImageFromCamera: () -> Unit = {}

    init {
        val contentView = context.verticalLayout {
            setPadding(dip(15), dip(20), dip(15), dip(20))

            textView("从相册选取") {
                textColor = Color.parseColor("#f27195fa")
                textSize = 16f
                setOnClickListener {
                    dismiss()
                    selectImageFromAlbum()
                }
            }

            view {
                setBackgroundColor(Color.parseColor("#1f000000"))
            }.lparams(ViewGroup.LayoutParams.MATCH_PARENT, dip(1)) {
                setMargins(0, dip(20), 0, dip(20))
            }

            textView("拍照") {
                textColor = Color.parseColor("#f27195fa")
                textSize = 16f
                setOnClickListener {
                    dismiss()
                    selectImageFromCamera()
                }
            }
        }
        setContentView(contentView)
    }
}