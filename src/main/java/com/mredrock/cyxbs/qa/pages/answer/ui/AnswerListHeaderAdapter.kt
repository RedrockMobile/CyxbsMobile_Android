package com.mredrock.cyxbs.qa.pages.answer.ui

import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.utils.setNicknameTv
import com.mredrock.cyxbs.qa.utils.timeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_popup_window_answer_list_sort_by.view.*
import kotlinx.android.synthetic.main.qa_recycler_item_answer_header.view.*
import org.jetbrains.anko.layoutInflater

/**
 * Created By jay68 on 2018/9/30.
 */
class AnswerListHeaderAdapter(private val onSortOrderChangedListener: (sortOrder: String) -> Unit) : BaseRvAdapter<Question>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HeaderViewHolder(parent).apply {
        onSortOrderChangedListener = this@AnswerListHeaderAdapter.onSortOrderChangedListener
    }

    class HeaderViewHolder(parent: ViewGroup) : BaseViewHolder<Question>(parent, R.layout.qa_recycler_item_answer_header) {
        private val sortOrderContainer: View by lazy { initMenuContentView() }
        private val popupWindow: PopupWindow by lazy {
            PopupWindow(sortOrderContainer,
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    true).init()
        }

        var onSortOrderChangedListener: ((sortOrder: String) -> Unit)? = null

        override fun refresh(data: Question?) {
            data ?: return
            itemView.apply {
                tv_question_title.text = data.title
                tv_question_content.text = Html.fromHtml("${getTagHtml(data.tags)}${data.description}")
                //todo load image
                iv_questioner_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_questioner_nickname.setNicknameTv(data.nickname, data.isEmotion && !data.isAnonymous, data.isMale)
                setDisappear(tv_disappear_at, data.disappearAt)
                tv_reward.text = context.getString(R.string.qa_question_item_reward, data.reward)
                tv_answer_count.text = "${data.answerNum}个回答"
                initSortBy()

            }
        }

        private fun setDisappear(tv: TextView, rowTime: String) {
            tv.text = context.getString(R.string.qa_question_item_disappear,
                    timeDescription(System.currentTimeMillis(), rowTime.toDate().time))
        }

        private fun getTagHtml(tag: String) = "<font color=\"#7195fa\">#$tag#</font>"

        private fun initSortBy() {
            if (itemView.tv_answer_count.text == "0") {
                itemView.tv_sort_answer_list.gone()
            } else {
                itemView.tv_sort_answer_list.visible()
                itemView.tv_sort_answer_list.setOnClickListener { popupWindow.show() }
            }
        }

        private fun initMenuContentView(): View {
            val root = context.layoutInflater.inflate(R.layout.qa_popup_window_answer_list_sort_by, null, false)
            root.fl_sort_by_default.setOnClickListener {
                val sortOrder = root.tv_sort_by_default.text.toString()
                if (sortOrder == itemView.tv_sort_answer_list.text) {
                    return@setOnClickListener
                }
                itemView.tv_sort_answer_list.text = sortOrder
                onSortOrderChangedListener?.invoke(sortOrder)
                changeCheckedState(true, root)
                popupWindow.dismiss()
            }
            root.fl_sort_by_time.setOnClickListener {
                val sortOrder = root.tv_sort_by_time.text.toString()
                if (sortOrder == itemView.tv_sort_answer_list.text) {
                    return@setOnClickListener
                }
                itemView.tv_sort_answer_list.text = sortOrder
                onSortOrderChangedListener?.invoke(sortOrder)
                changeCheckedState(false, root)
                popupWindow.dismiss()
            }
            root.view_frame.setOnClickListener { popupWindow.dismiss() }
            return root
        }

        private fun changeCheckedState(checkedDefault: Boolean, root: View) {
            val defaultColor = context.resources.getColor(
                    R.color.qa_popup_window_sort_by_checked_color.takeIf { checkedDefault }
                            ?: R.color.qa_popup_window_sort_by_uncheck_color

            )
            val timeColor = context.resources.getColor(
                    R.color.qa_popup_window_sort_by_checked_color.takeUnless { checkedDefault }
                            ?: R.color.qa_popup_window_sort_by_uncheck_color
            )
            root.tv_sort_by_default.setTextColor(defaultColor)
            root.tv_sort_by_time.setTextColor(timeColor)

            if (checkedDefault) {
                root.iv_checked_default.visible()
                root.iv_checked_time.invisible()
            } else {
                root.iv_checked_default.invisible()
                root.iv_checked_time.visible()
            }
        }

        private fun PopupWindow.init(): PopupWindow {
            isTouchable = true
            isOutsideTouchable = true
            return this
        }

        private fun PopupWindow.show() {
            changeCheckedState(itemView.tv_sort_answer_list.text == sortOrderContainer.tv_sort_by_default.text, sortOrderContainer)
            showAsDropDown(itemView)
        }
    }
}