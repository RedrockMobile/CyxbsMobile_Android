package com.cyxbs.pages.schedule.data.local.room3

import android.content.Context
import androidx.room3.Room

/**
 * 创建 Android 平台的 Schedule Room3 业务数据库。
 *
 * 数据库存放在应用私有目录。没有可用 migration 时直接清库重建；远端保有完整日程，本地临时数据按产品约定允许
 * 丢弃，因此不为未上线的开发期 schema 维护迁移。调用方应长期持有结果，并只在明确生命周期结束后调用
 * [closeScheduleRoomDatabase]。
 */
fun buildScheduleRoomDatabase(context: Context): ScheduleRoomDatabase =
  Room.databaseBuilder<ScheduleRoomDatabase>(
    context = context.applicationContext,
    name = "schedule-room3.db",
  ).setDriver(bundledScheduleRoomDriver())
    .fallbackToDestructiveMigration(dropAllTables = true)
    .build()
