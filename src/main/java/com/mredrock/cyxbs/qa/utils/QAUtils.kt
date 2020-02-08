package com.mredrock.cyxbs.qa.utils

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import org.jetbrains.anko.textColor

/**
 * Created By jay68 on 2018/9/30.
 */
internal fun TextView.setNicknameTv(nickname: String, showGender: Boolean, isMale: Boolean) {
    //为了防止后端传null
    text = nickname ?: ""
    if (showGender) {
        val drawable = if (isMale) {
            ContextCompat.getDrawable(context, R.drawable.qa_ic_gender_male)
        } else {
            ContextCompat.getDrawable(context, R.drawable.qa_ic_gender_female)
        }
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
    } else {
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
    }
}

internal fun TextView.setPraise(praiseNum: String?,
                                isPraised: Boolean?,
                                praiseIcon: Int = R.drawable.qa_ic_answer_list_praise,
                                praisedIcon: Int = R.drawable.qa_ic_answer_list_praised) {
    if (praiseNum != null) {
        text = praiseNum
    }
    isPraised ?: return
    val drawable = if (isPraised) {
        ContextCompat.getDrawable(context, praisedIcon)
    } else {
        ContextCompat.getDrawable(context, praiseIcon)
    }
    setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
}

internal fun TextView.unSelected() {
    textColor = ContextCompat.getColor(context, R.color.qa_quiz_select_type_text_color)
    background = ContextCompat.getDrawable(context, R.drawable.qa_selector_quiz_type_default_select)

}

internal fun TextView.selected() {
    textColor = ContextCompat.getColor(context, R.color.qa_quiz_selected_type_text_color)
    background = ContextCompat.getDrawable(context, R.drawable.qa_selector_quiz_type_default_selected)

}

internal fun setAdoptedTv(adopedTv: TextView, adoptTv: TextView, isAdopted: Boolean, showAdoptIcon: Boolean) = when {
    isAdopted -> {
        adoptTv.gone()
        adopedTv.visible()
    }

    showAdoptIcon -> {
        adoptTv.gone()
        adopedTv.gone()
    }

    else -> {
        adoptTv.visible()
        adopedTv.gone()
    }
}

internal fun <T> List<T>?.isNullOrEmpty() = this == null || this.isEmpty()