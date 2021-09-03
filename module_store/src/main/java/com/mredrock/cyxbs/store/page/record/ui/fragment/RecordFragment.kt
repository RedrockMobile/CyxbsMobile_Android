package com.mredrock.cyxbs.store.page.record.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRvAdapter
import com.mredrock.cyxbs.store.bean.ExchangeRecord
import com.mredrock.cyxbs.store.bean.StampGetRecord
import com.mredrock.cyxbs.store.page.record.ui.item.ExchangePageItem
import com.mredrock.cyxbs.store.page.record.ui.item.GetPageItem
import com.mredrock.cyxbs.store.page.record.viewmodel.RecordViewModel
import com.mredrock.cyxbs.store.utils.dp2px

/**
 *    author : zz (后期优化: 985892345)
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 15:10
 */
class RecordFragment private constructor() : BaseFragment() {

    companion object {
        /**
         * 因为兑换记录和获取记录界面中均只有一个 RecyclerView
         * 所以这里将两个界面基于 [RecordFragment]
         * 根据 [Page] 的不同 为 RecyclerView 赋予不同的 item
         */
        fun getFragment(page: Page): RecordFragment {
            val fragment = RecordFragment()
            fragment.arguments = Bundle().apply { putString(Page::class.java.simpleName, page.name) }
            return fragment
        }
    }

    enum class Page { // 用于起规范作用
        EXCHANGE, // 兑换记录页面
        GET // 获取记录页面
    }

    // 因为我只需要 Activity 的 ViewModel, 所以没有继承于 BaseViewModelFragment
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(requireActivity()).get(RecordViewModel::class.java)
    }

    // 这个表示了该 Fragment 显示 "兑换记录" 还是 "获取记录" 界面, 原因在与使用了 VP2 的复用机制
    private var mPageType: String? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mImageView: ImageView
    private lateinit var mTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.store_fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPageType = arguments?.getString(Page::class.java.simpleName) // 从之前的启动该 Fragment 的界面获取显示哪个界面
        initView(view)
        initObserve()
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.store_rv_fragment_record)
        mImageView = view.findViewById(R.id.store_iv_fragment_record)
        mTextView = view.findViewById(R.id.store_tv_fragment_record)
    }

    private fun initObserve() {
        when (mPageType) {
            Page.EXCHANGE.name -> {
                viewModel.mExchangeRecordIsSuccessful.observe(viewLifecycleOwner, Observer {
                    if (!it) { showNoInterceptImage() }
                })
                viewModel.mExchangeRecord.observe(viewLifecycleOwner, Observer {
                    if (it.isEmpty()) {
                        mRecyclerView.gone()
                        mImageView.setImageResource(R.drawable.store_ic_fragment_record_exchange_null)
                        mTextView.text = "还没有兑换记录哦"
                    }else {
                        mImageView.gone()
                        mTextView.gone()
                        setPageExchangeAdapter(it)
                    }
                })
            }
            Page.GET.name -> {
                viewModel.mStampGetRecordIsSuccessful.observe(viewLifecycleOwner, Observer {
                    if (!it) { showNoInterceptImage() }
                })
                viewModel.mStampGetRecord.observe(viewLifecycleOwner, Observer {
                    if (it.isEmpty()) {
                        mRecyclerView.gone()
                        mImageView.setImageResource(R.drawable.store_ic_fragment_record_get_null)
                        mTextView.text = "还没有获取记录，快去做任务吧"
                    }else {
                        mImageView.gone()
                        mTextView.gone()
                        setPageGetAdapter(it)
                    }
                })
            }
        }
    }

    private fun showNoInterceptImage() {
        mRecyclerView.gone()
        mImageView.setImageResource(R.drawable.store_ic_no_internet)
        val lp = mImageView.layoutParams
        lp.width = 157.dp2px() // 那张没有网络的图片有点小
        lp.height = 96.dp2px()
        mImageView.layoutParams = lp
        mTextView.text = getText(R.string.store_no_internet)
    }

    private fun setPageExchangeAdapter(list: List<ExchangeRecord>) {
        // 若 adapter 未设置 则进行设置
        if (mRecyclerView.adapter == null) {
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            // 其实这里只用原生的 Adapter 性能更好, 但这里面使用了 DataBinding, 原谅我的任性 :)
            mRecyclerView.adapter = SimpleRvAdapter()
                .addItem(ExchangePageItem(list, 0))
                .show()
        }
    }

    private fun setPageGetAdapter(list: List<StampGetRecord>) {
        // 若 adapter 未设置 则进行设置
        if (mRecyclerView.adapter == null) {
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            // 其实这里只用原生的 Adapter 性能更好, 但这里面使用了 DataBinding, 原谅我的任性 :)
            mRecyclerView.adapter = SimpleRvAdapter()
                .addItem(GetPageItem(list, 0))
                .show()
        }
    }
}