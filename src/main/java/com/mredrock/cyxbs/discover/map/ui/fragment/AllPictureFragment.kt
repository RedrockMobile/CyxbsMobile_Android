package com.mredrock.cyxbs.discover.map.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.ui.adapter.AllPictureRvAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.widget.MapDialog
import com.mredrock.cyxbs.discover.map.widget.OnSelectListener
import com.mredrock.cyxbs.discover.map.widget.ProgressDialog
import kotlinx.android.synthetic.main.map_fragment_all_picture.*


class AllPictureFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var allPictureAdapter: AllPictureRvAdapter
    private val imageData = mutableListOf<String>()
    private val manager: FragmentManager?
        get() = activity?.supportFragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_all_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        map_tv_all_picture.alpha = 0f
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        map_rv_all_picture.layoutManager = staggeredGridLayoutManager
        allPictureAdapter = AllPictureRvAdapter(requireContext(), mutableListOf())

        allPictureAdapter.setOnItemClickListener(object : AllPictureRvAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val transaction = manager?.beginTransaction()?.setCustomAnimations(
                        R.animator.map_slide_from_right,
                        R.animator.map_slide_to_left,
                        R.animator.map_slide_from_left,
                        R.animator.map_slide_to_right)
                transaction?.add(R.id.map_root_all_picture, ShowPictureFragment::class.java, bundleOf(Pair("pictureUrl", imageData[position])))
                transaction?.addToBackStack("showPicture")?.commit()
            }

        })

        map_rv_all_picture.adapter = allPictureAdapter

        map_iv_all_picture_back.setOnClickListener {
            viewModel.fragmentAllPictureIsShowing.value = false
        }


        map_rv_all_picture.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, state: Int) {
                // 如果停止滑动
                if (state == SCROLL_STATE_IDLE) {
                    // 获取布局管理器
                    val layout = recyclerView.layoutManager as StaggeredGridLayoutManager
                    // 用来记录lastItem的position
                    // 由于瀑布流有多个列 所以此处用数组存储
                    val positions = IntArray(4)
                    // 获取lastItem的positions
                    /**
                     * 其他布局管理器可使用同样方式获取
                     */
                    layout.findLastVisibleItemPositions(positions)
                    for (i in positions.indices) {
                        /**
                         * 判断lastItem的底边到recyclerView顶部的距离
                         * 是否小于recyclerView的高度
                         * 如果小于或等于 说明滚动到了底部
                         */
                        if (layout.findViewByPosition(positions[i])?.bottom ?: 0 <= recyclerView.height) {
                            /**
                             * 此处实现业务逻辑
                             */
                            map_tv_all_picture.animate().alpha(1f).duration = 500
                            return
                        } else {
                            map_tv_all_picture.animate().alpha(0f).duration = 500
                        }
                    }
                }
                super.onScrollStateChanged(recyclerView, state)
            }
        })

        map_tv_all_picture_share.setOnClickListener {
            context?.doIfLogin("分享") {
                doPermissionAction(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA) {
                    reason = BaseApp.context.resources.getString(R.string.map_require_permission_tips)
                    doAfterGranted {
                        uploadPicture()
                    }
                    doAfterRefused {
                        uploadPicture()
                    }
                }
            }

        }

    }

    override fun onResume() {
        super.onResume()
        if (viewModel.placeDetails.value?.images != null) {
            imageData.clear()
            imageData.addAll(viewModel.placeDetails.value?.images!!)
            allPictureAdapter.setList(viewModel.placeDetails.value?.images!!)
            allPictureAdapter.notifyDataSetChanged()
        }
    }


    /**
     * 上传图片
     */

    fun uploadPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (BaseApp.context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    BaseApp.context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    BaseApp.context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                CyxbsToast.makeText(BaseApp.context, BaseApp.context.resources.getString(R.string.map_no_permission_store), Toast.LENGTH_LONG).show()
                return
            }
        }
        MapDialog.show(requireContext(), BaseApp.context.getString(R.string.map_share_picture_title), BaseApp.context.resources.getString(R.string.map_share_picture), object : OnSelectListener {
            override fun onDeny() {
            }

            override fun onPositive() {
                selectPic()
            }
        })
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            val selectedImage = data!!.data
            val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? =
                    selectedImage?.let { requireContext().contentResolver.query(it, filePathColumn, null, null, null) }
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            val imgPath = columnIndex?.let { cursor.getString(it) }
            cursor?.close()
            /**
             * 上传图片
             */
            ProgressDialog.show(requireContext(), BaseApp.context.resources.getString(R.string.map_upload_picture_running), BaseApp.context.resources.getString(R.string.map_please_a_moment_text), false)
            viewModel.uploadPicture(imgPath, requireContext())
        }
    }

    private fun selectPic() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, 11)
    }

}