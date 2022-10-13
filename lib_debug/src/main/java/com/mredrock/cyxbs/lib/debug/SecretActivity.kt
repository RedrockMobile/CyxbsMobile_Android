package com.mredrock.cyxbs.lib.debug

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 * .
 *
 * @author 985892345
 * @date 2022/9/23 15:18
 */
class SecretActivity : AppCompatActivity() {
  
  companion object {
  
    private var sSpPandoraIsOpen: Boolean
      get() = appContext.getSp("pandora").getBoolean("isOpen", false)
      set(value) {
        appContext.getSp("pandora").edit {
          putBoolean("isOpen", value)
        }
      }
    
    private val SuccessCallback = arrayListOf<() -> Unit>()
  
    /**
     * 在使用一些特殊功能时，需要先调用该方法判断是否已经输过密码
     *
     * 如果没有输过密码会打开该 Activity 输密码，成功后回调
     * 如果已经输过，则会直接回调
     *
     * 由于比较简单，就直接用一个回调来解决了
     */
    fun tryStart(success: () -> Unit) {
      if (sSpPandoraIsOpen) {
        success.invoke()
      } else {
        appContext.startActivity(
          Intent(appContext, SecretActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        SuccessCallback.add(success)
      }
    }
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    setContentView(R.layout.debug_activity_secret)
    val et = findViewById<EditText>(R.id.debug_et_secret)
    val btn = findViewById<Button>(R.id.debug_btn_secret)
    
    btn.setOnClickListener {
      if (et.text.toString() == "pandora") {
        sSpPandoraIsOpen = true
        SuccessCallback.forEach { it.invoke() }
        SuccessCallback.clear()
        finish()
      } else {
        toast("密码错误")
      }
    }
  }
  
  override fun onDestroy() {
    super.onDestroy()
    SuccessCallback.clear()
  }
}