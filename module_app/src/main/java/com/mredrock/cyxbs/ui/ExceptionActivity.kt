package com.mredrock.cyxbs.ui

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess


/**
 * 调试用的异常activity，此activity不要继承BaseActivity
 */
class ExceptionActivity : AppCompatActivity() {
    companion object {
        const val STACK_INFO = "stack_info"
        const val DEVICE_INFO = "device_info"

        fun start(context: Context, stackInfo: String, deviceInfo: String) {
            val intent = Intent(context, ExceptionActivity::class.java)
                .putExtra(STACK_INFO, stackInfo)
                .putExtra(DEVICE_INFO, deviceInfo)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stackInfo = intent.getStringExtra(STACK_INFO)
        val deviceInfo = intent.getStringExtra(DEVICE_INFO)
    
        fun SpannableStringBuilder.appendLine(text: CharSequence, vararg spans: Any) {
            val textLength = text.length
            append(text)
            spans.forEach { span ->
                setSpan(span, this.length - textLength, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            appendLine()
        }

        val sb = SpannableStringBuilder().apply {
            appendLine("StackInfo:", ForegroundColorSpan(Color.parseColor("#FF0006")))
            appendLine(stackInfo)
            appendLine("deviceInfo:", ForegroundColorSpan(Color.parseColor("#FF0006")))
            appendLine(deviceInfo)
            setSpan(TypefaceSpan("monospace"), 0, length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        AlertDialog.Builder(this).apply {
            setMessage(sb)
            setTitle("哦豁，掌上重邮崩溃了！")
            setNegativeButton("复制异常信息退出") { _: DialogInterface, i: Int ->
                val message = "StackInfo:\n$stackInfo\ndeviceInfo:\n$deviceInfo"
                val cm = this@ExceptionActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("exception trace stack", message))
                this@ExceptionActivity.finish()
                Process.killProcess(Process.myPid())
                exitProcess(1)
            }
            setCancelable(false)
            setFinishOnTouchOutside(false)
        }.show()

    }
}