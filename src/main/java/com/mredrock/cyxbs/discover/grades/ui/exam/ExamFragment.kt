package com.mredrock.cyxbs.grades.ui.exam

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
import com.mredrock.cyxbs.grades.bean.Exam
import com.mredrock.cyxbs.grades.network.ApiService
import com.mredrock.cyxbs.grades.ui.adapter.ExamAdapter
import com.mredrock.cyxbs.grades.ui.adapter.ReExamAdapter
import kotlinx.android.synthetic.main.grades_fragment.view.*
import org.jetbrains.anko.support.v4.toast

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class ExamFragment : BaseFragment() {
    private lateinit var parent: View
    private lateinit var adapter: ExamAdapter
    private lateinit var reAdapter: ReExamAdapter
    private val user = BaseApp.user
    private val data: MutableList<Exam> = ArrayList()
    private lateinit var recyclerView: RecyclerView

    private val apiService: ApiService by lazy { ApiGenerator.getApiService(ApiService::class.java) }

    var kind: String? = null
        set(value) {
            if (value != null) {
                field = value
            }
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        parent = inflater.inflate(R.layout.grades_fragment, container, false)
        recyclerView = parent.rv_grades
        judge()
        return parent
    }

    private fun judge() {
        if (kind != null && user != null) {
            when (kind) {
                "考试安排" -> initExam()
                "补考安排" -> initReExam()
            }
        }
    }

    private fun initExam() {
        parent.tv_grades_noData.text = "暂无考试安排"
        context?.let { context ->
            adapter = ExamAdapter(context, data, intArrayOf(R.layout.grades_item_exam))
            recyclerView.layoutManager = LinearLayoutManager(getContext())
            recyclerView.adapter = adapter
        }

        if (user != null) {
            apiService
                    .getExam(user.stuNum!!)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnErrorWithDefaultErrorHandler {
                        toast("没有获取到考试安排")
                        false
                    }
                    .safeSubscribeBy { it ->
                        if (it.isNotEmpty()) {
                            it.sorted()
                            adapter.addData(it as MutableList<Exam>)
                            adapter.isShowFooter = false
                            parent.fl_grades_noData.gone()
                        }
                    }
        }

        parent.srl_grades.setOnRefreshListener {
            refreshExam()
        }
    }

    private fun refreshExam() {
        adapter.clearData()
        if (user != null) {
            apiService
                    .getExam(user.stuNum!!)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnErrorWithDefaultErrorHandler {
                        toast("没有获取到考试安排")
                        false
                    }
                    .safeSubscribeBy { it ->
                        it.sorted()
                        adapter.refreshData(it as MutableList<Exam>)
                        parent.srl_grades.isRefreshing = false
                    }
        }
    }

    private fun initReExam() {
        parent.tv_grades_noData.text = "暂无补考安排"
        context?.let { context ->
            reAdapter = ReExamAdapter(
                    context = context,
                    data = data,
                    layoutIds = intArrayOf(R.layout.grades_item_exam))
            recyclerView.layoutManager = LinearLayoutManager(getContext())
            recyclerView.adapter = reAdapter

        }

        if (user != null) {
            apiService
                    .getReExam(stu = user.stuNum!!)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnErrorWithDefaultErrorHandler {
                        toast("没有获取到补考安排")
                        false
                    }
                    .safeSubscribeBy { it ->
                        if (it.isNotEmpty()) {
                            it.sorted()
                            reAdapter.addData(it as MutableList<Exam>)
                            reAdapter.isShowFooter = false
                            parent.fl_grades_noData.gone()
                        }
                    }
        }

        parent.srl_grades.setOnRefreshListener {
            refreshReExam()
        }
    }

    private fun refreshReExam() {
        reAdapter.clearData()
        if (user != null) {
            apiService
                    .getReExam(user.stuNum!!)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnErrorWithDefaultErrorHandler {
                        toast("没有获取到考试安排")
                        false
                    }
                    .safeSubscribeBy { it ->
                        it.sorted()
                        reAdapter.refreshData(it as MutableList<Exam>)
                        parent.srl_grades.isRefreshing = false
                    }
        }
    }

}
