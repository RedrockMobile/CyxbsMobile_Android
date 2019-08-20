package com.mredrock.cyxbs.freshman.util

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.*
import com.mredrock.cyxbs.freshman.config.FIELD_MEMORANDUM_BOOK_CONTENT
import com.mredrock.cyxbs.freshman.config.FIELD_MEMORANDUM_BOOK_STATUS
import com.mredrock.cyxbs.freshman.config.TABLE_MEMORANDUM_BOOK
import com.mredrock.cyxbs.freshman.config.XML_ENROLLMENT_REQUIREMENTS
import com.mredrock.cyxbs.freshman.interfaces.ParseBean
import com.mredrock.cyxbs.freshman.util.database.FreshmanDatabaseOpenHelper

/**
 * Create by yuanbing
 * on 2019/8/9
 * 管理备忘录
 */
object MemorandumManager {
    private val database = FreshmanDatabaseOpenHelper.writableDatabase

    // 自己添加的备忘录

    fun queryStatus(name: String): Boolean {
        val sql = "SELECT $FIELD_MEMORANDUM_BOOK_STATUS FROM $TABLE_MEMORANDUM_BOOK " +
                "WHERE $FIELD_MEMORANDUM_BOOK_CONTENT='$name';"
        val cursor = database.rawQuery(sql, null)
        var status = false
        if (cursor.moveToNext())  {
            status = cursor.getInt(cursor.getColumnIndex(FIELD_MEMORANDUM_BOOK_STATUS)) == 1
        }
        cursor.close()
        return status
    }

    fun add(name: String) {
        if (exists(name)) return
        val sql = "INSERT INTO $TABLE_MEMORANDUM_BOOK VALUES('$name', $STATUS_FALSE_CUSTOM);"
        database.execSQL(sql)
    }

    fun done(name: String) {
        undo(name, STATUS_TRUE_CUSTOM)
    }

    fun undo(name: String, status: Int = STATUS_FALSE_CUSTOM) {
        val sql = "UPDATE $TABLE_MEMORANDUM_BOOK SET $FIELD_MEMORANDUM_BOOK_STATUS=$status WHERE " +
                "$FIELD_MEMORANDUM_BOOK_CONTENT='$name';"
        database.execSQL(sql)
    }

    fun remove(name: String) {
        val sql = "DELETE FROM $TABLE_MEMORANDUM_BOOK WHERE $FIELD_MEMORANDUM_BOOK_CONTENT='$name';"
        database.execSQL(sql)
    }

    fun exists(name: String = "", condition: String = ""): Boolean {
        val rex = if (condition.isBlank()) name else condition
        val sql = "SELECT * FROM $TABLE_MEMORANDUM_BOOK WHERE $FIELD_MEMORANDUM_BOOK_CONTENT LIKE '$rex';"
        val cursor = database.rawQuery(sql, null)
        val result = cursor.moveToNext()
        cursor.close()
        return result
    }

    fun getAll(): MutableList<ParseBean> {
        val sql = "SELECT * FROM $TABLE_MEMORANDUM_BOOK;"
        val cursor = database.rawQuery(sql, null)
        val result = mutableListOf<ParseBean>()
        while (cursor.moveToNext()) {
            result.add(EnrollmentRequirementsItemBean(
                    cursor.getString(cursor.getColumnIndex(FIELD_MEMORANDUM_BOOK_CONTENT)),
                    "",
                    cursor.getInt(cursor.getColumnIndex(FIELD_MEMORANDUM_BOOK_STATUS))
            ))
        }
        cursor.close()
        if (exists(condition = "%")) {
            result.add(EnrollmentRequirementsTitleBean(
                    BaseApp.context.resources.getString(R.string.freshman_add_memorandum_toolbar_title))
            )
        }
        return result
    }

    // 入学所必备的条目

    fun status(name: String): Int {
        return BaseApp.context.sharedPreferences(XML_ENROLLMENT_REQUIREMENTS).getInt(name, STATUS_FALSE_MUST)
    }

    fun undoMust(name: String) {
        doMust(name, STATUS_FALSE_MUST)
    }

    fun doMust(name: String, status: Int = STATUS_TRUE_MUST) {
        BaseApp.context.sharedPreferences(XML_ENROLLMENT_REQUIREMENTS).editor {
            putInt(name, status)
        }
    }

    fun addMust(name: String) {
        BaseApp.context.sharedPreferences(XML_ENROLLMENT_REQUIREMENTS).editor {
            putInt(name, status(name))
        }
    }
}