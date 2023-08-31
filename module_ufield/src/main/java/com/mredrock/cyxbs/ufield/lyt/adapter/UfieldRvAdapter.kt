package com.mredrock.cyxbs.ufield.lyt.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.bean.ItemActivityBean
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 *  description :用于活动布告栏的Rv adapter
 *  author : lytMoon
 *  date : 2023/8/20 20:40
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
class UfieldRvAdapter :
    ListAdapter<ItemActivityBean.ItemAll, UfieldRvAdapter.RvAllActViewHolder>((RvAllDiffCallback())) {


    /**
     * 点击活动的回调
     */
    private var mActivityClick: ((Int) -> Unit)? = null

    fun setOnActivityClick(listener: (Int) -> Unit) {
        mActivityClick = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvAllActViewHolder {
        return RvAllActViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.ufield_item_rv_all, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RvAllActViewHolder, position: Int) {
        val itemData = getItem(position)

        holder.bind(itemData)
    }


    inner class RvAllActViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actPic: ImageView = itemView.findViewById(R.id.uField_activity_pic)
        private val actName: TextView =
            itemView.findViewById<TextView?>(R.id.uField_activity_name).apply {
//            isSelected=true
            }
        private val actIsGoing: ImageView = itemView.findViewById(R.id.uField_activity_isGoing)
        private val actType: TextView = itemView.findViewById(R.id.uField_activity_type)
        private val actTime: TextView = itemView.findViewById(R.id.uField_activity_time)

        init {
            itemView.setOnClickListener {
                mActivityClick?.invoke(absoluteAdapterPosition)
            }
        }

        /**
         * 进行视图的绑定
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(itemData: ItemActivityBean.ItemAll) {
            actName.text = itemData.activity_title
            actType.text = itemData.activity_type
            actTime.text = timeFormat(itemData.activity_start_at)
            actPic.setImageFromUrl(itemData.activity_cover_url)

            when (itemData.ended) {
                false -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_on)
                else -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_off)
            }


            when (itemData.activity_type) {
                "culture" -> actType.text = "文娱活动"
                "sports" -> actType.text = "体育活动"
                else -> actType.text = "教育活动"
            }
            //   loadAndCropImageWithCenterScale(itemData.activity_cover_url,16f,16f,0f,0f,actPic)


//            /**
//             * 满足中心比例的缩放
//             */
//            Glide.with(itemView.context)
//                .load(itemData.activity_cover_url)
//                .transform(CenterCrop(), RoundedCorners(50))//设置圆角
//                .centerCrop() //中心比例的缩放（如果效果不稳定请删除）
//                .into(actPic)

        }


        /**
         * 加工时间戳,把时间戳转化为“年.月.日”格式
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun timeFormat(time: Long): String {
            return Instant
                .ofEpochSecond(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
        }


        // 加载并设置圆角切割和中心缩放的图像
        fun loadAndCropImageWithCenterScale(
            url: String,
            topLeftRadius: Float,
            topRightRadius: Float,
            bottomRightRadius: Float,
            bottomLeftRadius: Float,
            imageView: ImageView
        ) {
            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)

            Glide.with(imageView)
                .asBitmap()
                .load(url)
                .apply(requestOptions)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val result = cropImageWithRadius(
                            resource,
                            topLeftRadius,
                            topRightRadius,
                            bottomRightRadius,
                            bottomLeftRadius
                        )
                        imageView.setImageBitmap(result)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }

        // 切割图像并设置圆角
        private fun cropImageWithRadius(
            source: Bitmap,
            topLeftRadius: Float,
            topRightRadius: Float,
            bottomRightRadius: Float,
            bottomLeftRadius: Float
        ): Bitmap {
            val paint = Paint().apply {
                isAntiAlias = true
                shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }

            val path = Path()
            val rect = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
            path.addRoundRect(
                rect, floatArrayOf(
                    topLeftRadius, topLeftRadius, // 左上角
                    topRightRadius, topRightRadius, // 右上角
                    bottomRightRadius, bottomRightRadius, // 右下角
                    bottomLeftRadius, bottomLeftRadius // 左下角
                ), Path.Direction.CW
            )

            val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            canvas.drawPath(path, paint)

            return result
        }


    }


    class RvAllDiffCallback : DiffUtil.ItemCallback<ItemActivityBean.ItemAll>() {
        override fun areItemsTheSame(
            oldItem: ItemActivityBean.ItemAll,
            newItem: ItemActivityBean.ItemAll
        ): Boolean {
            return oldItem == newItem
        }

        /**
         * 通过数据类中的一个特征值来比较
         */
        override fun areContentsTheSame(
            oldItem: ItemActivityBean.ItemAll,
            newItem: ItemActivityBean.ItemAll
        ): Boolean {
            return oldItem.activity_id == newItem.activity_id
        }

    }


}