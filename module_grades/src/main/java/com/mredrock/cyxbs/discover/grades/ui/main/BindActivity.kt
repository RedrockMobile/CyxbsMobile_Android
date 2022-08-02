package com.mredrock.cyxbs.discover.grades.ui.main

import android.os.Bundle
import android.transition.Explode
import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel
import com.mredrock.cyxbs.discover.grades.utils.widget.KeyboardUtil


/**
 * @Author: xgl
 * @ClassName: BindActivity
 * @Description:
 * @Date: 2020/9/13 19:17
 */
class BindActivity : BaseActivity() {
    private lateinit var viewModel: ContainerViewModel

    private val mBtnBind by R.id.grades_btn_bind.view<Button>()
    private val mEtIds by R.id.grades_et_ids.view<EditText>()
    private val mEtPassword by R.id.grades_et_password.view<EditText>()
    private val mConstraintLayout by R.id.grades_constraint_layout.view<ConstraintLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grades_activity_bind)
        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this@BindActivity, com.mredrock.cyxbs.common.R.color.common_mine_sign_store_bg))
            initWithSplitLine("绑定认证码",
                    false)
            setTitleLocationAtLeft(true)
        }
        viewModel = ViewModelProvider(this)[ContainerViewModel::class.java]
        mBtnBind.setOnClickListener {
            val ids: String = mEtIds.text.toString()
            val password: String = mEtPassword.text.toString()
            if (ids.isNotEmpty() && password.isNotEmpty()) {
                KeyboardUtil.closeKeybord(this)
                viewModel.bindIds(ids, password) { bubble() }
            } else {
                toast("请输入统一认证码和密码哟")
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
        mEtPassword.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    val ids: String = mEtIds.text.toString()
                    val password: String = mEtPassword.text.toString()
                    if (ids.isNotEmpty() && password.isNotEmpty()) {
                        KeyboardUtil.closeKeybord(this)
                        viewModel.bindIds(ids, password) { bubble() }
                        true
                    } else {
                        toast("请输入统一认证码和密码哟")
                        false
                    }
                }
                else -> false
            }
        }
    }

    private fun bubble() {
        TransitionManager.beginDelayedTransition(mConstraintLayout, Explode())
        for (i in 0 until mConstraintLayout.childCount) {
            val view = mConstraintLayout[i]
            view.visibility = when (view.visibility) {
                View.GONE -> View.VISIBLE
                View.VISIBLE -> View.GONE
                else -> View.VISIBLE
            }
        }
    }
}