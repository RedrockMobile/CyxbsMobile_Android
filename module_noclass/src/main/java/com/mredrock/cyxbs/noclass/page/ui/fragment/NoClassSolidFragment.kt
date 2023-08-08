package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.noclass.R

/**
 * 固定分组的fragment
 */
class NoClassSolidFragment : BaseFragment(R.layout.noclass_fragment_solid) {

    /**
     * 上方添加同学的编辑框
     */
    private val mEditTextView : EditText by R.id.noclass_solid_et_add_classmate.view()

    /**
     * 是否有更改值
     */
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val extra = intent?.getSerializableExtra("GroupListResult")
            if (extra != null){
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NoClassSolidFragment().apply {}
    }
}