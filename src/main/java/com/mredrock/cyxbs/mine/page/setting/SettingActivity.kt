package com.mredrock.cyxbs.mine.page.setting

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.SP_SHOW_MODE
import com.mredrock.cyxbs.common.config.WIDGET_SETTING
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.encrypt.md5Encoding
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.saveImage
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_setting.*
import org.greenrobot.eventbus.EventBus


/**
 * Created by zzzia on 2018/8/14.
 * 设置
 */
class SettingActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_setting)

        common_toolbar.init("设置")

        if (!BaseApp.isLogin) {
            mine_setting_exit.visibility = View.GONE
        }

        mine_setting_feedback.setOnClickListener { onFeedBackClick() }
        mine_setting_about.setOnClickListener { onAboutClick() }
        mine_setting_share.setOnClickListener { onShareClick() }
        mine_setting_exit.setOnClickListener { onExitClick() }
        mine_setting_widget.setOnClickListener { onSetWidgetClick() }

        //切换按钮
        mine_setting_switchBtn.setCheckedImmediately(defaultSharedPreferences.getBoolean(SP_SHOW_MODE, true))
        mine_setting_switchBtn.setOnClickListener {
            defaultSharedPreferences.editor {
                // todo 发给课表的Event
                val next = !defaultSharedPreferences.getBoolean("SP_SHOW_MODE", true)
                putBoolean(SP_SHOW_MODE, next)
            }
        }
    }

    private fun onExitClick() {
        runOnUiThread {
            MaterialDialog.Builder(this)
                    .title("退出登录?")
                    .content("是否退出当前账号?")
                    .positiveText("退出")
                    .negativeText("取消")
                    .onPositive { _, _ ->
                        finish()
                        //todo 刷新缓存？
//                                AppWidgetCacheAndUpdateFunc.deleteCache()
                        EventBus.getDefault().post(LoginStateChangeEvent(false))
                    }
                    .show()
        }
    }

    private fun onShareClick() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.mine_dialog_share, null, false)
        builder.setView(view).show()
        val imageView = view.findViewById(R.id.imageView) as ImageView
        imageView.setOnLongClickListener {
            doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                reason = "为保存图片需要您的文件写入权限"
                doAfterGranted {
                    val drawable = imageView.drawable
                    if (drawable is BitmapDrawable) {
                        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                        val savePath = saveImage(bitmap, md5Encoding("掌邮二维码"))
                        runOnUiThread {

                        }
                    }
                }
            }

            true
        }
    }

    private fun onAboutClick() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun onSetWidgetClick() {
        ARouter.getInstance().build(WIDGET_SETTING).navigation()
    }

    private fun onFeedBackClick() {
        if (!joinQQGroup("DXvamN9Ox1Kthaab1N_0w7s5N3aUYVIf")) {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val data = ClipData.newPlainText("QQ Group", "570919844")
            clipboard.primaryClip = data
            Toast.makeText(this, "抱歉，由于您未安装手机QQ或版本不支持，无法跳转至掌邮bug反馈群。" + "已将群号复制至您的手机剪贴板，请您手动添加",
                    Toast.LENGTH_LONG).show()
        }
    }


    /****************
     *
     * 发起添加群流程。群号：掌上重邮反馈群(570919844) 的 key 为： DXvamN9Ox1Kthaab1N_0w7s5N3aUYVIf
     * 调用 joinQQGroup(DXvamN9Ox1Kthaab1N_0w7s5N3aUYVIf) 即可发起手Q客户端申请加群 掌上重邮反馈群(570919844)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     */
    private fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            false
        }
    }
}
