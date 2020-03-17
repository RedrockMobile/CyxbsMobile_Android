package com.mredrock.cyxbs.common.utils.extensions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat

/**
 * @author  Jon
 * @date  2020/3/18 2:03
 * description：
 */

fun Activity.startActivityTransition(intent: Intent, bundle: Bundle? = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()){
    startActivity(intent, bundle)
}