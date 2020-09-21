package com.mredrock.cyxbs.discover.noclass.pages.noclass

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.config.COURSE_ENTRY
import com.mredrock.cyxbs.common.config.DISCOVER_NO_CLASS
import com.mredrock.cyxbs.common.config.STU_NAME_LIST
import com.mredrock.cyxbs.common.config.STU_NUM_LIST
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.getScreenHeight
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.discover.noclass.R
import com.mredrock.cyxbs.discover.noclass.network.Student
import com.mredrock.cyxbs.discover.noclass.pages.stuselect.NoClassStuSelectActivity
import kotlinx.android.synthetic.main.noclass_activity_no_class.*
import java.io.Serializable

@Route(path = DISCOVER_NO_CLASS)
class NoClassActivity : BaseViewModelActivity<NoClassViewModel>() {

    override val viewModelClass = NoClassViewModel::class.java

    override val isFragmentActivity = false

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private var mStuList: MutableList<Student>? = null
    private var mAdapter: NoClassRvAdapter? = null

    companion object {
        const val REQUEST_SELECT = 1
    }

//    val mDataBinding:Disc


    override fun onCreate(savedInstanceState: Bundle?) {
        isSlideable = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_no_class)

        common_toolbar.initWithSplitLine("没课约")
        mStuList = ArrayList()
        initStuList()
        initBtn()
        initObserver()
        initEditText()
    }

    private fun initObserver() {
        viewModel.mStuList.observe(this, Observer {
            when {
                it!!.size > 1 -> {
                    val bundle = Bundle()
                    bundle.putSerializable("stu_list", it as Serializable)
                    val intent = Intent(this, NoClassStuSelectActivity::class.java)
                    intent.putExtras(bundle)
                    startActivityForResult(intent, REQUEST_SELECT, ActivityOptionsCompat.makeScaleUpAnimation(cl_noclass, getScreenWidth() / 2, getScreenHeight(), getScreenWidth(), getScreenHeight()).toBundle())
                }
                it.size == 1 -> {
                    addStu(it[0])
                }
            }
        })
    }

    fun doSearch(str: String) {
        viewModel.getStudent(str)
    }

    private fun initBtn() {
        bottomSheetBehavior = BottomSheetBehavior.from(course_bottom_sheet_content)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        et_noclass_add_classmate.apply {
                            clearFocus()
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        et_noclass_add_classmate.apply {
                            isFocusable = true
                            isFocusableInTouchMode = true
                            requestFocus()
                        }
                    }
                    else -> {
                        //忽略
                    }
                }
            }

        })
        noclass_btn_query.setOnClickListener {
            val students = (noclass_rv.adapter as NoClassRvAdapter).getStuList()
            val nameList = arrayListOf<String>()
            val numList = arrayListOf<String>()
            students.forEach {
                numList.add(it.stunum ?: "")
                nameList.add(it.name ?: "")
            }
            val fragment = getFragment(COURSE_ENTRY).apply {
                arguments = Bundle().apply {
                    putStringArrayList(STU_NUM_LIST, numList)
                    putStringArrayList(STU_NAME_LIST, nameList)
                }
            }
            //在滑动下拉课表容器中添加整个课表
            supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, fragment).apply {
                commit()
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initStuList() {
        val user = ServiceManager.getService(IAccountService::class.java).getUserService()
        user.apply {
            val stu = Student()
            stu.name = getRealName()
            stu.stunum = getStuNum()
            mStuList!!.add(stu)
        }
        mAdapter = NoClassRvAdapter(mStuList!!, this)
        noclass_rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        noclass_rv.adapter = mAdapter
    }

    private fun getFragment(path: String) = ARouter.getInstance().build(path).navigation() as Fragment


    private fun addStu(stu: Student) {
        mAdapter?.addStu(stu)
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return

        if (requestCode == REQUEST_SELECT && resultCode == Activity.RESULT_OK) {
            val stu = data.extras?.getSerializable("stu") as Student
            mAdapter!!.addStu(stu)
        }
    }

    private fun initEditText() {
        et_noclass_add_classmate.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = et_noclass_add_classmate.text.toString().trim()
                if (TextUtils.isEmpty(key)) {
                    toast("输入为空")
                    return@setOnEditorActionListener true
                }
                doSearch(key)
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                et_noclass_add_classmate.setText("")
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

    }

}
