package com.mredrock.cyxbs.widget.widget.page.trans

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.widget.little.LittleTransWidget
import kotlinx.android.synthetic.main.widget_activity_trans_config.*


/**
 * Created by zzzia on 2018/10/11.
 * 透明版设置
 * 写了一天辣鸡代码，等你们优化了
 */
class TransConfigActivity : BaseActivity() {

    private val userConfig by lazy {
        val config = TransConfig.getUserConfig(this@TransConfigActivity)
        config
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_trans_config)

        common_toolbar.init("主题设置")

        //从数据库读配置文件并复制给所有控件
        setAll(userConfig)

        setEditListener()

        //设置颜色选择的点击事件，联动
        setColorPicker(widget_config_trans_courseColorLayout, widget_config_trans_courseColor, widget_config_trans_courseTv, widget_config_trans_courseColorEt)
        setColorPicker(widget_config_trans_timeColorLayout, widget_config_trans_timeColor, widget_config_trans_timeTv, widget_config_trans_timeColorEt)
        setColorPicker(widget_config_trans_roomColorLayout, widget_config_trans_roomColor, widget_config_trans_roomTv, widget_config_trans_roomColorEt)
        setColorPicker(widget_config_trans_holderColorLayout, widget_config_trans_holderColor, null, widget_config_trans_holderColorEt)

        //预设方案的点击事件
        widget_config_trans_default_black.setOnClickListener { setAll(TransConfig.getDefaultBlack()) }
        widget_config_trans_default_white.setOnClickListener { setAll(TransConfig.getDefaultWhite()) }
        widget_config_trans_default_pink.setOnClickListener { setAll(TransConfig.getDefaultPink()) }

        //保存按钮监听
        widget_config_trans_saveBt.setOnClickListener {
            val timeSize = widget_config_trans_timeSb.progress
            val courseSize = widget_config_trans_courseSb.progress
            val roomSize = widget_config_trans_roomSb.progress
//            if (timeSize < 12 || courseSize < 12 || roomSize < 12) {
//                Toast.makeText(this@TransConfigActivity, "字体过小，请更改", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            val timeColor = widget_config_trans_timeColorEt.text.toString().replace(Regex("[^(#A-Fa-f0-9)]"), "")
            val courseColor = widget_config_trans_courseColorEt.text.toString().replace(Regex("[^(#A-Fa-f0-9)]"), "")
            val roomColor = widget_config_trans_roomColorEt.text.toString().replace(Regex("[^(#A-Fa-f0-9)]"), "")
            val holderColor = widget_config_trans_holderColorEt.text.toString().replace(Regex("[^(#A-Fa-f0-9)]"), "")

            if ((timeColor.length != 7 && timeColor.length != 9) ||
                    (courseColor.length != 7 && courseColor.length != 9) ||
                    (roomColor.length != 7 && roomColor.length != 9) ||
                    holderColor.length != 7 && holderColor.length != 9) {
                Toast.makeText(this@TransConfigActivity, "颜色有误，请修改，如#ffffff", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            TransConfig().apply {
                this@apply.timeTextSize = timeSize
                this@apply.courseTextSize = courseSize
                this@apply.roomTextSize = roomSize
                this@apply.timeTextColor = timeColor
                this@apply.courseTextColor = courseColor
                this@apply.roomTextColor = roomColor
                this@apply.holderColor = holderColor
            }.save(this)

            LittleTransWidget().refresh(this@TransConfigActivity)

            Toast.makeText(this@TransConfigActivity, "已刷新", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAll(config: TransConfig) {
        setTvColor(config)
        setTextSize(config)
        setColorEditText(config)
        setPreviewColor(config)

        //设置拖动条的监听
        setSeekBar(config)
    }

    private fun setEditListener() {
        widget_config_trans_holderColorEt.addTextChangedListener(getTextChangedListener(widget_config_trans_holderColor, null))
        widget_config_trans_timeColorEt.addTextChangedListener(getTextChangedListener(widget_config_trans_timeColor, widget_config_trans_timeTv))
        widget_config_trans_roomColorEt.addTextChangedListener(getTextChangedListener(widget_config_trans_roomColor, widget_config_trans_roomTv))
        widget_config_trans_courseColorEt.addTextChangedListener(getTextChangedListener(widget_config_trans_courseColor, widget_config_trans_courseTv))
    }

    private fun getTextChangedListener(colorView: View, textView: TextView?): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) return
                s.replace(Regex("[^(a-fA-F0-9#)]"), "")
                if (s.first() == '#' && (s.length == 7 || s.length == 9)) {
                    try {
                        val color = Color.parseColor(s.toString())
                        colorView.setBackgroundColor(color)
                        textView?.setTextColor(color)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

        }
    }

    private fun setPreviewColor(config: TransConfig) {
        try {
            widget_config_trans_timeColor.setBackgroundColor(Color.parseColor(config.timeTextColor))
            widget_config_trans_roomColor.setBackgroundColor(Color.parseColor(config.roomTextColor))
            widget_config_trans_courseColor.setBackgroundColor(Color.parseColor(config.courseTextColor))
            widget_config_trans_holderColor.setBackgroundColor(Color.parseColor(config.holderColor))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setColorEditText(config: TransConfig) {
        widget_config_trans_courseColorEt.setText(config.courseTextColor)
        widget_config_trans_roomColorEt.setText(config.roomTextColor)
        widget_config_trans_timeColorEt.setText(config.timeTextColor)
        widget_config_trans_holderColorEt.setText(config.holderColor)
    }

    private fun setTvColor(config: TransConfig) {
        try {
            widget_config_trans_courseTv.setTextColor(Color.parseColor(config.courseTextColor))
            widget_config_trans_roomTv.setTextColor(Color.parseColor(config.roomTextColor))
            widget_config_trans_timeTv.setTextColor(Color.parseColor(config.timeTextColor))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextSize(config: TransConfig) {
        widget_config_trans_courseTv.textSize = config.courseTextSize.toFloat()
        widget_config_trans_roomTv.textSize = config.roomTextSize.toFloat()
        widget_config_trans_timeTv.textSize = config.timeTextSize.toFloat()
    }

    private fun setSeekBar(config: TransConfig) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            widget_config_trans_courseSb.min = 12
//            widget_config_trans_roomSb.min = 12
//            widget_config_trans_timeSb.min = 12
//        }

        widget_config_trans_courseSb.max = 50
        widget_config_trans_courseSb.progress = config.courseTextSize
        widget_config_trans_roomSb.max = 50
        widget_config_trans_roomSb.progress = config.roomTextSize
        widget_config_trans_timeSb.max = 50
        widget_config_trans_timeSb.progress = config.timeTextSize

        widget_config_trans_courseSb.setOnSeekBarChangeListener(getSeekBarListener(widget_config_trans_courseTv))
        widget_config_trans_roomSb.setOnSeekBarChangeListener(getSeekBarListener(widget_config_trans_roomTv))
        widget_config_trans_timeSb.setOnSeekBarChangeListener(getSeekBarListener(widget_config_trans_timeTv))
    }

    private fun getSeekBarListener(tv: TextView): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                runOnUiThread {
                    tv.textSize = progress.toFloat()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        }
    }

    private fun setColorPicker(touchView: View, colorView: View, textView: TextView?, editText: EditText) {
        touchView.setOnClickListener {
            val dialog = ColorPickerDialog.newBuilder()
                    .setShowAlphaSlider(true)
                    .create()

            dialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
                override fun onDialogDismissed(dialogId: Int) {

                }

                override fun onColorSelected(dialogId: Int, color: Int) {
                    val stringColor = String.format("#%06X", (0xFFFFFFFF and color.toLong()))
                    editText.setText(stringColor)
                    textView?.setTextColor(color)
                    colorView.setBackgroundColor(color)
                }
            })
            dialog.show(fragmentManager, "颜色选择")
        }
    }
}
