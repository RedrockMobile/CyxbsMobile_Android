package com.mredrock.cyxbs.discover.grades.ui.main

import android.os.Bundle
import android.transition.Explode
import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel
import com.mredrock.cyxbs.discover.grades.utils.widget.KeyboardUtil
import kotlinx.android.synthetic.main.grades_activity_bind.*


/**
 * @Author: xgl
 * @ClassName: BindActivity
 * @Description:
 * @Date: 2020/9/13 19:17
 */
class BindActivity : BaseActivity() {
    private lateinit var viewModel: ContainerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grades_activity_bind)
        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this@BindActivity, R.color.common_mine_sign_store_bg))
            initWithSplitLine("绑定认证码",
                    false)
            setTitleLocationAtLeft(true)
        }
        viewModel = ViewModelProvider(this)[ContainerViewModel::class.java]
        grades_btn_bind.setOnClickListener {
            val ids: String = grades_et_ids.text.toString()
            val password: String = grades_et_password.text.toString()
            if (ids.isNotEmpty() && password.isNotEmpty()) {
                KeyboardUtil.closeKeybord(this)
                viewModel.bindIds(ids, password) { bubble() }
            } else {
                context.toast("请输入统一认证码和密码哟")
            }
        }
        viewModel.replaceBindFragmentToGPAFragment.observe(this, Observer {
            if (it == false) {
                bubble()
            } else {
                // 注意 这里finish返回后viewModel被重建了
                finish()
            }
        })
        grades_et_password.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    val ids: String = grades_et_ids.text.toString()
                    val password: String = grades_et_password.text.toString()
                    if (ids.isNotEmpty() && password.isNotEmpty()) {
                        KeyboardUtil.closeKeybord(this)
                        viewModel.bindIds(ids, password) { bubble() }
                        true
                    } else {
                        context.toast("请输入统一认证码和密码哟")
                        false
                    }
                }
                else -> false
            }
        }
    }

    private fun bubble() {
        TransitionManager.beginDelayedTransition(grades_constraint_layout, Explode())
        for (i in 0 until grades_constraint_layout.childCount) {
            val view = grades_constraint_layout[i]
            view.visibility = when (view.visibility) {
                View.GONE -> View.VISIBLE
                View.VISIBLE -> View.GONE
                else -> View.VISIBLE
            }
        }
    }
}