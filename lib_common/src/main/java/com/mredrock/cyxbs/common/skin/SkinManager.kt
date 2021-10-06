package com.mredrock.cyxbs.common.skin

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.text.TextUtils
import androidx.core.content.res.ResourcesCompat
import com.mredrock.cyxbs.common.BaseApp
import java.io.File
import java.util.*


object SkinManager {

    /**Resource是获取的关键内容，包含了皮肤包内的所有信息 */
    private var mSkinResources: Resources? = null
    private var context: Context? = null
    private var skinPackageName: String? = null
    private var isExternalSkin = false
    private val mListeners: MutableList<SkinUpdateListener> =
            ArrayList()
    private val KEY = "skin_path"

    /**判断context是否为空 */
    private fun judge() {
        checkNotNull(context) { "context is null" }
    }

    /**Android12将废弃asyncTask,因为有协程 */
    @SuppressLint("StaticFieldLeak")
    internal class LoadTask :
            AsyncTask<String?, Void?, Resources?>() {


        override fun onPostExecute(resources: Resources?) {
            super.onPostExecute(resources)
            mSkinResources = resources
            if (mSkinResources != null) {
                isExternalSkin = true
                notifySkinUpdate()
            }
        }

        override fun doInBackground(vararg paths: String?): Resources? {
            try {
                if (paths.size == 1) {
                    val skinPkgPath = paths[0]
                    val file = File(skinPkgPath)
                    if (!file.exists()) {
                        return null
                    }
                    val mPm = context!!.packageManager
                    val mInfo =
                            mPm.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES)
                    skinPackageName = mInfo.packageName
                    val assetManager = AssetManager::class.java.newInstance()
                    val addAssetPath = assetManager.javaClass.getMethod(
                            "addAssetPath",
                            String::class.java
                    )
                    addAssetPath.invoke(assetManager, skinPkgPath)
                    val superRes = context!!.resources

                    /**resource构造方法已过时，未找到替换方法 */
                    val skinResource = Resources(
                            assetManager, superRes
                            .displayMetrics, superRes.configuration
                    )
                    if (skinPkgPath != null) {
                        saveSkinPath(skinPkgPath)
                    }
                    return skinResource
                }
            } catch (e: Exception) {
                return null
            }
            return null
        }
    }

    /**监听皮肤更新 */
    interface SkinUpdateListener {
        fun onSkinUpdate()
    }


    private fun notifySkinUpdate() {
        for (listener in mListeners) {
            listener.onSkinUpdate()
        }
    }

    /**
     * API
     */
    fun addSkinUpdateListener(listener: SkinUpdateListener?) {
        if (listener == null) return
        judge()
        mListeners.add(listener)
    }

    val skinPath: String?
        get() {
            judge()
            val skinPath = SPUtil.get(context, KEY, "") as String
            return if (TextUtils.isEmpty(skinPath)) null else skinPath
        }

    fun saveSkinPath(path: String) {
        judge()
        SPUtil.put(context, KEY, path)
    }

    fun isExternalSkin(): Boolean {
        judge()
        return isExternalSkin
    }

    fun init(context: Context) {
        /** 这里直接 = context就行，不用context.什么什么的那个 */
        this.context = context
        val skinPath = SPUtil[context, KEY, ""] as String
        isExternalSkin = !TextUtils.isEmpty(skinPath)
        loadSkin(skinPath)
    }

    fun loadSkin(path: String?) {
        judge()
        if (path == null) return
        LoadTask().execute(path)
    }

    fun getColor(resName: String?, resId: Int): Int {
        judge()
        val originColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context!!.resources.getColor(resId,null)
        } else {
            context!!.resources.getColor(resId)
        }
        val isDarkMode =
                BaseApp.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (mSkinResources == null || !isExternalSkin || isDarkMode) {
            return originColor
        }
        val newResId = mSkinResources!!.getIdentifier(resName, "color", skinPackageName)
        val newColor: Int
        newColor = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSkinResources!!.getColor(newResId,null)
            } else {
                mSkinResources!!.getColor(newResId)
            }
        } catch (e: NotFoundException) {
            return originColor
        }
        return newColor
    }

    fun getDrawable(resName: String?, resId: Int): Drawable? {
        judge()
        val originDrawable = context?.resources?.let { ResourcesCompat.getDrawable(it,resId,null) }
        val isDarkMode =
                BaseApp.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (mSkinResources == null || !isExternalSkin || isDarkMode) {
            return originDrawable
        }
        val newResId = mSkinResources!!.getIdentifier(resName, "drawable", skinPackageName)
        val newDrawable: Drawable?
        newDrawable = try {
            ResourcesCompat.getDrawable(mSkinResources!!,newResId,null)
        } catch (e: Exception) {
            return originDrawable
        }
        return newDrawable
    }

    fun restoreDefaultTheme() {
        judge()
        SPUtil.put(context, KEY, "")
        isExternalSkin = false
        mSkinResources = null
        notifySkinUpdate()
    }

}