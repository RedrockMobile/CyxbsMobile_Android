package com.mredrock.cyxbs.freshman.view.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.config.FRESHMAN_ENTRY
import com.mredrock.cyxbs.common.event.GoToDiscoverEvent
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.view.adapter.FreshAdapter
import com.mredrock.cyxbs.freshman.view.adapter.OnItemClickListener
import com.mredrock.cyxbs.freshman.view.dialog.EnvelopDialog
import com.mredrock.cyxbs.freshman.view.widget.RotateBanner
import com.mredrock.cyxbs.freshman.view.widget.bubble.BubbleView
import kotlinx.android.synthetic.main.freshman_activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find

@Route(path = FRESHMAN_ENTRY)
class MainActivity : BaseActivity() {

    override val isFragmentActivity: Boolean
        get() = false
    private lateinit var banner: RotateBanner
    private lateinit var bv1: BubbleView
    private lateinit var bv2: BubbleView
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_main)
        bv1 = find(R.id.bv_1)
        bv2 = find(R.id.bv_2)
        rv = find(R.id.rv_fresh_item)
        banner = find(R.id.banner)
        val layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager
        val adapter = FreshAdapter()
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    1 -> {
                        val intent = Intent(this@MainActivity, EnrollmentRequirementsActivity::class.java)
                        startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(this@MainActivity, CampusMapActivity::class.java)
                        startActivity(intent)
                    }
                    3 -> {
                        val intent = Intent(this@MainActivity, EnrollmentProcessActivity::class.java)
                        startActivity(intent)
                    }
                    4 -> {
                        val intent = Intent(this@MainActivity, CampusGuidelinesActivity::class.java)
                        startActivity(intent)
                    }
                    5 -> {
                        val intent = Intent(this@MainActivity, OnlineCommunicationActivity::class.java)
                        startActivity(intent)
                    }
                    6 -> {
                        val intent = Intent(this@MainActivity, MoreActivity::class.java)
                        startActivity(intent)
                    }
                    7 -> {
                        startActivity<AboutUsActivity>()
                    }
                }
            }
        })
        rv.adapter = adapter
        rv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                when ((view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition) {
                    0 -> outRect.top = dp2px(20F)
                }
            }
        })
        val shared = context.sharedPreferences("HasEnvelop")
        if (!shared.getBoolean("envelop", false)) {
            showDialog()
        }
        iv_back.setOnClickListener { finish() }

    }

    //开始surfaceView动画
    override fun onResume() {
        super.onResume()
        bv1.start()
        bv2.start()
    }

    //暂停surfaceView动画
    override fun onPause() {
        bv1.stop()
        bv2.stop()
        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        banner.openAnimation()
    }

    private fun showDialog() {
        val dialog = EnvelopDialog(this, R.style.FreshmanDialog_Dialog)
        dialog.show()
        context.sharedPreferences("HasEnvelop").editor {
            putBoolean("envelop", true)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun finishActivity(event : GoToDiscoverEvent){
        finish()
    }

}
