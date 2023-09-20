package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
import com.mredrock.cyxbs.noclass.page.adapter.NoClassSolidAdapter
import com.mredrock.cyxbs.noclass.page.ui.activity.GroupDetailActivity
import com.mredrock.cyxbs.noclass.page.ui.dialog.AddToGroupDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.CreateGroupDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchNoExistDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchStudentDialog
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.NoClassViewModel
import com.mredrock.cyxbs.noclass.page.viewmodel.fragment.SolidViewModel
import com.mredrock.cyxbs.noclass.util.alphaAnim

/**
 * 固定分组的fragment
 */
class NoClassSolidFragment : BaseFragment(R.layout.noclass_fragment_solid) {

    /**
     * 上方添加同学的编辑框
     */
    private val mEditTextView: EditText by R.id.noclass_solid_et_add_classmate.view()

    /**
     * 界面的rv
     */
    private val mRecyclerView: RecyclerView by R.id.noclass_solid_rv_show.view()

    /**
     * 界面rv的adapter
     */
    private val mAdapter: NoClassSolidAdapter by lazy { NoClassSolidAdapter() }

    /**
     * 创建按钮
     */
    private val mBtnCreate: Button by R.id.noclass_solid_btn_create.view()

    /**
     * 删除缓冲区：可能想要删除，但是云端没删除成功，所以本地也不能删掉。必须云端删除成功本地才能删除成功
     */
    private val mWaitDeleteGroup: ArrayList<NoClassGroup> by lazy { ArrayList() }

    /**
     * 是否置顶的noClassGroup和textview，用完之后及时移除
     */
    private val mWaitIsTop: LinkedHashMap<NoClassGroup, TextView> by lazy { LinkedHashMap() }

    private val mViewModel by viewModels<SolidViewModel>()

    /**
     * 将获取所有分组放到了父activity中，原因是因为activity层在批量添加之后需要修改固定分组fragment的分组
     */
    private val mParentViewModel by activityViewModels<NoClassViewModel>()

    /**
     * 下面得提示文字，试试左滑删除列表
     */
    private val mHintText: TextView by R.id.noclass_solid_tv_hint.view()

    /**
     * 设置两秒后消失得runnable和handler，注意及时释放
     */
    private var mRunnable: Runnable? = null
    private var mHandler: Handler? = null

    /**
     * 进入组内管理界面，判断是否有更改值
     */
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val extra = intent?.getSerializableExtra("GroupDetailResult") as? NoClassGroup
                if (extra != null) {
                    // 移除旧的并且更新
                    val list = mAdapter.currentList.toMutableList()
                    for (index in 0 until list.size) {
                        if (list[index].id == extra.id) {
                            list.removeAt(index)
                            list.add(index, extra)
                            break
                        }
                    }
                    mAdapter.submitList(list)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        getAllGroup()
        initSearchEvent()
        initClickCreate()
    }

    override fun onStop() {
        super.onStop()
        mRunnable?.let { mHandler?.removeCallbacks(it) }
        mHintText.gone()
    }

    /**
     * 初始化创建的点击事件
     */
    private fun initClickCreate() {
        mBtnCreate.setOnClickListener {
            //弹出创建分组的弹窗
            CreateGroupDialog {
                mParentViewModel.getAllGroup()
            }.show(childFragmentManager, "SolidCreateGroupDialog")
        }
    }

    private fun getAllGroup() {
        mParentViewModel.getAllGroup()
    }

    private fun initView() {
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter.apply {
                setOnClickGroup {
                    startForResult.launch(
                        Intent(
                            requireContext(),
                            GroupDetailActivity::class.java
                        ).apply {
                            putExtra("NoClassGroup", it)
                        })
                    mViewModel.searchStudent.value = null
                }
                setOnClickGroupDelete {
                    //删除：加入缓冲区，并且更新云端状态
                    mWaitDeleteGroup.add(it)
                    mViewModel.deleteGroup(it.id)
                }
                setOnClickGroupIsTop { noclassGroup, tvIsTop ->
                    //置顶：加入缓冲区，更新云端置顶状态
                    mWaitIsTop[noclassGroup] = tvIsTop
                    mViewModel.updateGroup(
                        noclassGroup.id,
                        noclassGroup.name,
                        (!noclassGroup.isTop).toString()
                    )
                }
            }
        }
    }

    private fun initObserver() {
        mViewModel.searchStudent.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.isSuccess()){
                    if (it.data.isNotEmpty()) {
                        if (childFragmentManager.findFragmentByTag("SearchStudentDialog") == null){
                            SearchStudentDialog.newInstance(it.data).apply {
                                setOnAddClick { stu ->
                                    //点击加号之后的逻辑，需要弹窗选择分组加入
                                    AddToGroupDialog.newInstance(mAdapter.currentList, stu).apply {
                                        setAddCallBack {
                                            mParentViewModel.getAllGroup()
                                        }
                                    }.show(requireActivity().supportFragmentManager, "AddToGroupDialog")
                                }
                            }.show(childFragmentManager, "SearchStudentDialog")
                        }
                    } else {
                        SearchNoExistDialog(requireContext()).show()
                    }
                }else{
                    initHintText("网络异常请检查网络")
                }
            }
        }
        mParentViewModel.groupList.observe(viewLifecycleOwner) {
            mAdapter.submitListToOrder(it)
        }
        mViewModel.isDeleteSuccess.observe(viewLifecycleOwner) {
            //删除成功就把本地的删掉，删除失败就不删本地的
            if (it.second) {
                for (index in 0 until mWaitDeleteGroup.size) {
                    val item = mWaitDeleteGroup[index]
                    if (item.id == it.first) {
                        // 如果学号相等，那么就删除adapter中的item和缓冲区的item
                        mAdapter.deleteGroup(item)
                        mWaitDeleteGroup.remove(item)
                        //从前往后，遇到就break
                        break
                    }
                }
            } else {
                toast("删除失败")
            }
        }
        // 目前更新接口参数需要id，name，isTop，id和name无需更新，所以目前下面一定是置顶
        mViewModel.isUpdateSuccess.observe(viewLifecycleOwner) {
            if (it.second) {
                for (itemKey in mWaitIsTop.keys) {
                    if (itemKey.id == it.first) {
                        mAdapter.addItemToOrder(itemKey, mWaitIsTop[itemKey]!!)
                        mWaitIsTop.remove(itemKey)
                        break
                    }
                }
            } else {
                toast("更新失败")
            }
            // 用完及时置为null
        }
    }

    /**
     * 初始化下面试试左滑删除列表，设置两秒后消失
     */
    private fun initHintText(content: String? = null) {
        mHintText.alpha = 1f
        mHintText.visible()
        content?.let { mHintText.text = it }
        mHandler = Handler(Looper.getMainLooper())
        mRunnable = Runnable {
            mHintText.alphaAnim(mHintText.alpha, 0f, 200).start()
            mHintText.gone()
        }
        mHandler!!.postDelayed(mRunnable!!, 2000)
    }

    /**
     * 搜索操作的初始化
     */
    private fun initSearchEvent() {
        //防止软键盘弹起导致视图错位
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        //设置键盘上点击搜索的监听
        mEditTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearchStu()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    //开始搜索
    private fun doSearchStu() {
        val content = mEditTextView.text.toString().trim()
        if (TextUtils.isEmpty(content)) {
            toast("输入为空")
            return
        }
        mEditTextView.setText("")
        mViewModel.getSearchResult(content)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NoClassSolidFragment().apply {}
    }
}