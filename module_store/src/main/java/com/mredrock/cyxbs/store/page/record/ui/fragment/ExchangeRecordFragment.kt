package com.mredrock.cyxbs.store.page.record.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.utils.SimpleRvAdapter
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.store.bean.ExchangeRecord
import com.mredrock.cyxbs.store.page.record.ui.item.ExchangePageItem
import com.mredrock.cyxbs.store.page.record.viewmodel.RecordViewModel
import com.mredrock.cyxbs.store.utils.dp2px

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/9/4
 * @time 12:46
 */
class ExchangeRecordFragment : BaseFragment() {
    // 因为我只需要 Activity 的 ViewModel, 所以没有继承于 BaseViewModelFragment
    private val viewModel by activityViewModels<RecordViewModel>()

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mImageView: ImageView
    private lateinit var mTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.store_fragment_record_exchange, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initObserve()
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.store_rv_fragment_record_exchange)
        mImageView = view.findViewById(R.id.store_iv_fragment_record_exchange)
        mTextView = view.findViewById(R.id.store_tv_fragment_record_exchange)
    }

    private fun initObserve() {
        viewModel.mExchangeRecordIsSuccessful.observe {
            if (!it) {
                showNoInterceptImage()
            }
        }
        viewModel.mExchangeRecord.observe {
            if (it.isEmpty()) {
                mImageView.setImageResource(R.drawable.store_ic_fragment_record_exchange_null)
                mTextView.text = "还没有兑换记录哦"
            } else {
                setPageExchangeAdapter(it)
            }
        }
    }

    private fun showNoInterceptImage() {
        mImageView.setImageResource(R.drawable.store_ic_no_internet)
        val lp = mImageView.layoutParams
        lp.width = 157.dp2px() // 那张没有网络的图片有点小
        lp.height = 96.dp2px()
        mImageView.layoutParams = lp
        mTextView.text = getText(R.string.store_no_internet)
    }

    private lateinit var mExchangePageItem: ExchangePageItem
    private fun setPageExchangeAdapter(list: List<ExchangeRecord>) {
        // 没有领取的放在前面, 已领取的放在后面, 相同状态的都以时间排序
        val sortedList = list.sortedWith(Comparator { o1, o2 ->
            if (o1.isReceived != o2.isReceived) { // 如果两个领取的状态不一样
                if (o1.isReceived) 1 // 大于 1 时 o1 就放到后面
                else -1 // 小于 1 时 o1 就放到前面
            }else { // 一样的话就以时间排序
                (o2.date - o1.date).toInt()
            }
        })

        // 若 adapter 未设置 则进行设置
        if (mRecyclerView.adapter == null) {
            mExchangePageItem = ExchangePageItem(sortedList, 0)

            mRecyclerView.layoutManager = LinearLayoutManager(context)
            mRecyclerView.adapter = SimpleRvAdapter()
                .addItem(mExchangePageItem)
                .show()

        }else {
            mExchangePageItem.refresh(sortedList, 0)
        }
    }
}