package com.mredrock.cyxbs.noclass.bean


/**
 * 没课约临时分组搜索结果
 */
data class NoClassTemporarySearch(
    val `data`: Data,
    val info: String,
    val status: Int
) {
    data class Data(
        val cls: List<Cls>,
        val group: List<GroupDetail>,
        val isExist: Boolean,
        val students: List<Student>,
        val type: String   // 1:学生  2:班级  3:分组
    )
}

const val STUDENT = "学生"
const val CLASS = "班级"
const val GROUP = "分组"

interface NoClassItem{
    val id: String
    val name: String
}


interface Group : NoClassItem{
    val members: List<Student>
}

data class Cls(
    override val id: String,
    override val members: List<Student>,
    override val name: String,
) : Group

data class GroupDetail(
    override val id: String,
    override val members: List<Student>,
    override val name: String,
    val isTop: Boolean,
) : Group