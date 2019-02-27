package com.mredrock.cyxbs.discover.electricity.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_ELECTRICITY
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.electricity.R
import com.mredrock.cyxbs.discover.electricity.config.SP_BUILDING_KEY
import kotlinx.android.synthetic.main.electricity_activity_charge.*
import org.jetbrains.anko.defaultSharedPreferences

@Route(path = DISCOVER_ELECTRICITY)
class ElectricityChargeActivity : BaseActivity() {
    override val isFragmentActivity = false
    private val navigation by lazy { NavHostFragment.findNavController(nav_fragment) }
    private var menuI: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.electricity_activity_charge)
        common_toolbar.init("查电费")
        navigation.addOnNavigatedListener { _, destination ->
            if (destination.id == R.id.electricity_nav_setting_fragment) {
                common_toolbar.title = "设置寝室"
                menuI?.isVisible = false
            } else {
                common_toolbar.title = "查电费"
                menuI?.isVisible = true
            }
        }
        if (defaultSharedPreferences.getInt(SP_BUILDING_KEY, -1) == -1) {
            navigation.popBackStack()
            navigation.navigate(R.id.electricity_nav_setting_fragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.electricity_menu, menu)
        menuI = menu?.getItem(0)
        menuI?.setOnMenuItemClickListener {
            if (navigation.currentDestination?.id != R.id.electricity_nav_setting_fragment) {
                navigation.navigate(R.id.electricity_nav_setting_fragment)
            }
            return@setOnMenuItemClickListener false
        }
        menuI?.isVisible = navigation.currentDestination?.id == R.id.electricity_nav_charge_fragment
        return super.onCreateOptionsMenu(menu)
    }
}
