package com.mredrock.cyxbs.discover.utils


/**
 * @author zixuan
 * 2019/11/20
 *
 */
/*
class BannerAdapter(private val context: Context, private val viewPager: ViewPager2) :
    Adapter<RecyclerView.ViewHolder>() {
    var urlList = ArrayList<RollerViewInfo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.discover_viewpager_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return viewPager.currentItem + 2
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val url = if (urlList.size != 0) urlList[position % urlList.size] else null
        Glide.with(context)
            .load(url?.picture_url)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.discover_ic_cyxbsv6)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(context.dip(8))))
            )
            .into(holder.itemView.iv_viewpager_item)
        val targetUrl = url?.picture_goto_url
        targetUrl ?: return
        if (targetUrl.startsWith("http")) {
            holder.itemView.setOnSingleClickListener {
                RollerViewActivity.startRollerViewActivity(url, holder.itemView.context)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}*/
