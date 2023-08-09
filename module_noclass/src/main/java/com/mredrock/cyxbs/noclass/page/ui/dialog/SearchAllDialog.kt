package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.CLASS
import com.mredrock.cyxbs.noclass.bean.Cls
import com.mredrock.cyxbs.noclass.bean.GROUP
import com.mredrock.cyxbs.noclass.bean.GroupDetail
import com.mredrock.cyxbs.noclass.bean.NoClassItem
import com.mredrock.cyxbs.noclass.bean.NoClassTemporarySearch
import com.mredrock.cyxbs.noclass.bean.STUDENT
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.adapter.TemporarySearchAdapter

/**
 * 搜索所有，包括学生，分组，班级
 */
class SearchAllDialog(
    private val searchResult : NoClassTemporarySearch
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.noclass_dialog_search_student)
        val mScreenWidth : Int = resources.displayMetrics.widthPixels
        val mScreenHeight : Int = resources.displayMetrics.heightPixels
        dialog.findViewById<ConstraintLayout>(R.id.test_ccc).apply {
            this.layoutParams.height = mScreenHeight
            this.layoutParams.width = mScreenWidth
            requestLayout()
        }
        initView(dialog)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        if (dialog is BottomSheetDialog) {
            val behaviour = (dialog as BottomSheetDialog).behavior
            behaviour.isDraggable=false
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
    }

    private fun initView(dialog : Dialog){
        dialog.findViewById<TextView>(R.id.tv_noclass_search_student_cancel).apply {
            setOnClickListener {
                dialog.cancel()
            }
        }
        //点击空白处取消
        dialog.setCancelable(true)
        //设置dialog中的rv
        dialog.findViewById<RecyclerView>(R.id.rv_noclass_search_container).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TemporarySearchAdapter().apply {
                val data = searchResult.data
                val searchResultList = ArrayList<NoClassItem>()
                // 是否只有分组
                var isOnlyGroup = true
                //遍历类型，可能为学生，分组，班级，也可能为学生和分组重名组合
                for (type in data.types){
                    when(type){
                        STUDENT -> {
                            setOnClickStudent {
                                onClickStudent?.invoke(it)
                                dialog.cancel()
                            }
                            isOnlyGroup = false
                            searchResultList.addAll(data.students)
                        }
                        CLASS -> {
                            setOnClickClass {
                                onClickClass?.invoke(it)
                                dialog.cancel()
                            }
                            isOnlyGroup = false
                            searchResultList.add(data.`class`)
                        }
                        GROUP -> {
                            setOnClickGroup {
                                onClickGroup?.invoke(it)
                                dialog.cancel()
                            }
                            searchResultList.add(data.group)
                            // 如果只有分组，此时才显示分组下面的学生
                            if (isOnlyGroup){
                                searchResultList.addAll(data.group.members)
                                //单独设置的原因是因为要求点击分组下面的组员要求弹窗不消失
                                setOnClickStudent {
                                    onClickStudent?.invoke(it)
                                    deleteStudent(it)
                                }
                            }
                        }
                    }
                }
                submitList(searchResultList)
            }
        }
    }

    //点击学生的回调
    private var onClickStudent : ((stu : Student) -> Unit)? = null

    fun setOnClickStudent(onClickStudent : (stu : Student) -> Unit){
        this.onClickStudent = onClickStudent
    }

    //点击group的回调
    private var onClickGroup : ((groupDetail: GroupDetail) -> Unit)? = null

    fun setOnClickGroup(onClickGroup : (groupDetail: GroupDetail) -> Unit){
        this.onClickGroup = onClickGroup
    }

    //点击class的回调
    private var onClickClass : ((cls: Cls) -> Unit)? = null

    fun setOnClickClass(onClickClass : (cls: Cls) -> Unit){
        this.onClickClass = onClickClass
    }
}