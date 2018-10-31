package com.mredrock.cyxbs.discover.electricity.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.electricity.R
import com.mredrock.cyxbs.discover.electricity.config.SP_BUILDING_KEY
import com.mredrock.cyxbs.discover.electricity.config.SP_ROOM_KEY
import com.mredrock.cyxbs.discover.electricity.ui.adapter.DormitoryAdapter
import kotlinx.android.synthetic.main.electricity_dialog_dormitory_notice.view.*
import kotlinx.android.synthetic.main.electricity_fragment_setting.view.*
import org.jetbrains.anko.support.v4.defaultSharedPreferences
import org.jetbrains.anko.support.v4.dip

/**
 * Author: Hosigus
 * Date: 2018/9/13 16:33
 * Description: com.mredrock.cyxbs.electricity.ui.fragment
 */

class SettingFragment : BaseFragment() {

    private lateinit var noticeDialog: Dialog

    private lateinit var bottomDialog: BottomSheetDialog

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var buildingNames: Array<String>

    private var selectBuildingPosition = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.electricity_fragment_setting, container, false)
                    ?: super.onCreateView(inflater, container, savedInstanceState)

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticeDialog = AlertDialog.Builder(context!!)
                .setView(layoutInflater.inflate(R.layout.electricity_dialog_dormitory_notice, null).apply {
                    btn_confirm.setOnClickListener {
                        noticeDialog.dismiss()
                    }
                })
                .create()
        noticeDialog.window.setBackgroundDrawableResource(R.drawable.electricity_shape_round)

        selectBuildingPosition = defaultSharedPreferences.getInt(SP_BUILDING_KEY, -1)
        buildingNames = resources.getStringArray(R.array.electricity_building_name)

        val rv = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = DormitoryAdapter(buildingNames) {
                view.findViewById<AppCompatEditText>(R.id.et_dormitory_building).setText(buildingNames[it])
                selectBuildingPosition = it
                bottomDialog.dismiss()
            }
            itemAnimator = DefaultItemAnimator()
        }

        bottomDialog = BottomSheetDialog(context!!).apply {
            setContentView(rv)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(rv.parent as View)
        bottomSheetBehavior.peekHeight = dip(250)

        view.et_dormitory_building.apply {
            inputType = InputType.TYPE_NULL

            setOnTouchListener { _, _ ->
                bottomDialog.show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                false
            }

            if (selectBuildingPosition != -1) {
                setText(buildingNames[selectBuildingPosition])
            }
        }

        view.et_dormitory_room.setText(defaultSharedPreferences.getString(SP_ROOM_KEY, ""))

        view.btn_dormitory_confirm.setOnClickListener {
            if (selectBuildingPosition != -1 && view.et_dormitory_room.text.length == 3) {
                defaultSharedPreferences.editor {
                    putInt(SP_BUILDING_KEY, selectBuildingPosition)
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