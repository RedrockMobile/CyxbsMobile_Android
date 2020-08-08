package com.mredrock.cyxbs.qa.utils

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import org.jetbrains.anko.textColor

/**
 * Created By jay68 on 2018/9/30.
 */
internal fun TextView.setPraise(praiseNum: String?,
                                isPraised: Boolean?,
                                praiseIcon: Int = R.drawable.qa_ic_answer_list_praise,
                                praisedIcon: Int = R.drawable.qa_ic_answer_list_praised,
                                praiseColor: Int = R.color.common_qa_answer_praise_count_color,
                                praisedColor: Int = R.color.common_qa_answer_praised_count_color) {
    if (praiseNum != null) {
        text = praiseNum
    }
    isPraised ?: return
    val drawable = if (isPraised) {
        ContextCompat.getDrawable(context, praisedIcon)
    } else {
        ContextCompat.getDrawable(context, praiseIcon)
    }
    textColor = if (isPraised) {
        ContextCompat.getColor(context, praisedColor)
    } else {
        ContextCompat.getColor(context, praiseColor)
    }
    setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
}

internal fun TextView.unSelected() {
    textColor = ContextCompat.getColor(context, R.color.common_qa_quiz_select_type_text_color)
    background = ContextCompat.getDrawable(context, R.drawable.qa_selector_quiz_type_default_select)

}

internal fun TextView.selected() {
    textColor = ContextCompat.getColor(context, R.color.common_qa_quiz_selected_type_text_color)
    background = ContextCompat.getDrawable(context, R.drawable.qa_selector_quiz_type_default_selected)

}

internal fun setAdoptedTv(adoptedTv: TextView, adoptTv: TextView, isAdopted: Boolean, removeAdoptIcon: Boolean) = when {
    isAdopted -> {
        adoptTv.gone()
        adoptedTv.visible()
    }

    removeAdoptIcon -> {
        adoptTv.gone()
        adoptedTv.gone()
    }

    else -> {
        adoptTv.visible()
        adoptedTv.gone()
    }
}

internal fun View.setVisibleCondition(visibleCondition: Boolean) = when {
    visibleCondition -> {
        this.visible()
    }
    else -> {
        this.gone()
    }
}

internal fun View.setInvisibleCondition(invisibleCondition: Boolean) = when {
    invisibleCondition -> {
        this.gone()
    }
    else -> {
        this.visible()
    }
}

internal fun <T> List<T>?.isNullOrEmpty() = this == null || this.isEmpty()