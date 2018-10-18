package com.mredrock.cyxbs.electricity.ui.activity

import android.os.Bundle
import android.view.Menu
import androidx.navigation.fragment.NavHostFragment
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.electricity.R
import com.mredrock.cyxbs.electricity.config.SP_BUILDING_KEY
import kotlinx.android.synthetic.main.electricity_activity_charge.*
import org.jetbrains.anko.defaultSharedPreferences

class ElectricityChargeActivity : BaseActivity() {
    override val isFragmentActivity = false
    private val navigation by lazy { NavHostFragment.findNavController(nav_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.electricity_activity_charge)
        common_toolbar.init("查电费")
        navigation.addOnNavigatedListener { _, destination ->
            if (destination.id == R.id.electricity_nav_setting_fragment) {
                common_toolbar.title = "设置寝室"
            } else {
                common_toolbar.title = "查电费"
            }
        }
        if (defaultSharedPreferences.getInt(SP_BUILDING_KEY, -1) == -1) {
            navigation.popBackStack()
            navigation.navigate(R.id.electricity_nav_setting_fragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.electricity_menu, menu)
        menu?.getItem(0)?.setOnMenuItemClickListener {
            if (navigation.currentDestination?.id != R.id.electricity_nav_setting_fragment) {
                navigation.navigate(R.id.electricity_nav_setting_fragment)
            }
            return@setOnMenuItemClickListener false
        }
        return super.onCreateOptionsMenu(menu)
    }
}
