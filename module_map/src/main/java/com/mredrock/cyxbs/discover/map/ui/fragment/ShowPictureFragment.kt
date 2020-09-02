package com.mredrock.cyxbs.discover.map.ui.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.component.MapToast
import com.mredrock.cyxbs.discover.map.ui.adapter.MyImageAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.widget.MapDialog
import com.mredrock.cyxbs.discover.map.widget.OnSelectListener
import kotlinx.android.synthetic.main.map_fragment_show_picture.*
import java.io.File
import java.io.FileOutputStream


class ShowPictureFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel
    private var picturePosition = 0
    private val imageData = mutableListOf<String>()
    private lateinit var adapter: MyImageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_show_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)

        /**
         * 获取点击第几张图片
         */
        picturePosition = arguments?.getInt("picturePosition", 0) ?: 0

        adapter = context?.let { MyImageAdapter(imageData, it) } ?: return

        /**
         * 单击图片退出
         */
        adapter.setMyOnPhotoClickListener(object : MyImageAdapter.OnPhotoClickListener {
            override fun onPhotoClick() {
                val manager = activity?.supportFragmentManager
                manager?.popBackStack()
            }
        })

        /**
         * 长按保存图片
         */
        adapter.setMyOnPhotoLongClickListener(object : MyImageAdapter.OnPhotoLongClickListener {
            override fun onPhotoLongClick(url: String) {
                MapDialog.show(context!!
                        , context!!.getString(R.string.map_show_picture_save),
                        context!!.getString(R.string.map_show_picture_content)
                        , object : OnSelectListener {
                    override fun onDeny() {
                    }

                    override fun onPositive() {

                        (context as AppCompatActivity).doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                            doAfterGranted {
                                Glide.with(BaseApp.context).asBitmap().load(url).into(object : SimpleTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        saveImage(resource)
                                    }
                                }
                                )
                            }
                        }
                    }
                })
            }

        })

        /**
         * 从ViewModel获取数据
         */
        if (viewModel.placeDetails.value?.images != null) {
            imageData.clear()
            imageData.addAll(viewModel.placeDetails.value?.images!!)
            adapter.setList(viewModel.placeDetails.value?.images!!)
            adapter.notifyDataSetChanged()
        }
        map_vp_show_picture.adapter = adapter

        /**
         * 设置当前展示的是点击的图片
         */
        map_vp_show_picture.setCurrentItem(picturePosition, false)

        /** 图片上方页数 */
        val s = "${picturePosition + 1}/${imageData.size}"
        map_tv_show_picture.text = s
        map_vp_show_picture.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                picturePosition = position
                val content = "${picturePosition + 1}/${imageData.size}"
                map_tv_show_picture.text = content
            }
        })

    }

    private fun saveImage(resource: Bitmap) {
        val file = File(BaseApp.context.externalMediaDirs[0]?.absolutePath
                + File.separator
                + "Map"
                + File.separator
                + System.currentTimeMillis()
                + ".jpg")
        val parentDir = file.parentFile
        if (parentDir.exists()) parentDir.delete()
        parentDir.mkdir()
        file.createNewFile()
        val fos = FileOutputStream(file)
        resource.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        galleryAddPic(file.path)
        context?.let { MapToast.makeText(it, "图片已保存在" + file.absolutePath, Toast.LENGTH_LONG).show() }
    }

    //更新相册
    private fun galleryAddPic(imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        BaseApp.context.sendBroadcast(mediaScanIntent)
    }

}