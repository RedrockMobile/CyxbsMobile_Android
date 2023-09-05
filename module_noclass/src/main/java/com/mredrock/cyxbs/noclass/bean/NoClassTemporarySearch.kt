package com.mredrock.cyxbs.noclass.bean


/**
 * 没课约临时分组搜索结果
 */
data class NoClassTemporarySearch(
    val `class`: Cls,
    val group: NoClassGroup,
    val students: List<Student>,
    val types: List<String>
)

const val STUDENT_TYPE = "学生"
const val CLASS_TYPE = "班级"
const val GROUP_TYPE = "分组"

interface NoClassItem {
    val id: String
}

interface Group : NoClassItem {
    val members: List<Student>
}

data class Cls(
    override val id: String,
    override val members: List<Student>,
    val name: String,
) : Group

