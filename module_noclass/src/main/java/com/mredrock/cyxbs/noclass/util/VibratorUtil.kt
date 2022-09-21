package com.mredrock.cyxbs.noclass.util

import android.content.Context
import android.os.*

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.util
 * @ClassName:      VibratorUtil
 * @Author:         Yan
 * @CreateDate:     2022年08月16日 16:27:00
 * @UpdateRemark:  更新说明：
 * @Version:        1.0
 * @Description:    震动类
 */

internal object VibratorUtil {

    /**
     * 根据不同的 Android 版本调用不同的震动方法
     */
    fun start(context: Context, milliseconds: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrator = context.getSystemService(VibratorManager::class.java)
            vibrator.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        milliseconds,
                        VibrationEffect.EFFECT_TICK
                    )
                )
            )
        } else {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        milliseconds,
                        VibrationEffect.EFFECT_TICK
                    )
                )
            } else {
                vibrator.vibrate(milliseconds)
            }
        }
    }
}