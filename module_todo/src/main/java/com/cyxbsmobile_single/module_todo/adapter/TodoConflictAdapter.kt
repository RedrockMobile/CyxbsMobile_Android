package com.cyxbsmobile_single.module_todo.adapter

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.TodoModel
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.extensions.append
import com.mredrock.cyxbs.common.utils.extensions.appendln
import kotlinx.android.synthetic.main.todo_fragment_on_confilt.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author: RayleighZ
 * Time: 2021-10-29 10:16
 * Describe: 当出现冲突的时候，展示在feed上的adapter
 */
class TodoConflictAdapter(
    private val remoteSyncTime: Long,
    private val localSyncTime: Long,
    private val onConflictHandled: () -> Unit
) :
    BaseFeedFragment.Adapter() {

    lateinit var rootView: View

    override fun onCreateView(context: Context, parent: ViewGroup): View {
        rootView = LayoutInflater.from(context).inflate(R.layout.todo_fragment_on_confilt, parent)
        rootView.apply {
            //加载todo
            val spannableString = SpannableString("掌友，你的云同步存档 ")
            val format = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA)
            val remoteTime = format.format(Date(remoteSyncTime * 1000))
            val localTime = format.format(Date(localSyncTime * 1000))
            val sb = SpannableStringBuilder()
            val conflictSpan = ForegroundColorSpan(
                ContextCompat.getColor(
                    BaseApp.context,
                    R.color.todo_on_conflict_button_color
                )
            )

            val normalSpan = ForegroundColorSpan(
                ContextCompat.getColor(
                    BaseApp.context,
                    R.color.common_level_three_font_color
                )
            )

            sb.append("掌友，你的云同步存档 ", normalSpan)
            sb.append(remoteTime, conflictSpan)
            sb.append(" 和本地存档 ", normalSpan)
            sb.append(localTime, conflictSpan)
            sb.append("存在冲突，请选择一个存档予以保留", normalSpan)

            todo_tv_fragment_conflict_show.text = sb

            todo_btn_fragment_conflict_local.setOnClickListener {
                TodoModel.INSTANCE.forcePush { onConflictHandled.invoke() }
            }

            todo_btn_fragment_conflict_remote.setOnClickListener {
                TodoModel.INSTANCE.getAllTodoFromNet(true) { onConflictHandled.invoke() }
            }
        }
        return rootView
    }
}