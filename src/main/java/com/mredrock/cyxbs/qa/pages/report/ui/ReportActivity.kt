package com.mredrock.cyxbs.qa.pages.report.ui

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.AppCompatCheckedTextView
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.report.ReportViewModel
import kotlinx.android.synthetic.main.qa_activity_report.*
import org.jetbrains.anko.startActivity

class ReportActivity : BaseViewModelActivity<ReportViewModel>() {
    companion object {
        fun activityStart(context: Context, qid: String) {
            context.startActivity<ReportActivity>("qid" to qid)
        }
    }

    override val viewModelClass = ReportViewModel::class.java
    override val isFragmentActivity = false

    private var preCheckedTypeSelectorIndex = 0
    private lateinit var typeStringArray: Array<String>
    private lateinit var qid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_report)
        common_toolbar.init(getString(R.string.qa_report_title))
        qid = intent.getStringExtra("qid")
        initTypeSelector()
        btn_submit_report.setOnClickListener {
            viewModel.report(qid, edt_report_content.text.toString(), typeStringArray[preCheckedTypeSelectorIndex])
        }
    }

    private fun initTypeSelector() {
        typeStringArray = resources.getStringArray(R.array.qa_report_type)
        val typeSelectorIds = arrayOf(
                R.id.tv_report_type_1, R.id.tv_report_type_2, R.id.tv_report_type_3,
                R.id.tv_report_type_4, R.id.tv_report_type_5, R.id.tv_report_type_6
        )
        val typeSelector = Array(typeSelectorIds.size) {
            findViewById<AppCompatCheckedTextView>(typeSelectorIds[it])
        }
        typeSelector.asSequence()
                .filterIndexed { index, _ -> index < typeStringArray.size }
                .forEachIndexed { index, tv ->
                    tv.text = typeStringArray[index]
                    tv.setOnClickListener {
                        if (preCheckedTypeSelectorIndex != index) {
                            tv.toggle()
                            typeSelector[preCheckedTypeSelectorIndex].toggle()
                            preCheckedTypeSelectorIndex = index
                        }
                    }
                }
    }
}
