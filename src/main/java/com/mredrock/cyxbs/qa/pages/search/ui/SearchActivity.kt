package com.mredrock.cyxbs.qa.pages.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.event.QASearchEvent
import com.mredrock.cyxbs.qa.pages.search.room.QAHistory
import com.mredrock.cyxbs.qa.pages.search.ui.fragment.QuestionSearchedFragment
import com.mredrock.cyxbs.qa.pages.search.ui.fragment.QuestionSearchingFragment
import com.mredrock.cyxbs.qa.pages.search.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.qa_activity_question_search.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.intentFor

/**
 * Created by yyfbe, Date on 2020/8/12.
 */
class SearchActivity : BaseViewModelActivity<SearchViewModel>(), EventBusLifecycleSubscriber {
    override val viewModelClass: Class<SearchViewModel> = SearchViewModel::class.java
    override val isFragmentActivity: Boolean
        get() = true
    private val questionSearchingFragment: QuestionSearchingFragment by lazy(LazyThreadSafetyMode.NONE) { QuestionSearchingFragment() }
    private val questionSearchedFragment: QuestionSearchedFragment by lazy(LazyThreadSafetyMode.NONE) { QuestionSearchedFragment() }

    companion object {
        fun activityStart(fragment: Fragment) {
            val intent = fragment.context?.intentFor<SearchActivity>()
            fragment.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_question_search)
        viewModel.getHistoryFromDB()
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        tv_question_search_cancel.setOnClickListener { finish() }
        supportFragmentManager.beginTransaction().replace(R.id.fcv_question_search, questionSearchingFragment).commit()
        rl_question_searching.setOnTouchListener { v, event -> et_question_search.onTouchEvent(event) }
        et_question_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                turnToResult(v.text.toString())
                viewModel.insert(QAHistory(v.text.toString(), System.currentTimeMillis()))
            }
            false
        }
        et_question_search.setOnTouchListener { _, _ ->
            turnToSearching()
            false
        }

    }

    private fun turnToSearching() {
        val curFragment = supportFragmentManager.findFragmentById(R.id.fcv_question_search)
        if (curFragment is QuestionSearchedFragment) {
            supportFragmentManager.beginTransaction().replace(R.id.fcv_question_search, questionSearchingFragment).commit()
        }

    }

    private fun turnToResult(keyWord: String) {
        val bundle = Bundle()
        val curFragment = supportFragmentManager.findFragmentById(R.id.fcv_question_search)
        bundle.putString(QuestionSearchedFragment.SEARCH_KEY, keyWord)
        if (curFragment is QuestionSearchedFragment) {
            curFragment.refreshSearchKey(keyWord)
            curFragment.arguments = bundle
            curFragment.invalidate()
        } else {
            questionSearchedFragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.fcv_question_search, questionSearchedFragment).commit()
        }

        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun startSearching(e: QASearchEvent) {
        et_question_search.setText(e.searchKey)
        turnToResult(e.searchKey)
    }

}
