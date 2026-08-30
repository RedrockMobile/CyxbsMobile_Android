package com.cyxbs.pages.schedule.data.local.room3

import androidx.room3.Room
import platform.Foundation.NSHomeDirectory

/**
 * 创建 iOS 平台的 Schedule Room3 业务数据库。
 *
 * [path] 未传入时使用应用 Home 目录的独立业务文件，且不兼容旧 P0 probe/Settings 数据。没有可用 migration 时直接
 * 清库重建；远端保有完整日程，本地临时数据按产品约定允许丢弃。
 */
fun buildScheduleRoomDatabase(path: String = "${NSHomeDirectory()}/schedule-room3.db"): ScheduleRoomDatabase =
  Room.databaseBuilder<ScheduleRoomDatabase>(
    name = path,
  ).setDriver(bundledScheduleRoomDriver())
    .fallbackToDestructiveMigration(dropAllTables = true)
    .build()
