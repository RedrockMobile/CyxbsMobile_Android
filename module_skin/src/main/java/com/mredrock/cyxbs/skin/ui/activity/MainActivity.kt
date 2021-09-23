package com.mredrock.cyxbs.skin.ui.activity

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.SKIN_ENTRY
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.skin.R
import com.mredrock.cyxbs.skin.ui.adapter.SkinRvAdapter
import com.mredrock.cyxbs.skin.viewmodel.SkinViewModel
import kotlinx.android.synthetic.main.skin_main.*

/**
 * Created by LinTong
 * Description:换肤主Activity
 */
@Route(path = SKIN_ENTRY)
class MainActivity : BaseViewModelActivity<SkinViewModel>(), View.OnClickListener {
    private var skinPath //皮肤包路径
            : String? = null
    private var skinPath2 //皮肤包路径2
            : String? = null
    private lateinit var adapter: SkinRvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.skin_main)
        adapter = SkinRvAdapter(this, mutableListOf())
        skin_rv.layoutManager = GridLayoutManager(this,2)
//        skin_rv.adapter = adapter
        viewModel.init()
        viewModel.skinInfo.observe(this, Observer {data ->
            val skinData = data.skin_data
            adapter.notifyList(skinData)
            skin_rv.adapter = adapter
        })

        btn_default.setOnClickListener(this)
        btn_blue.setOnClickListener(this)
        common_toolbar.apply {
            initWithSplitLine("换肤",
                    false,
                    R.drawable.skin_ic_arrow_left,
                    View.OnClickListener {
                        finishAfterTransition()
                    })
        }
        doPermissionAction(Manifest.permission.READ_EXTERNAL_STORAGE) {
            reason = "haha"
            doAfterGranted {
                skinPath = "/sdcard/Download/testskin.skin"
            }

            doAfterRefused {
                skinPath = ""
            }
        }


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_default -> {
                SkinManager.restoreDefaultTheme()
            }
            R.id.btn_blue -> {
                println("2")
                SkinManager.loadSkin(skinPath)
            }
        }
    }
}