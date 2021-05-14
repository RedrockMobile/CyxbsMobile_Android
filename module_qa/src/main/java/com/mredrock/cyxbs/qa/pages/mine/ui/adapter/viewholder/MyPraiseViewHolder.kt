package com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder

import android.view.View
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.beannew.Praise
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_my_praise.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author: RayleighZ
 * Time: 2021-03-11 1:12
 */
class MyPraiseViewHolder(item: View): BaseViewHolder<Praise>(item) {
    override fun refresh(data: Praise?) {
        data?.apply {
            itemView.apply {
                //2021-05-13T23:27:49+08:00
                val dateFormat =
                if (type == 0){//如果是对动态的点赞
                   SimpleDateFormat("赞了你的动态      yyyy.MM.dd  HH:mm", Locale.getDefault())
                } else {
                    SimpleDateFormat("赞了你的评论      yyyy.MM.dd  HH:mm", Locale.getDefault())
                }
                val dateDecodeFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssXXX", Locale.getDefault())
                val date = dateDecodeFormat.parse(time)
                qa_circleimageview_my_praise_avatar.setAvatarImageFromUrl(avatar)
                qa_tv_my_praise_nick_name.text = nickName
                qa_tv_my_praise_content.text = from
                qa_tv_my_praise_reply_time.text = dateFormat.format(date)
            }
        }
    }
}