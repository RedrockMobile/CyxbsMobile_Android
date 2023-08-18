package com.mredrock.cyxbs.ufield.gyd

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.config.view.JToolbar
import com.mredrock.cyxbs.ufield.R

class CreateActivity : BaseViewModelActivity<CreateViewModel>() {

    private val toolbar by R.id.create_toolbar.view<Toolbar>()
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createactivity)
        initToolbar()
    }


    private fun initToolbar(){
        toolbar.title=""
        setSupportActionBar(toolbar)
//      supportActionBar?.setHomeAsUpIndicator(R.drawable.toolbar_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutParams = toolbar.getChildAt(0).layoutParams as Toolbar.LayoutParams
        layoutParams.setMargins(27, 0, 0, 0)
        toolbar.getChildAt(0).layoutParams = layoutParams
    }

}