package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.activityViewModels
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel

/**
 * @class
 * @author YYQF
 * @data 2021/9/24
 * @description
 **/
abstract class BaseDynamicFragment : BaseFragment() {
    //用来将发送调节window alpha的handler,如果任务还未执行就返回到了这个window就马上取消任务
    protected lateinit var handler: Handler
    protected lateinit var windowAlphaRunnable: Runnable
    companion object {
        const val REQUEST_LIST_REFRESH_ACTIVITY = 0x1

        //R.string.qa_search_hot_word_key 长度
        const val HOT_WORD_HEAD_LENGTH = 6
    }

    val viewModel by activityViewModels<DynamicListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        handler = Handler()
        windowAlphaRunnable = Runnable()
        {
            val window: Window = requireActivity().window
            val layoutParams: WindowManager.LayoutParams = window.attributes
            layoutParams.alpha = 0F
            window.attributes = layoutParams
        }
        return inflater.inflate(getLayoutId(),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    open fun initView(){}

    open fun initData(){}

    abstract fun getLayoutId(): Int

}