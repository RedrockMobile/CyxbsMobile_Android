package com.mredrock.cyxbs.update.component

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import io.reactivex.subjects.PublishSubject

/**
 * Create By Hosigus at 2020/5/3
 */
class PackageInstallsFragment : Fragment() {
    var sub: PublishSubject<Boolean>? = null
    var requesting = false

    companion object {
        const val TAG = "PackageInstallsFragment"
        const val CODE = 0x0101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestPermissions() {
        requesting = true
        val packageName = activity?.packageName
        if (packageName == null) {
            requesting = false
            return
        }
        startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.fromParts("package", packageName, null)), CODE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        requesting = false
        val sub = sub ?: return
        sub.onNext(activity?.packageManager?.canRequestPackageInstalls() ?: false)
        sub.onComplete()
    }
}
