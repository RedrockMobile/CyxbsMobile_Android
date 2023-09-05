package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.NoClassViewModel
import com.mredrock.cyxbs.noclass.widget.MyFlexLayout

/**
 * 分页流式布局，采用vp + fragment实现，流程如下：
 * activity的viewModel，dialog和fragment均共享
 *
 * 1 创建一个fragment并且添加到adapter中，用于空的时候显示
 *
 * 2 在fragment中添加view，如果填满2行则回调填满方法  注：数据是从viewModel中拿来的，在dialog中设置
 *
 * 3 fragment的填满方法中更新数据
 *
 * 4 dialog的观察者中观察到数据的更新，然后根据减少之后的数据添加fragment
 *
 * 5 重复 2，3，4直至不再回调填满方法
 */
class NoClassBusyPageFragment : BaseFragment(R.layout.noclass_layout_gathering) {

    private val mParentViewModel by activityViewModels<NoClassViewModel>()

    private lateinit var myFlexLayout: MyFlexLayout

    companion object{
        fun newInstance() : NoClassBusyPageFragment{
            Log.d("lx", "newInstance:我希望我只出现一次 ")
            return NoClassBusyPageFragment()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 流式布局
        initView(view)
        initObserve()
    }

    private fun initView(view: View) {
        myFlexLayout = view.findViewById(R.id.noclass_gl_container)
        myFlexLayout.setOnFillCallback(object : MyFlexLayout.OnFillCallback{
            override fun onFill(itemsSize: Int) {
                mParentViewModel.removeBusyName(itemsSize)
            }
        })
    }

    private fun initObserve() {
        mParentViewModel.busyNameList.observe{
            // 每次观察的时候判断，当前有没有子view，如果没有就添加进去
            if (it.isNotEmpty() && myFlexLayout.childCount == 0){
                setView(requireContext(),it)
            }
        }
    }

    /**
     * 这里是将所有的view添加到一个大的flexLayout中
     */
    private fun setView(context: Context, nameList: List<String>) {
        // 先来获取之前所有的item数
        var noClassView: View
        var linearLayoutView: LinearLayout
        var textView: TextView
        for (element in nameList) {
            noClassView = noclassView(context)
            linearLayoutView = noClassView.findViewById(R.id.noclass_ll_gathering_item_container)
            textView = linearLayoutView.getChildAt(0) as TextView
            textView.text = element
            myFlexLayout.addView(noClassView)
        }
    }


    /**
     * viewpager2一页的每一项
     */
    private fun noclassView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.noclass_item_gathering, null).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(12, 14, 12, 14)
            }
        }
    }

}