package com.mredrock.cyxbs.discover.electricity.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.discover.electricity.config.*
import com.mredrock.cyxbs.electricity.R
import kotlinx.android.synthetic.main.electricity_dialog_dormitory_select.*
import kotlinx.android.synthetic.main.electricity_dialog_dormitory_select.view.*
import org.jetbrains.anko.support.v4.defaultSharedPreferences

class ElectricityFeedSettingDialogFragment(private val refresher: (id: String, room: String) -> Unit) : DialogFragment() {
    private val buildingNames by lazy(LazyThreadSafetyMode.NONE) { BUILDING_NAMES }
    private val buildingHeadNames by lazy(LazyThreadSafetyMode.NONE) { BUILDING_NAMES_HEADER }

    private var selectBuildingHeadPosition = 0
    private var selectBuildingFootPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Dialog_MinWidth)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.electricity_dialog_dormitory_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var room = defaultSharedPreferences.getString(SP_ROOM_KEY, "101") ?: "101"
        view.apply {
            et_electricity_room_num.apply {
                setText(room)
                text?.let {
                    setSelection(it.length)
                }

            }
            et_electricity_room_num.doOnTextChanged { text, _, _, _ -> room = text.toString() }
            wp_dormitory_head.setOnItemSelectedListener { _, data, _ ->
                wp_dormitory_foot.data = buildingNames[data]?.map { s -> s.replaceAfter("舍", "") }
                setCorrectBuildingNum()
            }

            if (selectBuildingHeadPosition != -1) {
                wp_dormitory_head.data = buildingHeadNames
                wp_dormitory_head.selectedItemPosition = selectBuildingHeadPosition
                wp_dormitory_foot.data = buildingNames[buildingHeadNames[selectBuildingHeadPosition]]?.map { s -> s.replaceAfter("舍", "") }
                wp_dormitory_foot.selectedItemPosition = selectBuildingFootPosition
                setCorrectBuildingNum()
            }
            wp_dormitory_foot.setOnItemSelectedListener { _, _, _ -> setCorrectBuildingNum() }

            btn_dialog_dormitory_confirm.setOnClickListener {
                selectBuildingHeadPosition = wp_dormitory_head.currentItemPosition
                selectBuildingFootPosition = wp_dormitory_foot.currentItemPosition
                val id = BUILDING_NAMES.getValue(BUILDING_NAMES_HEADER[selectBuildingHeadPosition])[selectBuildingFootPosition].split("(")[1].split("栋")[0]
                LogUtils.d("MyTag,com.mredrock.cyxbs.discover.electricity.ui.fragment","id=$id,room=$room")
                refresher.invoke(id, room)
                this@ElectricityFeedSettingDialogFragment.dismiss()

                defaultSharedPreferences.editor {
                    putInt(SP_BUILDING_HEAD_KEY, selectBuildingHeadPosition)
                    putInt(SP_BUILDING_FOOT_KEY, selectBuildingFootPosition)
                    putString(SP_ROOM_KEY, et_electricity_room_num.text.toString())
                }
            }
        }
        isCancelable = true

    }

    private fun setCorrectBuildingNum() {
        selectBuildingHeadPosition = wp_dormitory_head.currentItemPosition
        selectBuildingFootPosition = wp_dormitory_foot.currentItemPosition
        tv_dormitory_num.text = buildingNames[buildingHeadNames[selectBuildingHeadPosition]]?.get(selectBuildingFootPosition)?.substringAfter("(")?.replace(")", "")
    }

}