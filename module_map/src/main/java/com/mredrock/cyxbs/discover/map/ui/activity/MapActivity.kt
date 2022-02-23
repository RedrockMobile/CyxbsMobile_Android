package com.mredrock.cyxbs.discover.map.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.COURSE_POS_TO_MAP
import com.mredrock.cyxbs.common.config.DISCOVER_MAP
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.getAbsolutePath
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.model.DataSet
import com.mredrock.cyxbs.discover.map.ui.fragment.AllPictureFragment
import com.mredrock.cyxbs.discover.map.ui.fragment.FavoriteEditFragment
import com.mredrock.cyxbs.discover.map.ui.fragment.MainFragment
import com.mredrock.cyxbs.discover.map.util.KeyboardController
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.widget.GlideProgressDialog
import com.mredrock.cyxbs.discover.map.widget.ProgressDialog
import kotlinx.android.synthetic.main.map_activity_map.*
import top.limuyang2.photolibrary.LPhotoHelper
import java.io.File

/**
 * 单activity模式，所有fragment在此activity下，能拿到同一个viewModel实例
 * Fragment不能继承BaseViewModelFragment，因为获得的viewModel是不同实例，必须：
 * ViewModelProvider(requireActivity()).get(MapViewModel::class.java)来获得实例
 */


@Route(path = DISCOVER_MAP)
class MapActivity : BaseViewModelActivity<MapViewModel>() {

    private val fragmentManager = supportFragmentManager
    private val mainFragment = MainFragment()
    private val favoriteEditFragment = FavoriteEditFragment()
    private val allPictureFragment = AllPictureFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_map)
        val openString = intent.getStringExtra(COURSE_POS_TO_MAP)
        val path = DataSet.getPath()
        /**
         * 如果有保存路径且地图存在，则不展示dialog
         */
        if (path == null || !fileIsExists(path)) {
            GlideProgressDialog.show(this, BaseApp.context.getString(R.string.map_download_title), BaseApp.context.getString(R.string.map_download_message), false)
        }

        //初始化viewModel
        viewModel.init()
        /**
         * 获取MapInfo后进行地点搜索请求
         */
        viewModel.mapInfo.observe(this, Observer {
            viewModel.getPlaceSearch(openString)
        })
        fragmentManager.beginTransaction().add(R.id.map_fl_main_fragment, mainFragment).show(mainFragment).commit()


        //控制收藏页面是否显示
        viewModel.fragmentFavoriteEditIsShowing.observe(
                this@MapActivity,
                Observer<Boolean> { t ->
                    if (t == true) {
                        val transaction = fragmentManager.beginTransaction()
                        transaction.setCustomAnimations(R.animator.map_slide_from_right, R.animator.map_slide_to_left, R.animator.map_slide_from_left, R.animator.map_slide_to_right)
                        transaction.hide(mainFragment)
                        if (!favoriteEditFragment.isAdded) {
                            transaction.add(R.id.map_fl_main_fragment, favoriteEditFragment)
                        }
                        transaction
                                .show(favoriteEditFragment)
                                .addToBackStack("favorite_edit")
                                .commit()
                    } else {
                        //隐藏键盘再返回，防止发生布局变形
                        KeyboardController.hideInputKeyboard(this, map_fl_main_fragment)
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.animator.map_slide_from_left, R.animator.map_slide_to_right, R.animator.map_slide_from_right, R.animator.map_slide_to_left)
                                .hide(favoriteEditFragment)
                                .show(mainFragment)
                                .commit()
                        fragmentManager.popBackStack()

                    }
                }
        )

        //控制全部图片页面是否显示
        viewModel.fragmentAllPictureIsShowing.observe(
                this@MapActivity,
                Observer<Boolean> { t ->
                    if (t == true) {
                        val transaction = fragmentManager.beginTransaction()
                        transaction.setCustomAnimations(R.animator.map_slide_from_right, R.animator.map_slide_to_left, R.animator.map_slide_from_left, R.animator.map_slide_to_right)
                        transaction.hide(mainFragment)
                        if (!allPictureFragment.isAdded) {
                            transaction.add(R.id.map_fl_main_fragment, allPictureFragment)
                        }
                        transaction
                                .show(allPictureFragment)
                                .addToBackStack("all_picture")
                                .commit()
                    } else {
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.animator.map_slide_from_left, R.animator.map_slide_to_right, R.animator.map_slide_from_right, R.animator.map_slide_to_left)
                                .hide(allPictureFragment)
                                .show(mainFragment)
                                .commit()
                        fragmentManager.popBackStack()

                    }
                }
        )

    }

    override fun onBackPressed() {
        if (mainFragment.childFragmentManager.backStackEntryCount != 0) {
            mainFragment.closeSearchFragment()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        ProgressDialog.hide()
        GlideProgressDialog.hide()
        super.onDestroy()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }

    //判断文件是否存在
    private fun fileIsExists(strFile: String?): Boolean {
        try {
            if (!File(strFile).exists()) {
                return false
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MapViewModel.PICTURE_SELECT && resultCode == Activity.RESULT_OK) {
            /**
             * 从图片选择框选择照片后
             */
            val pictureList = ArrayList((LPhotoHelper.getSelectedPhotos(data)).map {
                it.toString()
            })
            //上面获得的是UriString，转换下
            val pictureListPath = ArrayList<String>()
            pictureList.forEach { pictureListPath.add(Uri.parse(it).getAbsolutePath(this)) }

            /**
             * 上传图片
             * 只需把路径列表pictureList传入，context传入即可
             */
            ProgressDialog.show(this, BaseApp.context.resources.getString(R.string.map_upload_picture_running), BaseApp.context.resources.getString(R.string.map_please_a_moment_text), false)
            viewModel.uploadPicture(pictureListPath, this)
        }
    }


}
