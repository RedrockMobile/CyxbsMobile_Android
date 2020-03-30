package com.mredrock.cyxbs.discover.pages.morefunction

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.utils.MoreFunctionProvider

class MoreFunctionSettingBindable : Bindable {
    override fun onBindViewHolder(holder: MoreFunctionRvAdapter.MoreFunctionViewHolder, position: Int, adapter: MoreFunctionRvAdapter) {
        if (!adapter.getChosenList().contains(position)) {
            if (adapter.getChosenList().size > 2) {
                CyxbsToast.makeText(holder.itemView.context, "只能选择三个哦", Toast.LENGTH_SHORT).show()
            } else {
                adapter.getChosenList().add(position)
                adapter.notifyItemChanged(position)
            }
        } else {
            adapter.getChosenList().remove(position)
            adapter.notifyItemChanged(position)
        }
    }

    override fun setMoreFunctionIcon(imageView: ImageView) {
        imageView.setImageResource(R.drawable.discover_ic_settings)
    }

    override fun settingClicked(adapter: MoreFunctionRvAdapter, context: Context, imageView: ImageView):Boolean {
        if (adapter.getChosenList().size == 3) {
            MoreFunctionProvider.saveHomePageFunctionsToSp(adapter.getChosenList())
            adapter.resetChosenList()
            setMoreFunctionIcon(imageView)
            CyxbsToast.makeText(context, "更新成功", Toast.LENGTH_SHORT).show()
            return true
        } else {
            CyxbsToast.makeText(context, "要选择三个哦", Toast.LENGTH_SHORT).show()
            return false
        }
    }


}