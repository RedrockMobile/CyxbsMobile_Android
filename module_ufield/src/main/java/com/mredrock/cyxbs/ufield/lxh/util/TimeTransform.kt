package com.mredrock.cyxbs.ufield.lxh.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
fun formatNumberToTime(number: Long): String {
    return Instant
        .ofEpochSecond(number)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
}