package com.mredrock.cyxbs.qa.utils

import android.widget.TextView
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R

/**
 * Created By jay68 on 2018/9/30.
 */
internal fun TextView.setNicknameTv(nickname: String, showGender: Boolean, isMale: Boolean) {
    //为了防止后端传null
    text = nickname?:""
    if (showGender) {
        val drawable = if (isMale) {
            context.resources.getDrawable(R.drawable.qa_ic_gender_male)
        } else {
            context.resources.getDrawable(R.drawable.qa_ic_gender_female)
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
        context.resources.getDrawable(praisedIcon)
    } else {
        context.resources.getDrawable(praiseIcon)
    }
    setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
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