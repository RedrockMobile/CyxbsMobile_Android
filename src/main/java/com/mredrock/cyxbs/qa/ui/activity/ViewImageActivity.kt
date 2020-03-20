package com.mredrock.cyxbs.qa.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.common.utils.extensions.setFullScreen
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.ui.adapter.HackyViewPagerAdapter
import kotlinx.android.synthetic.main.qa_activity_view_image.*
import org.jetbrains.anko.startActivity

/**
 * Created by yyfbe, Date on 2020/3/20.
 * 将图片点击放大独立出来，实现左右滑动功能
 */
class ViewImageActivity : AppCompatActivity() {
    companion object {
        private const val IMG_RES_PATHS = "imgResPaths"
        private const val POSITION = "position"

        fun activityStart(context: Context, imgResUrls: Array<String>, position: Int) {
            context.startActivity<ViewImageActivity>(IMG_RES_PATHS to imgResUrls, POSITION to position)
        }
    }

    private var imgUrls: Array<String>? = null
    private var position: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_view_image)
        setFullScreen()
        imgUrls = intent?.extras?.getStringArray(IMG_RES_PATHS)
        position = intent.getIntExtra(POSITION, 0)
        if (!imgUrls.isNullOrEmpty()) {
            hc_vp.adapter = HackyViewPagerAdapter(imgUrls)
            hc_vp.currentItem = position
        }

    }

}