package com.mredrock.cyxbs.lib.debug.pandora

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.lib.debug.R
import tech.linjiang.pandora.Pandora

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/22 12:49
 */
class PandoraActivity : AppCompatActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    setContentView(R.layout.debug_layout_activity_pandora)
    val et = findViewById<EditText>(R.id.debug_et_pandora)
    val btn = findViewById<Button>(R.id.debug_btn_pandora)
    
    btn.setOnClickListener {
      if (et.text.toString() == "pandora") {
        PandoraInitialService.sSpPandoraIsOpen = true
        Pandora.get().open()
        finish()
      }
    }
  }
}