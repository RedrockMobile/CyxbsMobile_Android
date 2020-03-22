package com.mredrock.cyxbs.discover.grades.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel
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
                viewModel.bindIds(ids, password)
            } else {
                context?.toast("请输入统一认证码和密码哟")
            }
        }
    }

}