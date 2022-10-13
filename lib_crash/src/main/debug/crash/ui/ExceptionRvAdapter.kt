package crash.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/8
 * @Description:
 */
class ExceptionRvAdapter(val data: List<String>) :
    RecyclerView.Adapter<ExceptionRvAdapter.InnerViewHolder>() {
    inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView_Rv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        return InnerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.crash_item_rv, parent, false)
        )
    }
    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        if (position == 10) throw RuntimeException("RV的OnBindView异常")
        else holder.textView.text = data[position]
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}