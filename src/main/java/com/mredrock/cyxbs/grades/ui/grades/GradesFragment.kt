package com.mredrock.cyxbs.grades.ui.grades

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.grades.R
import com.mredrock.cyxbs.grades.bean.Grade
import com.mredrock.cyxbs.grades.network.ApiService
import com.mredrock.cyxbs.grades.ui.adapter.GradesAdapter
import kotlinx.android.synthetic.main.grades_fragment.view.*
import org.jetbrains.anko.support.v4.toast

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class GradesFragment : BaseFragment() {
    private lateinit var parent: View
    private var data: MutableList<Grade> = ArrayList()
    private lateinit var adapter: GradesAdapter
    private val user = BaseApp.user
    private val apiService: ApiService by lazy { ApiGenerator.getApiService(ApiService::class.java) }
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        parent = inflater.inflate(R.layout.grades_fragment, container, false)
        recyclerView = parent.rv_grades
        init()
        initRv()
        return parent
    }

    private fun init() {
        parent.tv_grades_noData.text = "暂无成绩信息"
    }

    private fun initRv() {
        context?.let { context ->
            adapter = GradesAdapter(
                    context = context,
                    data = data,
                    layoutIds = intArrayOf(R.layout.grades_item_grade))
            recyclerView.layoutManager = LinearLayoutManager(getContext())
            recyclerView.adapter = adapter
        }

        if (user != null) {
            apiService
                    .getGrades(stuNum = user.stuNum!!, idNum = user.idNum!!)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnErrorWithDefaultErrorHandler {
                        toast("无法获取成绩信息")
                        false
                    }
                    .safeSubscribeBy { it ->
                        if (it.isNotEmpty()) {
                            adapter.addData(it as MutableList<Grade>)
                            adapter.isShowFooter = false
                            parent.fl_grades_noData.gone()
                        }
                    }
        } else {
            toast("无法获取到用户信息")
        }

        parent.srl_grades.setOnRefreshListener {
            refreshData()
        }
    }


    private fun refreshData() {
        if (user != null) {
            apiService
                    .getGrades(stuNum = user.stuNum!!, idNum = user.idNum!!)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnErrorWithDefaultErrorHandler {
                        toast("无法获取成绩信息")
                        false
                    }
                    .safeSubscribeBy { it ->
                        adapter.refreshData(it as MutableList<Grade>)
                        parent.srl_grades.isRefreshing = false
                    }
        } else {
            toast("无法获取到用户信息")
        }
    }

}
