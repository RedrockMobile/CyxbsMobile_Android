package com.mredrock.cyxbs.mine.page.mine.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel

class IdentityActivity : BaseViewModelActivity<IdentityViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_identity)
    }
}