package com.mredrock.cyxbs.sport.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.config.route.DISCOVER_SPORT
import com.mredrock.cyxbs.config.route.LOGIN_BIND_IDS
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnDoubleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.sport.R
import com.mredrock.cyxbs.sport.databinding.SportActivitySportDetailBinding
import com.mredrock.cyxbs.sport.ui.adapter.SportRvAdapter
import com.mredrock.cyxbs.sport.ui.viewmodel.SportDetailViewModel
import com.mredrock.cyxbs.sport.util.sSpIdsIsBind
import java.util.*

@Route(path = DISCOVER_SPORT)
class SportDetailActivity : BaseBindActivity<SportActivitySportDetailBinding>() {

    /**
     * RecyclerView的adapter
     */
    private val sportRvAdapter = SportRvAdapter()

    /**
     * 是否放假中
     */
    private var isHoliday = false

    //TODO 获取课表模块提供的周数
    private val week: Int = 22

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!sSpIdsIsBind) {
            ARouter.getInstance().build(LOGIN_BIND_IDS).navigation()
            "请先绑定教务在线才能继续使用哦".toast()
            finish()
        }
        //初始化RecyclerView的adapter以及layoutManger
        binding.sportRvDetailList.run {
            adapter = sportRvAdapter
            layoutManager = LinearLayoutManager(this@SportDetailActivity)
        }
        //获取ViewModel
        val vm = ViewModelProvider(this).get(SportDetailViewModel::class.java)
        binding.run {
            //初始化时加载数据较慢，显示加载动画让用户知晓正在加载
            sportSrlDetailList.autoRefreshAnimationOnly()
            //设置双击返回顶部
            sportClDetailTop.setOnDoubleClickListener {
                binding.sportRvDetailList.smoothScrollToPosition(0)
                binding.sportSrlDetailList.autoRefresh()
            }
            //设置返回键
            sportIbDetailBack.setOnClickListener {
                finish()
            }
            //设置右上方的时间段
            //年份
            val year: Int = Calendar.getInstance()[Calendar.YEAR]
            //学期的季节
            val season: String = when (Calendar.getInstance()[Calendar.MONTH] + 1) {
                1 -> "秋"
                in 2..7 -> "春"
                in 8..12 -> "秋"
                else -> ""
            }
            if (week in 1..21) {
                //若周数为1到21则认定为未放假
                sportTvDetailTime.text = "${year}年  $season"
            } else {
                //若为其他值则显示放假中,并设置提示图为放假
                isHoliday = true
                sportTvDetailTime.text = "放假中"
                sportSrlDetailList.finishRefresh()
                sportRvDetailList.gone()
                sportSivDetailHint.setImageResource(R.drawable.sport_ic_holiday)
                sportSivDetailHint.visible()
                sportTvDetailHint.text = "大家都放假了，好好度假吧！"
                sportTvDetailHint.visible()
            }
            //设置刷新监听
            if (!isHoliday) {
                //未放假则正常刷新
                sportSrlDetailList.setOnRefreshListener {
                    vm.refreshSportDetailData()
                }
            } else {
                //放假则直接结束刷新
                sportSrlDetailList.setOnRefreshListener {
                    sportSrlDetailList.finishRefresh()
                }
            }
        }
        //出错时结束刷新并设置提示图片和文字
        vm.isError.observe(this) {
            //若不处于放假中则显示错误页面
            if (it && !isHoliday) {
                binding.run {
                    sportSrlDetailList.finishRefresh()
                    sportRvDetailList.gone()
                    sportSivDetailHint.setImageResource(R.drawable.sport_ic_data_error)
                    sportSivDetailHint.visible()
                    sportTvDetailHint.text = "数据错误"
                    sportTvDetailHint.visible()
                }
            }
        }
        //添加数据
        vm.sportDetailData.observe(this) { bean ->
            //未放假则正常加载
            if (week in 1..21) {
                //添加页面顶部总数据
                binding.run {
                    sportTvDetailTotalDone.text =
                        (bean.runDone + bean.otherDone).toString()                //总的已打次数
                    sportTvDetailTotalNeed.text =
                        "/${bean.runTotal + bean.otherTotal}"                     //总的需要打卡次数
                    sportTvDetailRunDone.text =
                        (bean.runDone).toString()                                 //跑步已打卡次数
                    sportTvDetailRunNeed.text =
                        "/${bean.runTotal}"                                       //跑步需要打卡的次数
                    sportTvDetailOtherDone.text =
                        (bean.otherDone).toString()                               //其他已打卡次数
                    sportTvDetailOtherNeed.text =
                        "/${bean.otherTotal}"                                     //其他需要打卡的次数
                    sportTvDetailAward.text = bean.award.toString()               //奖励次数
                }
                if ((bean.runDone + bean.otherDone) != 0) {
                    //若打卡次数不为0则添加RecyclerView的数据并将提示所用的图片和文字隐藏
                    binding.sportTvDetailHint.gone()
                    binding.sportSivDetailHint.gone()
                    binding.sportRvDetailList.visible()
                    //把风雨操场的两项数据进行合并（因为太长了显示不下，经与产品商讨后采用此种方式，ios同步）
                    val list = bean.item.map {
                        it.apply {
                            spot = spot.replace("风雨操场（篮球馆）", "风雨操场")
                            spot = spot.replace("风雨操场（乒乓球馆）", "风雨操场")
                        }
                    }.reversed()
                    sportRvAdapter.submitList(list)
                } else if ((bean.runDone + bean.otherDone) == 0) {
                    //若打卡的次数为0则隐藏RecyclerView并设置没有记录的提示
                    binding.run {
                        sportRvDetailList.gone()
                        sportSivDetailHint.setImageResource(R.drawable.sport_ic_no_data)
                        sportSivDetailHint.visible()
                        sportTvDetailHint.text = "暂时还没记录哦~"
                        sportTvDetailHint.visible()
                    }
                }
            }
            //设置刷新完成
            binding.sportSrlDetailList.finishRefresh()
        }
    }
}