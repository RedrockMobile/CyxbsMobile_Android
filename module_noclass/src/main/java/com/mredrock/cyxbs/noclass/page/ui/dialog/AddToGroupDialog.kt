package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.page.adapter.NoClassAddToGroupAdapter
import com.mredrock.cyxbs.noclass.widget.StickIndicator

/**
 * 将该学生添加到指定的分组中
 */

class AddToGroupDialog(
    private val groupList: List<NoclassGroup>
) : BottomSheetDialogFragment() {

    /**
     * vp，放置分组，每一页放置八个
     */
    private lateinit var mVp : ViewPager2

    /**
     * vp的指示器
     */
    private lateinit var mIndicator : StickIndicator

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.noclass_dialog_add_to_group)
        initView(dialog)
        initVp()
        return dialog
    }

    private fun initVp() {
        // 一面八条数据,满八进一
        val groupListByPage = HashMap<Int,ArrayList<String>>()
        var index = 0
        for (i in groupList.indices){
            if (groupListByPage[index] == null){
                groupListByPage[index] = ArrayList()
            }
            groupListByPage[index]!![i%8] = groupList[i].name
            // 如果不是0，且是八的倍数，那么就换页
            if (groupListByPage[index]!!.size == 8){
                index++
            }
        }
        mVp.apply {
            adapter = NoClassAddToGroupAdapter(groupListByPage,context)
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
        dialog.findViewById<Button>(R.id.noclass_add_to_group_done).apply {
            setOnClickListener {
                //todo 添加该学生到该分组
                dismiss()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
    }
}