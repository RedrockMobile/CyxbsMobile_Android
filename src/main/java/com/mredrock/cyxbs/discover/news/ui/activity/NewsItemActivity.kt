package com.mredrock.cyxbs.discover.news.ui.activity

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import com.afollestad.materialdialogs.MaterialDialog
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.news.R
import com.mredrock.cyxbs.discover.news.bean.NewsAttachment
import com.mredrock.cyxbs.discover.news.utils.FileTypeHelper
import com.mredrock.cyxbs.discover.news.viewmodel.NewsItemViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.news_activity_detail.*
import java.io.File

class NewsItemActivity : BaseViewModelActivity<NewsItemViewModel>(), NewsItemViewModel.NewsDownloadListener {
    private val files = mutableListOf<File>()
    private var downloadNeedSize = 0
    private var downloadEndSize = 0

    private val permissionDialog by lazy {
        AlertDialog.Builder(this)
                .setTitle("权限遭拒")
                .setMessage("没有「存储空间」权限就无法下载哦。\n请轻触「马上去设置」按钮，然后选择「权限」，并给掌上重邮授予「存储空间」权限。")
                .setPositiveButton("马上去设置") { _, _ ->
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", packageName, null)
                    })
                }
                .setNegativeButton("放弃") { _, _ -> }
                .create()
    }

    private fun showOpenFileDialog() {
        MaterialDialog.Builder(this)
                .items(files.map { it.name })
                .itemsCallbackSingleChoice(-1) { _, _, which, _ ->
                    if (which != -1) {
                        val file = files[which]
                        if (file.exists()) {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .setDataAndType(Uri.fromFile(file), FileTypeHelper.getMIMEType(file)))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    return@itemsCallbackSingleChoice true
                }
                .title("下载附件")
                .positiveText("确定")
                .negativeText("取消")
                .show()
    }

    override fun onDownloadStart() {
        viewModel.progressDialogEvent.value = ProgressDialogEvent.SHOW_CANCELABLE_DIALOG_EVENT
    }

    override fun onProgress(id: Int, currentBytes: Long, contentLength: Long) {
        //todo 如果需要进度回调
    }

    @Synchronized
    override fun onDownloadEnd(id: Int, file: File?, e: Throwable?) {
        if (file != null) {
            files.add(file)
        } else {
            e?.printStackTrace()
            AndroidSchedulers.mainThread().scheduleDirect {
                when (e?.message) {
                    "permission deny" -> permissionDialog.show()
                    else -> viewModel.toastEvent.value = R.string.news_download_error
                }
                viewModel.progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT
            }
        }
        downloadEndSize++
        if (downloadEndSize == downloadNeedSize) {
            AndroidSchedulers.mainThread().scheduleDirect {
                viewModel.progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT
                showOpenFileDialog()
            }
        }
    }

    override val viewModelClass = NewsItemViewModel::class.java
    override val isFragmentActivity = false

    private lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_activity_detail)

        common_toolbar.init("详情")

        rxPermissions = RxPermissions(this)

        viewModel.news.observe(this, Observer {
            it ?: return@Observer

            tv_title.text = it.title
            tv_time.text = it.pubTime
            tv_detail.text = if (it.content.length < 10 && it.content.contains("(见附件)")) {
                intent.getStringExtra("title")
            } else {
                it.content
            }
        })

        intent.getIntExtra("id", -1).let {
            if (it == -1) {
                viewModel.toastEvent.value = R.string.news_init_error
                finish()
            } else {
                viewModel.getNews(it)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.news_menu, menu)
        menu?.getItem(0)?.setOnMenuItemClickListener { _ ->
            val items = viewModel.news.value?.attachment
            if (items == null) {
                viewModel.toastEvent.value = R.string.news_init
                return@setOnMenuItemClickListener false
            }
            MaterialDialog.Builder(this)
                    .items(items.map {
                        it.name
                    })
                    .itemsCallbackMultiChoice(null) { _, positions, _ ->
                        val list = mutableListOf<NewsAttachment>()
                        positions.forEach {
                            list.add(items[it])
                        }
                        if (list.isNotEmpty()) {
                            downloadNeedSize = list.size
                            viewModel.download(rxPermissions, list, this)
                        }
                        true
                    }
                    .title("下载附件")
                    .positiveText("确定")
                    .negativeText("取消")
                    .show()
            return@setOnMenuItemClickListener false
        }
        return super.onCreateOptionsMenu(menu)
    }

}
