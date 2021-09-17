package com.mredrock.cyxbs.discover.grades.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.ui.expandableAdapter.GPAAdapter
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel
import kotlinx.android.synthetic.main.grades_fragment_gpa.*

/**
 * Created by roger on 2020/3/21
 */
class GPAFragment : Fragment() {
    private lateinit var viewModel: ContainerViewModel

    private lateinit var adapter: GPAAdapter

    private lateinit var hashMap: HashMap<Int, Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.grades_fragment_gpa, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this)[ContainerViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //创建type到resId的映射
        hashMap = HashMap(4)
        hashMap.put(GPAAdapter.HEADER, R.layout.grades_item_gpa_list_header)
        hashMap.put(GPAAdapter.CHILD, R.layout.grades_item_gpa_list_child)
        hashMap.put(GPAAdapter.NORMAL_BOTTOM, R.layout.grades_item_gpa_list_normal_bottom)
        hashMap.put(GPAAdapter.NORMAL_TOP, R.layout.grades_item_gpa_list_normal_top)


        viewModel.analyzeData.observe(viewLifecycleOwner, Observer {
            val context = context ?: return@Observer
            if (it?.data == null) return@Observer
            adapter = GPAAdapter(context, hashMap, it.data)
            grades_rv.adapter = adapter
        })



    }
}