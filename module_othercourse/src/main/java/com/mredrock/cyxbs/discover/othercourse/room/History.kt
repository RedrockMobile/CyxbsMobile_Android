package com.mredrock.cyxbs.discover.othercourse.room

import androidx.room.Entity
import androidx.room.PrimaryKey

const val STUDENT_TYPE = 0
const val TEACHER_TYPE = 1

@Entity
data class History(
        var info: String,
        var type: Int = STUDENT_TYPE,
        var verify: String = "",//记录的标识，对于学生而言是学号，对于老师而言是名字
        @PrimaryKey(autoGenerate = true) var historyId: Int = 0
)