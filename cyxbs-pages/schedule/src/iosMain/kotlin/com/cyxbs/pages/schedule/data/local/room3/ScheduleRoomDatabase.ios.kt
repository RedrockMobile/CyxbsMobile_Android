package com.cyxbs.pages.schedule.data.local.room3

import androidx.room3.Room
import platform.Foundation.NSHomeDirectory

/**
 * 创建 iOS 平台的 Schedule Room3 业务数据库。
 *
 * [path] 未传入时使用应用 Home 目录下的新 `schedule.db` 文件，避免开发期同为 version 1 的旧结构绕过
 * destructive migration 后触发 Room identity 校验失败。旧库尚未上线且不再读取；远端保有完整日程，本地临时
 * 数据按产品约定允许丢弃。
 */
fun buildScheduleRoomDatabase(path: String = "${NSHomeDirectory()}/schedule.db"): ScheduleRoomDatabase =
  Room.databaseBuilder<ScheduleRoomDatabase>(
    name = path,
  ).setDriver(bundledScheduleRoomDriver())
    .fallbackToDestructiveMigration(dropAllTables = true)
    .build()
