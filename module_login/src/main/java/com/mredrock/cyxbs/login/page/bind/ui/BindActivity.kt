package com.mredrock.cyxbs.login.page.bind.ui

import android.os.Bundle
import android.transition.Explode
import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.login.IBindService
import com.mredrock.cyxbs.config.route.LOGIN_BIND_IDS
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.login.R
import com.mredrock.cyxbs.login.page.bind.viewmodel.BindViewModel
import com.mredrock.cyxbs.login.widget.KeyboardUtil


/**
 * @Author: xgl
 * @ClassName: BindActivity
 * @Description:
 * @Date: 2020/9/13 19:17
 */
@Route(path = LOGIN_BIND_IDS)
class BindActivity : BaseActivity() {
    private lateinit var viewModel: BindViewModel

    private val mBtnBind by R.id.login_btn_bind.view<Button>()
    private val mEtIds by R.id.login_et_ids.view<EditText>()
    private val mEtPassword by R.id.login_et_password.view<EditText>()
    private val mConstraintLayout by R.id.login_constraint_layout.view<ConstraintLayout>()
    private val mToolbar by R.id.login_toolbar_bind.view<Toolbar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_bind)
        //TODO 设置toolbar
        mToolbar.title = "绑定认证码"
        viewModel = ViewModelProvider(this)[BindViewModel::class.java]
        mBtnBind.setOnClickListener {
            val ids: String = mEtIds.text.toString()
            val password: String = mEtPassword.text.toString()
            if (ids.isNotEmpty() && password.isNotEmpty()) {
                KeyboardUtil.closeKeybord(this)
                viewModel.bindIds(ids, password) { bubble() }
            } else {
                "请输入统一认证码和密码哟".toast()
            }
        }
        mToolbar.setNavigationOnClickListener {
            finish()
        }
        IBindService::class.impl.isBindSuccess.observe(this, Observer {
            if (it) {
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
                        "请输入统一认证码和密码哟".toast()
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