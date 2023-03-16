package com.mredrock.cyxbs.declare.pages.detail.page.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.declare.databinding.DeclareActivityDetailBinding
import com.mredrock.cyxbs.declare.pages.detail.bean.VoteData
import com.mredrock.cyxbs.declare.pages.detail.page.adapter.DetailRvAdapter
import com.mredrock.cyxbs.declare.pages.detail.page.viewmodel.DetailViewModel
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity

class DetailActivity : BaseBindActivity<DeclareActivityDetailBinding>() {
    companion object {
        /**
         * 启动投票详情页面
         */
        fun startActivity(context: Context, id: Int) {
            context.startActivity(
                Intent(
                    context,
                    DetailActivity::class.java
                ).apply { putExtra("id", id) })
        }
    }

    private val mViewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("id", -1)
        //因为投票后返回的是个map，map是无序的，所以这里用个list记下未投票之前的选项排布顺序
        val voteDataList = mutableListOf<VoteData>()

        val declareDetailRvAdapter = DetailRvAdapter()

        binding.declareDetailRecyclerview.run {
            layoutManager = LinearLayoutManager(this@DetailActivity)
            adapter = declareDetailRvAdapter
        }

        mViewModel.detailLiveData.observe {
            voteDataList.clear()
            binding.declareDetailTitle.text = it.title
            val votedList = mutableListOf<VoteData>()//差分刷新要求 源数据集和新数据集 不是同一个对象才能生效
            for (s: String in it.choices) {//未投票
                voteDataList.add(VoteData(it.voted, s, 0))
            }

            if (it.statistic != null) {//投过票
                var sumVotes = 0
                for (v in it.statistic) {//计算总票数
                    sumVotes += v.value
                }
                for (data in voteDataList) {//计算占比（不知道为什么占比不在后端计算-_-|||）
                    data.percent = (it.statistic[data.choice]!! * 100) / sumVotes
                }
            }
            votedList.addAll(voteDataList)//除去旧的数据集
            declareDetailRvAdapter.submitList(votedList)//每次提交新的数据集
        }

        mViewModel.votedLiveData.observe {
            var sumVotes = 0
            val votedList = mutableListOf<VoteData>()//差分刷新要求 源数据集和新数据集 不是同一个对象才能生效
            for (data in voteDataList) { //计算总票数
                sumVotes += it.statistic[data.choice]!!
            }
            for (data in voteDataList) {//计算占比
                data.percent = (it.statistic[data.choice]!! * 100) / sumVotes
                votedList.add(VoteData(it.voted, data.choice, data.percent))
            }
            declareDetailRvAdapter.submitList(votedList)
        }

        mViewModel.cancelLiveData.observe {
            if (it.Id == id) mViewModel.getDeclareDetail(id)//取消投票成功就再刷新一下数据
        }

        declareDetailRvAdapter.setOnClickedListener {
            if (it.voted == null) {//未投票
                mViewModel.putChoice(id, it.choice)
            } else if (it.voted == it.choice) {//已经投票并且点击的是投票选项就取消投票
                mViewModel.cancelChoice(id)
            }
        }

        mViewModel.errorLiveData.observe {
            if (it) {
                binding.declareDetailCl.visibility = View.GONE
                binding.declareDetailNoNet.visibility = View.VISIBLE
            } else {
                binding.declareDetailCl.visibility = View.VISIBLE
                binding.declareDetailNoNet.visibility = View.GONE
            }
        }

        mViewModel.getDeclareDetail(id)
    }
}