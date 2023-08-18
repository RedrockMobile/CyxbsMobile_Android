package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.adapter.NoClassAddToGroupAdapter
import com.mredrock.cyxbs.noclass.page.viewmodel.fragment.SolidViewModel
import com.mredrock.cyxbs.noclass.widget.StickIndicator

/**
 * 将该学生添加到指定的分组中
 */

class AddToGroupDialog(
    private val groupList: List<NoClassGroup>,
    private val student: Student
) : BottomSheetDialogFragment() {

    /**
     * vp，放置分组，每一页放置八个
     */
    private lateinit var mVp : ViewPager2

    /**
     * vp的指示器
     */
    private lateinit var mIndicator : StickIndicator

    /**
     * 完成点击按钮
     */
    private lateinit var mBtnDone : Button

    /**
     * 选中的分组
     */
    private var chooseGroup : List<NoClassGroup>? = null

    /**
     * 固定分组fragment的viewModel
     */
    private val mSolidViewModel by viewModels<SolidViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.noclass_dialog_add_to_group)
        initView(dialog)
        initObserver()
        initVp()
        return dialog
    }

    private fun initObserver() {
        mSolidViewModel.saveState.observe(this){
            if (!it){
                toast("添加失败")
            }
        }
    }

    private fun initVp() {
        // 一面八条数据,满八进一
        val groupListByPage = HashMap<Int,ArrayList<NoClassGroup>>()
        var index = 0
        for (i in groupList.indices){
            if (groupListByPage[index] == null){
                groupListByPage[index] = ArrayList()
            }
            groupListByPage[index]!!.add(groupList[i])
            // 如果不是0，且是八的倍数，那么就换页
            if (groupListByPage[index]!!.size == 8){
                index++
            }
        }
        mVp.apply {
            adapter = NoClassAddToGroupAdapter(groupListByPage,context){ isOk , chooseGroup ->
                //true为可以点击完成,也就是深色
                if (isOk){
                    mBtnDone.setBackgroundResource(R.drawable.noclass_shape_button_common_bg)
                    //更新分组
                    this@AddToGroupDialog.chooseGroup = chooseGroup
                    mBtnDone.isClickable = true
                }else{
                    mBtnDone.setBackgroundResource(R.drawable.noclass_shape_button_save_default_bg)
                    mBtnDone.isClickable = false
                }
            }
        }
        // indicator 设置总数量
        mIndicator.setTotalCount(groupList.size / 8 + 1)
        // 增加vp页面改变的监视器
        mVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                mIndicator.setCurIndex(position)
                super.onPageSelected(position)
            }
        })
    }


    private fun initView(dialog : Dialog) {
        // 弹窗的取消按钮
        dialog.findViewById<TextView>(R.id.noclass_add_to_group_tv_cancel).apply {
            setOnClickListener {
                dismiss()
            }
        }
        mVp = dialog.findViewById(R.id.noclass_add_to_group_vp_container)
        mIndicator = dialog.findViewById(R.id.noclass_add_to_group_indicator)
        // 完成按钮
        mBtnDone = dialog.findViewById<Button>(R.id.noclass_add_to_group_done).apply {
            setOnClickListener {
                chooseGroup!!.forEach {
                    //每一个被选中的分组添加成员
                    mSolidViewModel.addAndDeleteStu(it.id, setOf(student) ,setOf())
                }
                dismiss()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
    }
}