package com.mredrock.cyxbs.skin.ui.activity

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.SKIN_ENTRY
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.skin.R
import com.mredrock.cyxbs.skin.bean.SkinInfo
import com.mredrock.cyxbs.skin.ui.adapter.SkinRvAdapter
import com.mredrock.cyxbs.skin.viewmodel.SkinViewModel
import kotlinx.android.synthetic.main.skin_main.*

/**
 * Created by LinTong
 * Description:换肤主Activity
 */
@Route(path = SKIN_ENTRY)
class MainActivity : BaseViewModelActivity<SkinViewModel>() {
    private var skinPath //皮肤包路径
            : String? = null
    private lateinit var adapter: SkinRvAdapter
    private val skinData = mutableListOf<SkinInfo.Data>()
    private lateinit var lastData: SkinInfo.Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.skin_main)
        lastData = SkinInfo.Data("", "", "敬请期待", " ", "--", "1", 1, "", "", "")
        adapter = SkinRvAdapter(this, mutableListOf())
        adapter.setOnItemClickListener(object : SkinRvAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, url: String, fileName: String) {
                viewModel.downloadSkin(this@MainActivity, url, fileName)
            }

            override fun onSwitchUnChecked() {
                SkinManager.restoreDefaultTheme()
            }
        })
        skin_rv.layoutManager = LinearLayoutManager(this)
        initViewModel()
        common_toolbar.apply {
            initWithSplitLine("换肤",
                    false,
                    R.drawable.common_ic_back,
                    View.OnClickListener {
                        finishAfterTransition()
                    })
            navigationIcon = SkinManager.getDrawable("common_ic_back", R.drawable.common_ic_back)
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

    private fun initViewModel() {
        viewModel.init()
        viewModel.skinInfo.observe(this, Observer { data ->
            if (data != null) {
                addLastData(data)
                adapter.notifyList(skinData)
                skin_rv.adapter = adapter
            }
        })
        viewModel.loadFail.observe(this, Observer {
            if (it) {
                CyxbsToast.makeText(this, "加载失败，使用缓存", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.downloadFail.observe(this, Observer {
            if (it) {
                CyxbsToast.makeText(this, "下载皮肤包失败", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.btn_default -> {
//                SkinManager.restoreDefaultTheme()
//            }
//            R.id.btn_blue -> {
//                println("2")
//                SkinManager.loadSkin(skinPath)
//            }
//        }
//    }

    fun addLastData(skinData: List<SkinInfo.Data>) {
        this.skinData.clear()
        this.skinData.addAll(skinData)
        this.skinData.add(lastData)
    }
}