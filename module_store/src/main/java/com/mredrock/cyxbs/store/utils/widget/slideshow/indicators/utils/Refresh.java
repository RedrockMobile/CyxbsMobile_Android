package com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import java.lang.annotation.Retention;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;
import static com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Refresh.Condition.COEXIST;
import static com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Refresh.Condition.COVERED;
import static com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Refresh.Condition.ONLY_ONE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * .....
 *
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/29
 */
public class Refresh {
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef({COEXIST, COVERED, ONLY_ONE})
    public @interface Condition {

        /**
         * 1、将 OnRefreshListener 接口中的处理与之前 onBindViewHolder 中的处理共存，
         * 调用循序为 onBindViewHolder 先调用
         * <p/>
         * 2、实现原理为调用 notifyItemChanged(position, payload)，该方法会调用
         *  onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>)
         * <p/>
         * 3、然后会有一个数组保存 OnRefreshListener 对象，实现视图被 ViewPager2
         * 回收后仍能恢复
         * <p/>
         * <b/>WARNING：
         * 1、不建议写入延时操作。
         * 2、如果填入周期短的对象，该对象可能将无法被回收
         */
        int COEXIST = 0;

        /**
         * 1、将 OnRefreshListener 接口中的处理覆盖之前 onBindViewHolder 中的处理
         * <p/>
         * 2、实现原理为调用 notifyItemChanged(position, payload)，该方法会调用
         *  onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>)
         * <p/>
         * 3、然后会有一个数组保存 OnRefreshListener 对象，实现视图被 ViewPager2
         * 回收后仍能恢复
         * <p/>
         * <b/>WARNING：
         * 1、不建议写入延时操作。
         * 2、如果填入周期短的对象，该对象可能将无法被回收
         */
        int COVERED = 1;

        /**
         * 1、只执行一次
         * <p/>
         * 2、因为内部使用的 ViewPager2，视图是会回收重复利用的，该方法只会被调用一次，在视图被 ViewPager2
         * 回收后，将会恢复原样
         * <p/>
         * 3、实现原理为调用 notifyItemChanged(position, payload)，该方法会调用
         *  onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>)，
         * <p/>
         * <b/>WARNING：
         * 1、不建议写入延时操作。
         */
        int ONLY_ONE = 2;
    }
}
