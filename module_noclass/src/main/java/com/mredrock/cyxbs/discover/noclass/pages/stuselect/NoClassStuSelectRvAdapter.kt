package com.mredrock.cyxbs.discover.noclass.pages.stuselect

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.noclass.R
import com.mredrock.cyxbs.discover.noclass.network.Student
import kotlinx.android.synthetic.main.noclass_item_rv_select.view.*
import com.mredrock.cyxbs.common.utils.extensions.*


/**
 * Created by zxzhu
 *   2018/9/13.
 *   enjoy it !!
 */
class NoClassStuSelectRvAdapter(private val mStuList: List<Student>) : RecyclerView.Adapter<NoClassStuSelectRvAdapter.StuSelectHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StuSelectHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_rv_select, parent, false)
        return StuSelectHolder(view)
    }

    override fun getItemCount() = mStuList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StuSelectHolder, position: Int) {
        holder.itemView.noclass_item_select_name.text = mStuList[position].name
        holder.itemView.noclass_item_select_class.text = mStuList[position].major + " " + mStuList[position].stunum
        holder.itemView.setSingleOnClickListener {
            val intent = Intent()
            val bundle = Bundle()
            bundle.putSerializable("stu", mStuList[position])
            intent.putExtras(bundle)
            (holder.itemView.context as AppCompatActivity).setResult(Activity.RESULT_OK, intent)
            (holder.itemView.context as AppCompatActivity).finish()
        }
    }


    class StuSelectHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}