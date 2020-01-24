package com.mredrock.cyxbs.course.adapters

import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel

/**
 * Created by anriku on 2018/9/9.
 * Correct by jon 2019-12-12 20:47
 * 描述:
 *   这个适配用于周数选择里面的AutoWarpView的适配器
 */
class WeekSelectRecAdapter(mActivity: FragmentActivity) : RedRockAutoWarpView.Adapter() {

    private val mEditAffairViewModel: EditAffairViewModel = ViewModelProviders.of(mActivity).get(EditAffairViewModel::class.java)

    private val mWeeks = mEditAffairViewModel.weekArray

    override fun getItemId(): Int {
        return R.layout.course_week_select_rec_item
    }

    override fun getItemCount(): Int {
        return mWeeks.size
    }

    override fun initItem(item: View, position: Int) {
        val checkBox = item.findViewById<CheckBox>(R.id.cb)
        checkBox.tag = position
        checkBox.text = mWeeks[position]
        if (mEditAffairViewModel.isSelectedWeekViews.indexOfKey(position) >= 0) {
            checkBox.isChecked = true
            //替换掉上次打开TimeSelectDialogFragment时存储的对应的CheckBox对象
            mEditAffairViewModel.isSelectedWeekViews.put(position, checkBox)
        }

        checkBox.apply {
            setOnCheckedChangeListener { cb, isChecked ->
                val isSelectedWeekViews = mEditAffairViewModel.isSelectedWeekViews
                if (isChecked) {
                    if (position == 0) {
                        repeat(isSelectedWeekViews.size()) {
                            // 这里要注意的是在我们进行isChecked设定为false的操作后，整个存储结构就发生变化了。
                            // 比如说：现在SparseArray中存了2个值，它们的index分别是0，1。0index调用了remove
                            // p操作后现在SarseArray中只有1个值了，这时剩下这个值的index就变为0了，如果这时
                            // 你要获取index为1的值就是null了，再为null设置false就会崩掉。
                            isSelectedWeekViews.valueAt(0).isChecked = false
                        }
                    } else {
                        if (isSelectedWeekViews.valueAt(0) != null && isSelectedWeekViews.valueAt(0) is CheckBox
                                && isSelectedWeekViews.keyAt(0) == 0) {
                            isSelectedWeekViews.valueAt(0).isChecked = false
                        }
                    }
                    isSelectedWeekViews.put(position, cb as CheckBox)
                } else {
                    isSelectedWeekViews.remove(position)
                }
            }
        }
    }
}