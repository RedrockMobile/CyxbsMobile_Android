package com.mredrock.cyxbs.noclass.page.course

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.api.affair.DateJson
import com.mredrock.cyxbs.api.course.utils.getBeginLesson
import com.mredrock.cyxbs.lib.course.helper.affair.view.TouchAffairView
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.helper.expand.ISingleSideExpandable
import com.mredrock.cyxbs.noclass.page.ui.dialog.NoClassGatherDialog


class NoClassTouchAffairItem(
    context: Context,
    private val nameMap: HashMap<String, String>,
    private val week: Int
) : TouchAffairView(context) {

    init {
        // 将含有加号的图片置空
        mImageView.setImageDrawable(null)
    }

    override fun cancelShow() {
        startAnimation(AlphaAnimation(1F, 0F).apply {
            duration = 500
        })
        (parent as ViewGroup).removeView(this)
    }

    override fun newSideExpandable(): ISingleSideExpandable {
        return super.newSideExpandable().apply {
            addAnimListener(object : ISingleSideExpandable.OnAnimListener {
                override fun onAnimEnd(
                    side: ISingleSideExpandable,
                    course: ICourseViewGroup,
                    item: IItem,
                    child: View
                ) {
                    super.onAnimEnd(side, course, item, child)
                    // 这里取消的原因是slideExpandable的属性动画会和cancelShow的补间动画发生冲突
                    toastGatheringDialog()
                }
            })
        }
    }

    /**
     * 弹出分组dialog，显示0忙碌
     */
    private fun toastGatheringDialog() {
        val weekNum = lp.weekNum  // 周几

        val duration = lp.length  //课长度
        //开始与结束序列
        val begin = lp.startRow   //得到开始的节数
        val end = begin + duration - 1

        val beginTime = com.mredrock.cyxbs.api.course.utils.getShowStartTimeStr(begin)
        val endTime = com.mredrock.cyxbs.api.course.utils.getShowEndTimeStr(end)

        val beginLesson = if (begin >= 10) begin - 1 else if (begin <= 3) begin + 1 else begin

        val month = when (weekNum) {
            1 -> "周一"
            2 -> "周二"
            3 -> "周三"
            4 -> "周四"
            5 -> "周五"
            6 -> "周六"
            7 -> "周日"
            else -> ""
        }

        val specialBeginLesson = getBeginLesson(begin)

        val timeText =when (specialBeginLesson){
            -1 -> "中午"
            -2 -> "傍晚"
            else -> "${beginLesson}-${beginLesson + duration - 1}"
        }

        val textTime = "$month $timeText ${beginTime}-${endTime}"

        val dateJson = DateJson(specialBeginLesson, weekNum, duration, week)
        val mNumNameIsSpare = hashMapOf<Pair<String, String>, Boolean>()
        nameMap.forEach { (id, name) ->
            mNumNameIsSpare[id to name] = true
        }
        NoClassGatherDialog.newInstance(
            dateJson,
            mNumNameIsSpare,
            textTime
        ).apply {
            setClickBlankCancel { cancelShow() }
        }.show((context as AppCompatActivity).supportFragmentManager, "NoClassGatherDialog")
    }
}