package com.mredrock.cyxbs.skin

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.SKIN_ENTRY
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import kotlinx.android.synthetic.main.skin_main.*

@Route(path = SKIN_ENTRY)
class MainActivity : SkinBaseActivity(),View.OnClickListener {
    private var skinPath //皮肤包路径
            : String? = null
    private var skinPath2 //皮肤包路径2
            : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.skin_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        btn_default.setOnClickListener(this)
        btn_blue.setOnClickListener(this)
        doPermissionAction(Manifest.permission.READ_EXTERNAL_STORAGE){
            reason = "haha"
            doAfterGranted{
                skinPath = "/sdcard/Download/testskin.skin"
            }

            doAfterRefused{
                skinPath = ""
            }
        }


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_default -> {
                println("1")
                SkinManager.instance.restoreDefaultTheme()
            }
            R.id.btn_blue -> {
                println("2")
                SkinManager.instance.loadSkin(skinPath)
            }
        }
    }
}