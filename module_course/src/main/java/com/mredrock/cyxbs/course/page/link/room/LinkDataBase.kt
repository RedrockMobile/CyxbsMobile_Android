package com.mredrock.cyxbs.course.page.link.room

import androidx.room.*
import com.mredrock.cyxbs.course.page.link.bean.LinkStudent
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import io.reactivex.rxjava3.core.Observable
import java.io.Serializable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/23 14:40
 */
@Database(entities = [LinkStuEntity::class], version = 1)
abstract class LinkDataBase : RoomDatabase() {
  abstract fun getLinkStuDao(): LinkStuDao

  companion object {
    val INSTANCE by lazy {
      Room.databaseBuilder(
        appContext,
        LinkDataBase::class.java,
        "course_link_student_db"
      ).fallbackToDestructiveMigration().build()
    }
  }
}

@Entity(tableName = "link_stu")
data class LinkStuEntity(
  @PrimaryKey
  val selfNum: String, // 自己的学号
  val linkNum: String, // 关联人的学号
  val linkMajor: String, // 关联人的专业
  val linkName: String, // 关联人的姓名
  val isShowLink: Boolean, // 是否显示
  val isBoy: Boolean // 是否是男生
) : Serializable {
  constructor(
    linkStu: LinkStudent,
    isShowLink: Boolean,
    isBoy: Boolean
  ) : this(linkStu.selfNum, linkStu.linkNum, linkStu.major, linkStu.name, isShowLink, isBoy)
  
  fun isNull(): Boolean = linkNum.isBlank() || selfNum.isBlank()
  fun isNotNull(): Boolean = !isNull()
  companion object {
    val NULL = LinkStuEntity("", "", "", "", isShowLink = false, isBoy = true)
  }
}

@Dao
interface LinkStuDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertLinkStu(link: LinkStuEntity)
  
  @Update
  fun updateLinkStu(link: LinkStuEntity)

  @Query("SELECT * FROM link_stu WHERE selfNum = :selfNum")
  fun getLinkStu(selfNum: String): LinkStuEntity?
  
  @Query("SELECT * FROM link_stu WHERE selfNum = :selfNum")
  fun observeLinkStu(selfNum: String): Observable<LinkStuEntity>
}