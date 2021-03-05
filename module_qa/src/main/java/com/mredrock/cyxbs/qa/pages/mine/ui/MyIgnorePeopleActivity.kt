package com.mredrock.cyxbs.qa.pages.mine.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Ignore
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.SimpleRvAdapter
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder.IgnoreViewHolder
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyIgnoreViewModel
import kotlinx.android.synthetic.main.qa_activity_my_ignore_people.*

class MyIgnorePeopleActivity : BaseViewModelActivity<MyIgnoreViewModel>() {

    private lateinit var rvAdapter: SimpleRvAdapter<Ignore>
    private val ignoreList = ArrayList<Ignore>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_my_ignore_people)
        initView()
        initObserve()
        startRequest()
    }

    private fun initView() {
        rvAdapter = SimpleRvAdapter(
                IgnoreViewHolder::class.java,
                R.layout.qa_recycler_item_ignore,
                ignoreList//tip: 这里采用的是引用传递，所以内外的list数据已经保持同步
        ) { viewHolder, position ->
            val ignoreVH = viewHolder as IgnoreViewHolder
            ignoreList[position].apply {
                ignoreVH.nickName.text = nickName
                Glide.with(this@MyIgnorePeopleActivity)
                        .load(avatar)
                        .into(ignoreVH.avatar)
                ignoreVH.introduction.text = introduction
                ignoreVH.antiIgnore.setOnClickListener {
                    //解除屏蔽
                    viewModel.antiIgnore(this.uid){
                        rvAdapter.notifyItemRemoved(position)
                        ignoreList.removeAt(position)
                        //TODO: 弹toast
                    }
                }
            }
        }
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this,R.drawable.qa_shape_divide_line)?.let {
            divider.setDrawable(it)
        }
        qa_rv_my_ignore.addItemDecoration(divider)
        qa_rv_my_ignore.adapter = rvAdapter
        qa_rv_my_ignore.layoutManager = LinearLayoutManager(this)
    }

    private fun startRequest() {
        viewModel.getIgnorePeople()
    }

    private fun initObserve() {
        viewModel.ignoreList.observe {
            it?.let {
                ignoreList.clear()
                ignoreList.addAll(it)
                rvAdapter.notifyDataSetChanged()
            }
        }
    }
}