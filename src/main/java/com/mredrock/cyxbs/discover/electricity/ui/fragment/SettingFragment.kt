package com.mredrock.cyxbs.discover.electricity.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
import com.mredrock.cyxbs.discover.electricity.config.*
import com.mredrock.cyxbs.electricity.R
import kotlinx.android.synthetic.main.electricity_dialog_dormitory_notice.view.*
import kotlinx.android.synthetic.main.electricity_dialog_dormitory_select.view.*
import kotlinx.android.synthetic.main.electricity_fragment_setting.*
import kotlinx.android.synthetic.main.electricity_fragment_setting.view.*
import org.jetbrains.anko.support.v4.defaultSharedPreferences

/**
 * Author: Hosigus
 * Date: 2018/9/13 16:33
 * Description: com.mredrock.cyxbs.electricity.ui.fragment
 */

class SettingFragment : BaseFragment() {

    private val errNoticeDialog: Dialog by lazy(LazyThreadSafetyMode.NONE) {
        AlertDialog.Builder(context!!)
                .setView(layoutInflater.inflate(R.layout.electricity_dialog_dormitory_notice, null).apply {
                    btn_confirm.setOnClickListener {
                        errNoticeDialog.dismiss()
                    }
                })
                .create()
                .apply { window?.setBackgroundDrawableResource(R.drawable.electricity_shape_round) }
    }

    private lateinit var selectDialog: Dialog

    private val buildingNames by lazy(LazyThreadSafetyMode.NONE) { BUILDING_NAMES }
    private val buildingHeadNames by lazy(LazyThreadSafetyMode.NONE) { BUILDING_NAMES_HEADER }

    private var selectBuildingHeadPosition = 0
    private var selectBuildingFootPosition = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.electricity_fragment_setting, container, false)
                    ?: super.onCreateView(inflater, container, savedInstanceState)

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun refreshBuildName() {
            if (selectBuildingHeadPosition != -1 && selectBuildingFootPosition != -1) {
                val headName = buildingHeadNames[selectBuildingHeadPosition]
                val name = "$headName ${buildingNames.getValue(headName)[selectBuildingFootPosition]}"
                view.et_dormitory_building.setText(name)
            }
        }

        Glide.with(this).asGif().load(R.drawable.electricity_header_setting).listener(object : RequestListener<GifDrawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean) = false
            override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                resource?.apply {
                    setLoopCount(1)
                    startFromFirstFrame()
                }
                return false
            }
        }).into(iv_bg)

        selectBuildingHeadPosition = defaultSharedPreferences.getInt(SP_BUILDING_HEAD_KEY, 1)
        selectBuildingFootPosition = defaultSharedPreferences.getInt(SP_BUILDING_FOOT_KEY, 1)

        view.apply {
            et_dormitory_building.apply {
                inputType = InputType.TYPE_NULL
                refreshBuildName()

                setOnTouchListener { _, _ ->
                    selectDialog.show()
                    false
                }
            }

            et_dormitory_room.setText(defaultSharedPreferences.getString(SP_ROOM_KEY, ""))

            btn_dormitory_confirm.setOnClickListener {
                if (selectBuildingHeadPosition == -1 || et_dormitory_room.text?.length != 3) {
                    errNoticeDialog.show()
                    return@setOnClickListener
                }

                defaultSharedPreferences.editor {
                    putInt(SP_BUILDING_HEAD_KEY, selectBuildingHeadPosition)
                    putInt(SP_BUILDING_FOOT_KEY, selectBuildingFootPosition)
                    putString(SP_ROOM_KEY, et_dormitory_room.text.toString())
                }
                NavHostFragment.findNavController(this@SettingFragment).apply {
                    popBackStack()
                    popBackStack(R.id.electricity_nav_charge_fragment, true)
                    navigate(R.id.electricity_nav_charge_fragment)
                }
            }
        }

        selectDialog = Dialog(context!!).apply {
            setContentView(layoutInflater.inflate(R.layout.electricity_dialog_dormitory_select, null, false).apply {
                wp_dormitory_head.setOnItemSelectedListener { _, data, _ ->
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
            })
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            window?.let {
                val a = it.attributes
                a.width = context.dp2px(380f)
                it.attributes = a
            }
        }
    }
}