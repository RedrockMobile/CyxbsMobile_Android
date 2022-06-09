package script

import com.ss.android.ugc.bytex.shrinkR.ShrinkRExtension
import com.ss.android.ugc.bytex.shrinkR.res_check.AssetsCheckExtension
import com.ss.android.ugc.bytex.shrinkR.res_check.ResourceCheckExtension

//apply(plugin = "bytex.shrink_r_class")

/*
plugins {
    id("bytex.shrink_r_class")
}

configure<ShrinkRExtension> {
    enable(true)
    enableInDebug(false)
    logLevel("DEBUG")
    keepList = listOf(
        // keep android.support.constraint.R里所有id
        "android.support.constraint.R.id",
        // keep 所有以im_e为前缀的drawable字段
        "R.drawable.im_e+",
    )
    configure<ResourceCheckExtension> {
        enable(true)// 无用资源检查的开关
        // 根据资源所在的路径做模糊匹配（因为第三方库用到的冗余资源没法手动删）
        onlyCheck = listOf( // 只检查主工程里的资源
            "module_app/build"
        )
        // 检查白名单。这些资源就算是冗余资源也不会report出来
        keepRes = listOf(
            "R.drawable.ic_list_dou_order",
            "R.string.snapchat_tiktok_client_id",
            "R.string.snapchat_musically_client_id",
            "R.string.fb_account_kit_client_token",
            "R.string.mapbox_*",
            "R.string.kakao*",
            "R.dimen",
            "R.color",
            "R.animator",
            "R.integer",
            "R.bool",
            "R.style",
            "R.styleable",
            "R.attr",
            "R.xml",
            "R.array",
            "R.string"
        )
    }

    configure<AssetsCheckExtension> {
        enable(true) // 冗余assets资源检查开关
        keepBySuffix = listOf(
            ".model",
            ".otf",
            ".ttf"
        )
        keepAssets = listOf(
            "start_anim/",
            "Contour_2D/",
        )
    }
}*/
