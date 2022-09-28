package com.mredrock.cyxbs.lib.debug.crash

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.debug.R
import com.mredrock.cyxbs.lib.debug.SecretActivity
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import java.io.PrintWriter
import java.io.Serializable
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * .
 *
 * @author 985892345
 * @date 2022/9/23 15:56
 */
class CrashActivity : BaseActivity() {
  
  companion object {
    fun start(throwable: Throwable, processName: String, threadName: String) {
      appContext.startActivity(
        Intent(appContext, CrashActivity::class.java)
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          .putExtra(CrashActivity::mStackTrace.name, throwable.collectStackTrace())
          .putExtra(
            CrashActivity::mRebootIntent.name,
            // 重新启动整个应用的 intent
            appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)!!
              .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
          ).putExtra(CrashActivity::mMainProcessPid.name, Process.myPid())
          .putExtra(
            CrashActivity::mNetworkResult.name,
            ApiGenerator.apiResultList.mapTo(ArrayList()) {
              // 因为 CrashActivity 是在另一个进程中启动，所以只能以 String 的形式传过去
              NetworkApiResult(it.first.toString(), it.second.toString(), it.third.toString())
            }
          ).putExtra(CrashActivity::mProcessName.name, processName)
          .putExtra(CrashActivity::mThreadName.name, threadName)
      )
    }
  
    private fun Throwable.collectStackTrace(): String {
      val writer = StringWriter()
      val printWriter = PrintWriter(writer)
      printStackTrace(printWriter)
      var cause = cause
      var position = 1
      while (cause != null) {
        printWriter.println()
        printWriter.print("第 $position 个 Caused By: ")
        position++
        cause.printStackTrace(printWriter)
        cause = cause.cause
      }
      printWriter.close()
      return writer.toString()
    }
    
    class NetworkApiResult(
      val request: String,
      val response: String,
      val stackTrace: String
    ) : Serializable
  }
  
  private val mStackTrace by intent<String>()
  private val mRebootIntent by intent<Intent>()
  private val mMainProcessPid by intent<Int>()
  private val mNetworkResult by intent<ArrayList<NetworkApiResult>>()
  private val mProcessName by intent<String>()
  private val mThreadName by intent<String>()
  
  private val mTvProcess by R.id.debug_tv_process_crash.view<TextView>()
  private val mTvThread by R.id.debug_tv_thread_crash.view<TextView>()
  private val mScaleScrollTextView by R.id.debug_ssv_crash.view<ScaleScrollTextView>()
  private val mBtnCopy by R.id.debug_btn_copy_crash.view<Button>()
  private val mBtnReboot by R.id.debug_btn_reboot_crash.view<Button>()
  private val mBtnNetwork by R.id.debug_btn_network_crash.view<Button>()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Process.killProcess(mMainProcessPid); // Kill original main process
    setContentView(R.layout.debug_activity_crash)
    initTextView()
    initShowStackTrace()
    initClick()
    toast("哦豁，掌上重邮崩溃了！")
  }
  
  @SuppressLint("SetTextI18n")
  private fun initTextView() {
    mTvProcess.text = "崩溃进程名：$mProcessName"
    mTvThread.text = "崩溃线程名：$mThreadName"
  }
  
  private fun initShowStackTrace() {
    val builder = SpannableStringBuilder(mStackTrace)
    val regex = Regex("(?<=.{1,999})\\(\\w+\\.kt:\\d+\\)")
    val result = regex.findAll(builder)
    result.forEach {
      builder.setSpan(
        ForegroundColorSpan(Color.RED),
        it.range.first,
        it.range.last + 1,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
    }
    mScaleScrollTextView.setText(builder)
  }
  
  private fun initClick() {
    mBtnCopy.setOnClickListener {
      val cm = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      cm.setPrimaryClip(ClipData.newPlainText("掌邮崩溃记录", mStackTrace))
      toast("复制成功！")
    }
    
    var isReboot: Boolean? = null
    val rebootRunnable = Runnable {
      startActivity(mRebootIntent)
      finish()
      exitProcess(0)
    }
    mBtnReboot.setOnClickListener {
      when (isReboot) {
        null -> {
          if (mProcessName != appContext.packageName) {
            toast("该异常为其他进程异常，直接按返回键即可")
            isReboot = false
          } else {
            toast("两秒后将重启，再次点击取消")
            it.postDelayed(rebootRunnable, 2000)
            isReboot = true
          }
        }
        true -> {
          toast("取消重启成功")
          it.removeCallbacks(rebootRunnable)
          isReboot = null
        }
        false -> {
          toast("两秒后将重启，再次点击取消")
          it.postDelayed(rebootRunnable, 2000)
          isReboot = true
        }
      }
    }
    
    mBtnNetwork.setOnClickListener {
      SecretActivity.tryStart {
        NetworkApiResultActivity.start(mNetworkResult)
      }
    }
  }
  
  private var mLastBackPressedTime = 0L
  
  override fun onBackPressed() {
    if (mProcessName == appContext.packageName) {
      val nowTime = System.currentTimeMillis()
      if (nowTime - mLastBackPressedTime > 2000) {
        toast("主进程已崩溃，返回键将退出应用，再次返回即可退出")
        mLastBackPressedTime = nowTime
      } else {
        super.onBackPressed()
      }
    } else {
      super.onBackPressed()
    }
  }
}