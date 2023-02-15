package com.mredrock.cyxbs.declare.pages.post.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mredrock.cyxbs.declare.databinding.DeclareItemAddSectionBinding
import com.mredrock.cyxbs.declare.databinding.DeclareItemSectionBinding
import com.mredrock.cyxbs.lib.utils.extensions.toast


/**
 * com.mredrock.cyxbs.declare.pages.post.adapter.PostSectionRvAdapter.kt
 * CyxbsMobile_Android
 *
 * @author 寒雨
 * @since 2023/2/8 下午4:56
 */
class PostSectionRvAdapter(private val onItemTouch: (list: MutableList<String>, position: Int, et: EditText) -> Unit) : RecyclerView.Adapter<PostSectionRvAdapter.Holder>() {

    val list: MutableList<String> = mutableListOf(
        "", "", "", ""
    )

    @SuppressLint("ClickableViewAccessibility")
    inner class Holder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            if (binding is DeclareItemSectionBinding) {
                binding.sivRm.setOnClickListener {
                    list.removeAt(bindingAdapterPosition)
                    notifyItemRemoved(bindingAdapterPosition)
                    // 刷新下面所有的item，让选项号保持顺序
                    (bindingAdapterPosition..list.size).forEach {
                        notifyItemChanged(it)
                    }
                }
                binding.et.setOnTouchListener { _, _ ->
                    onItemTouch(list, bindingAdapterPosition, binding.et)
                    true
                }
            } else if (binding is DeclareItemAddSectionBinding) {
                binding.sivAdd.setOnClickListener {
                    if (list.size == 10) {
                        toast("最多仅可以添加10个选项")
                        return@setOnClickListener
                    }
                    list.add("")
                    notifyItemInserted(list.size - 1)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            if (viewType == TYPE_TAIL) {
                DeclareItemAddSectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            } else {
                DeclareItemSectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            }
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_NORMAL -> {
                val binding = holder.binding as DeclareItemSectionBinding
                val content = list[position]
                if (binding.et.hint != "选项${position + 1}") {
                    binding.root.post {
                        binding.et.hint = "选项${position + 1}"
                        binding.et.setText(content)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        // 最后一个
        if (position == itemCount - 1) {
            return TYPE_TAIL
        }
        return TYPE_NORMAL
    }

    // 尾部
    override fun getItemCount(): Int {
        return list.size + 1
    }

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_TAIL = 1
    }
}