package com.mredrock.cyxbs.discover.grades.ui.fragment

import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel
import com.mredrock.cyxbs.discover.grades.utils.widget.KeyboardUtil
import kotlinx.android.synthetic.main.grades_fragment_bind.*

/**
 * Created by roger on 2020/3/20
 */
class BindFragment : Fragment() {
    private lateinit var viewModel: ContainerViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.grades_fragment_bind, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[ContainerViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        grades_btn_bind.setOnClickListener {
            val ids: String = grades_et_ids.text.toString()
            val password: String = grades_et_password.text.toString()
            if (ids.isNotEmpty() && password.isNotEmpty()) {

                KeyboardUtil.closeKeybord(activity)
                viewModel.bindIds(ids, password) { bubble() }
            } else {
                context?.toast("请输入统一认证码和密码哟")
            }
        }
        viewModel.replaceBindFragmentToGPAFragment.observe(this, Observer {
            if (it == false) {
                bubble()
            }
        })

        grades_et_password.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    val ids: String = grades_et_ids.text.toString()
                    val password: String = grades_et_password.text.toString()
                    if (ids.isNotEmpty() && password.isNotEmpty()) {

                        KeyboardUtil.closeKeybord(activity)
                        viewModel.bindIds(ids, password) { bubble() }
                        true
                    } else {
                        context?.toast("请输入统一认证码和密码哟")
                        false
                    }
                }
                else -> false
            }
        }
    }

    private fun bubble() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(grades_constraint_layout, Explode())
        }
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