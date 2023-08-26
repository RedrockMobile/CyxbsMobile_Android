package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.adapter.NoClassAddToGroupAdapter
import com.mredrock.cyxbs.noclass.page.viewmodel.fragment.SolidViewModel

/**
 * 将该学生添加到指定的分组中
 */

class AddToGroupDialog(
    private val groupList: List<NoClassGroup>,
    private val student: Student
) : BottomSheetDialogFragment() {

    /**
     * 分组文字的recyclerView
     */
    private lateinit var mRv : RecyclerView

    /**
     * seekbar
     */
    private lateinit var mSb : SeekBar

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
        initRv()
        initScroll()
        initObserver()
        return dialog
    }

    private fun initObserver() {
        mSolidViewModel.saveState.observe(this){
            if (!it){
                toast("添加失败")
            }
        }
    }

    private fun initRv() {
        mRv.apply {
            layoutManager = GridLayoutManager(requireContext(),3,RecyclerView.HORIZONTAL,false)
            adapter = NoClassAddToGroupAdapter(groupList,context){ isOk , chooseGroup ->
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
    }


    private fun initView(dialog : Dialog) {
        mRv = dialog.findViewById(R.id.noclass_add_to_group_rv_container)
        mSb = dialog.findViewById(R.id.noclass_add_to_group_seekbar)
        // 弹窗的取消按钮
        dialog.findViewById<TextView>(R.id.noclass_add_to_group_tv_cancel).apply {
            setOnClickListener {
                dismiss()
            }
        }
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

    /**
     * 这个方法用来rv和seekbar协作滑动，滑动事件监听
     */
    private fun initScroll() {
        mRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val haveScrolled = recyclerView.computeHorizontalScrollOffset().toDouble()
                val rvMaxWidth = recyclerView.computeHorizontalScrollRange().toDouble()
                val percent: Double = haveScrolled / rvMaxWidth
                val processWidth = mSb.measuredWidth
                val scrollDistance = processWidth * percent
                mSb.progress = scrollDistance.toInt()
            }
        })

        // 等待布局完成之后
        mRv.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (mRv.width >= resources.displayMetrics.widthPixels){
                mSb.visible()
            }
        }
        mSb.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val haveScrolled = progress.toDouble()
                    val sbMaxWidth = mSb.measuredWidth.toDouble()
                    val percent: Double = haveScrolled / sbMaxWidth
                    val rvScrollWidth =
                        mRv.computeHorizontalScrollRange().toDouble()
                    val scrollDistance = rvScrollWidth * percent
                    mRv.smoothScrollToPosition(scrollDistance.toInt())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}