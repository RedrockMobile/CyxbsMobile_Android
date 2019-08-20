package com.mredrock.cyxbs.freshman.util.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.freshman.config.DATABASE_FRESHMAN
import com.mredrock.cyxbs.freshman.config.FIELD_MEMORANDUM_BOOK_CONTENT
import com.mredrock.cyxbs.freshman.config.FIELD_MEMORANDUM_BOOK_STATUS
import com.mredrock.cyxbs.freshman.config.TABLE_MEMORANDUM_BOOK

/**
 * Create by yuanbing
 * on 2019/8/9
 */
object FreshmanDatabaseOpenHelper : SQLiteOpenHelper(
        BaseApp.context,
        DATABASE_FRESHMAN,
        null,
        1
) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val sql = "CREATE TABLE $TABLE_MEMORANDUM_BOOK($FIELD_MEMORANDUM_BOOK_CONTENT TEXT NOT NULL, " +
                "$FIELD_MEMORANDUM_BOOK_STATUS INTEGER NOT NULL DEFAULT 0);"
        p0?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {  }
}