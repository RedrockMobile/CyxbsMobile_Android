package com.mredrock.cyxbs.discover.grades.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel

/**
 * Created by roger on 2020/3/21
 */
class GPAFragment : Fragment() {
    private lateinit var viewModel: ContainerViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.grades_fragment_gpa, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[ContainerViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.analyzeData.observe(this, Observer {

        })

    }
}