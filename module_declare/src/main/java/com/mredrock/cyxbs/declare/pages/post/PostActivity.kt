package com.mredrock.cyxbs.declare.pages.post

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter.LengthFilter
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.mredrock.cyxbs.declare.databinding.DeclareActivityPostBinding
import com.mredrock.cyxbs.declare.databinding.DeclareLayoutDialogEditBinding
import com.mredrock.cyxbs.declare.databinding.DeclareLayoutDialogSubmitBinding
import com.mredrock.cyxbs.declare.pages.post.adapter.PostSectionRvAdapter
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import kotlinx.coroutines.launch
import java.util.Optional
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PostActivity : BaseBindActivity<DeclareActivityPostBinding>() {

    override val isCancelStatusBar: Boolean
        get() = false

    private val viewModel by viewModels<PostViewModel>()
    private lateinit var submitDialogLayoutBinding: DeclareLayoutDialogSubmitBinding
    private lateinit var editDialogLayoutBinding: DeclareLayoutDialogEditBinding
    private lateinit var submitDialog: MaterialDialog
    private lateinit var editDialog: MaterialDialog
    private lateinit var sectionAdapter: PostSectionRvAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null
        initDialog()
        binding.rvTopic.apply {
            layoutManager = LinearLayoutManager(this@PostActivity)
            adapter = PostSectionRvAdapter { list, position, et ->
                lifecycleScope.launch {
                    // 长度限制15
                    openEdit(15, list[position]).ifPresent {
                        et.setText(it)
                        list[position] = it
                    }
                }
            }.also { sectionAdapter = it }
        }
        binding.btnSubmit.setOnClickListener {
            // 弹出Dialog
            submitDialog.show()
        }
        binding.etTopic.setOnTouchListener { _, _ ->
            lifecycleScope.launch {
                openEdit(30, binding.etTopic.text.toString()).ifPresent {
                    binding.etTopic.setText(it)
                }
            }
            true
        }
        binding.declareIvToolbarArrowLeft.setOnClickListener {
            finish()
        }
        lifecycleScope.launch {
            viewModel.postResultFlow.collectLaunch {
                if (it.isSuccess()) {
                    toast("发布成功")
                    finish()
                } else {
                    toast("发布失败 $it")
                }
            }
        }
    }

    private fun initDialog() {
        submitDialogLayoutBinding = DeclareLayoutDialogSubmitBinding.inflate(layoutInflater)
        editDialogLayoutBinding = DeclareLayoutDialogEditBinding.inflate(layoutInflater)
        submitDialog = MaterialDialog(this)
            .customView(view = submitDialogLayoutBinding.root)
            .cornerRadius(literalDp = 8f)
            .maxWidth(literal = 300.dp2px)
        editDialog = MaterialDialog(this)
            .customView(view = editDialogLayoutBinding.root)
            .cornerRadius(literalDp = 8f)
            .maxWidth(literal = 300.dp2px)
        submitDialogLayoutBinding.btnCancel.setOnClickListener {
            submitDialog.hide()
        }
        submitDialogLayoutBinding.btnSubmit.setOnClickListener {
            lifecycleScope.launch {
                viewModel.post(binding.etTopic.text.toString(), sectionAdapter.list)
            }
            submitDialog.hide()
        }
    }

    private suspend fun openEdit(maxLen: Int, originText: String = ""): Optional<String> = suspendCoroutine { co ->
        editDialogLayoutBinding.apply {
            // 重置edittext状态
            et.setText(originText)
            et.filters = arrayOf(LengthFilter(maxLen))
            textInputLayout.counterMaxLength = maxLen

            fun resetListeners() {
                btnCancel.setOnClickListener(null)
                btnSubmit.setOnClickListener(null)
            }
            btnCancel.setOnClickListener {
                editDialog.cancel()
                resetListeners()
                co.resume(Optional.empty())
            }
            btnSubmit.setOnClickListener {
                editDialog.hide()
                resetListeners()
                co.resume(Optional.of(et.text.toString()))
            }
        }
        editDialog.show()
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PostActivity::class.java)
            context.startActivity(starter)
        }
    }
}