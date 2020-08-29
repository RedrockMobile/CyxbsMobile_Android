package com.mredrock.cyxbs.discover.map.ui.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.longToast
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.util.SubsamplingScaleImageViewShowPictureTarget
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.widget.*
import kotlinx.android.synthetic.main.map_fragment_show_picture.*
import java.io.File
import java.io.FileOutputStream


class ShowPictureFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel
    private var pictureUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_show_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)

        pictureUrl = arguments?.getString("pictureUrl", "")

        pictureUrl?.let { url ->
            val dialog = android.app.ProgressDialog(context)
            dialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL)
            dialog.setMessage(BaseApp.context.getString(R.string.map_picture_loading))
            dialog.setCancelable(true)
            ProgressInterceptor.addListener(url, object : ProgressListener {
                override fun onProgress(progress: Int) {
                }
            })
            context?.let {
                GlideApp.with(it)
                        .download(GlideUrl(url))
                        .into(SubsamplingScaleImageViewShowPictureTarget(it, map_iv_show_picture, dialog, url))
            }
            map_iv_show_picture.setOnLongClickListener {
                context?.let { it1 ->
                    MapDialog.show(it1, "保存图片", "是否保存图片到本地？", object : OnSelectListener {
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

                true
            }
        }

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
        context?.longToast("图片已保存在" + file.absolutePath)
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