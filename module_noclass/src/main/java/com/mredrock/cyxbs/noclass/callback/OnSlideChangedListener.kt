package com.mredrock.cyxbs.noclass.callback

import com.mredrock.cyxbs.noclass.widget.SlideMenuLayout

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.interface
 * @ClassName:      OnSlideChangedListener
 * @Author:         Yan
 * @CreateDate:     2022年08月16日 20:01:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */

interface OnSlideChangedListener {

    /**
     * 侧滑菜单状态变化
     *
     * @param slideMenu        侧滑菜单[SlideMenuLayout]
     * @param isLeftSlideOpen  左滑菜单是否打开
     * @param isRightSlideOpen 右滑菜单是否打开
     */
    fun onSlideStateChanged(
        slideMenu: SlideMenuLayout,
        isLeftSlideOpen: Boolean,
        isRightSlideOpen: Boolean,
    )

    /**
     * 右侧状态改变
     * 只有在滑动状态为 [SLIDE_MODE_RIGHT]或[SLIDE_MODE_LEFT_RIGHT] 时会被触发
     *
     * @param percent 0-1 表示右滑比例
     */
    fun onSlideRightChanged(percent : Float)

    /**
     * 左侧状态改变
     * 只有在滑动状态为 [SLIDE_MODE_LEFT]或[SLIDE_MODE_LEFT_RIGHT] 时会被触发
     *
     * @param percent 0-1 表示左划比例
     */
    fun onSlideLeftChanged(percent : Float)

}