package com.mredrock.cyxbs.discover.electricity.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.discover.electricity.config.BUILDING_NAMES
import com.mredrock.cyxbs.discover.electricity.config.SP_BUILDING_FOOT_KEY
import com.mredrock.cyxbs.discover.electricity.config.SP_BUILDING_HEAD_KEY
import com.mredrock.cyxbs.electricity.R
import com.mredrock.cyxbs.discover.electricity.config.SP_ROOM_KEY
import kotlinx.android.synthetic.main.electricity_dialog_dormitory_notice.view.*
import kotlinx.android.synthetic.main.electricity_dialog_dormitory_select.view.*
import kotlinx.android.synthetic.main.electricity_fragment_setting.*
import kotlinx.android.synthetic.main.electricity_fragment_setting.view.*
import org.jetbrains.anko.support.v4.defaultSharedPreferences
import org.jetbrains.anko.windowManager

/**
 * Author: Hosigus
 * Date: 2018/9/13 16:33
 * Description: com.mredrock.cyxbs.electricity.ui.fragment
 */

class SettingFragment : BaseFragment() {

    private lateinit var noticeDialog: Dialog

    private lateinit var selectDialog: Dialog

    private val buildingNames = BUILDING_NAMES
    private lateinit var buildingHeadNames: Array<String>

    private var selectBuildingHeadPosition = 0
    private var selectBuildingFootPosition = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.electricity_fragment_setting, container, false)
                    ?: super.onCreateView(inflater, container, savedInstanceState)

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun refreshBuildName(){
            if (selectBuildingHeadPosition != -1 && selectBuildingFootPosition != -1) {
                val headName = buildingHeadNames[selectBuildingHeadPosition]
                val name = "$headName ${buildingNames.getValue(headName)[selectBuildingFootPosition]}"
                view.et_dormitory_building.setText(name)
            }
        }

        Glide.with(this).asGif().load(R.drawable.electricity_header_setting).listener(object :RequestListener<GifDrawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean) = false
            override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                resource?.apply {
                    setLoopCount(1)
                    startFromFirstFrame()
                }
                return false
            }

        }).into(iv_bg)

        noticeDialog = AlertDialog.Builder(context!!)
                .setView(layoutInflater.inflate(R.layout.electricity_dialog_dormitory_notice, null).apply {
                    btn_confirm.setOnClickListener {
                        noticeDialog.dismiss()
                    }
                })
                .create()
        noticeDialog.window.setBackgroundDrawableResource(R.drawable.electricity_shape_round)

        buildingHeadNames = resources.getStringArray(R.array.electricity_building_name_head)

        selectBuildingHeadPosition = defaultSharedPreferences.getInt(SP_BUILDING_HEAD_KEY, 1)
        selectBuildingFootPosition = defaultSharedPreferences.getInt(SP_BUILDING_FOOT_KEY, 1)

        val v = layoutInflater.inflate(R.layout.electricity_dialog_dormitory_select, null, false)
        v.apply {
            wp_dormitory_head.setOnItemSelectedListener { picker, data, position ->
                wp_dormitory_foot.data = buildingNames[data]
            }

            if (selectBuildingHeadPosition != -1) {
                wp_dormitory_head.selectedItemPosition = selectBuildingHeadPosition
                wp_dormitory_foot.data = buildingNames[buildingHeadNames[selectBuildingHeadPosition]]
                wp_dormitory_foot.selectedItemPosition = selectBuildingFootPosition
            }

            btn_dialog_dormitory_confirm.setOnClickListener {
                selectBuildingHeadPosition = wp_dormitory_head.currentItemPosition
                selectBuildingFootPosition = wp_dormitory_foot.currentItemPosition
                selectDialog.dismiss()
                refreshBuildName()
            }
        }

        selectDialog = Dialog(context!!).apply {
            setContentView(v)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }

        selectDialog.window.let {
            val a = it.attributes
            a.width = context!!.dp2px(380f)
            it.attributes =a
        }
        view.et_dormitory_building.apply {
            inputType = InputType.TYPE_NULL

            setOnTouchListener { _, _ ->
                selectDialog.show()
                false
            }
            refreshBuildName()
        }

        view.et_dormitory_room.setText(defaultSharedPreferences.getString(SP_ROOM_KEY, ""))

        view.btn_dormitory_confirm.setOnClickListener {
            if (selectBuildingHeadPosition != -1 && view.et_dormitory_room.text.length == 3) {
                defaultSharedPreferences.editor {
                    putInt(SP_BUILDING_HEAD_KEY, selectBuildingHeadPosition)
                    putInt(SP_BUILDING_FOOT_KEY, selectBuildingFootPosition)
                    putString(SP_ROOM_KEY, view.et_dormitory_room.text.toString())
                }
                NavHostFragment.findNavController(this).apply {
                    popBackStack()
                    popBackStack(R.id.electricity_nav_charge_fragment, true)
                    navigate(R.id.electricity_nav_charge_fragment)
                }
            } else {
                noticeDialog.show()
            }
        }
    }
}