package com.mredrock.cyxbs.freshman.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.MAIN_MAIN
import com.mredrock.cyxbs.common.event.GoToDiscoverEvent
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.view.adapter.MoreActivtyAdapter
import com.mredrock.cyxbs.freshman.view.adapter.OnItemClickListener
import com.mredrock.cyxbs.freshman.view.fragment.MoreDialogFragment
import com.mredrock.cyxbs.freshman.view.fragment.MoreDialogListener
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.find


/**
 * Create by roger
 * on 2019/8/3
 */
class MoreActivity : BaseActivity(), MoreDialogListener {
    override fun onDialogLongClick() {
        val intent = Intent(this@MoreActivity, ChooseSaveQrActivity::class.java)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    val bitmap = ResourcesCompat.getDrawable(resources, R.drawable.freshman_dialog_qr_code, null) as BitmapDrawable
                    com.mredrock.cyxbs.freshman.view.widget.saveImage(bitmap.bitmap)
                }
            }
        }
    }

    override fun onDialogClick() {

    }

    override val isFragmentActivity: Boolean
        get() = false

    private lateinit var recyclerView: RecyclerView

    private fun initToolbar() {
        common_toolbar.init(
                title = resources.getString(R.string.freshman_more),
                listener = View.OnClickListener { finish() }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_more)
        initToolbar()
        recyclerView = find(R.id.rv_more)
        val adapter = MoreActivtyAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://web.redrock.team/welcome2019/mobile/")))
                    1 -> {
                        ARouter.getInstance().build(MAIN_MAIN).navigation()
                        EventBus.getDefault().apply {
                            post(GoToDiscoverEvent())
                        }
                        finish()
                    }
                    2 -> showDialog()
                }
            }

        })
    }

    private fun showDialog() {
        val dialogFragment = MoreDialogFragment()
        dialogFragment.show(supportFragmentManager, "more_dialog")
    }

}

