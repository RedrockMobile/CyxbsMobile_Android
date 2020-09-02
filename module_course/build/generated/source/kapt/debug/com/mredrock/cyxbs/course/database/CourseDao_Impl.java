package com.mredrock.cyxbs.course.database;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.RxRoom;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mredrock.cyxbs.course.database.converter.ClassListStringConverter;
import com.mredrock.cyxbs.course.database.converter.IntListStringConverter;
import com.mredrock.cyxbs.course.network.Course;
import io.reactivex.Flowable;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class CourseDao_Impl implements CourseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Course> __insertionAdapterOfCourse;

  private final IntListStringConverter __intListStringConverter = new IntListStringConverter();

  private final ClassListStringConverter __classListStringConverter = new ClassListStringConverter();

  private final EntityDeletionOrUpdateAdapter<Course> __updateAdapterOfCourse;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllCourses;

  public CourseDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCourse = new EntityInsertionAdapter<Course>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `courses` (`courseId`,`courseNum`,`course`,`hashDay`,`hashLesson`,`beginLesson`,`day`,`lesson`,`teacher`,`classroom`,`rawWeek`,`weekModel`,`weekBegin`,`weekEnd`,`type`,`period`,`week`,`classNumber`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Course value) {
        stmt.bindLong(1, value.getCourseId());
        if (value.getCourseNum() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getCourseNum());
        }
        if (value.getCourse() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getCourse());
        }
        stmt.bindLong(4, value.getHashDay());
        stmt.bindLong(5, value.getHashLesson());
        stmt.bindLong(6, value.getBeginLesson());
        if (value.getDay() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDay());
        }
        if (value.getLesson() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getLesson());
        }
        if (value.getTeacher() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getTeacher());
        }
        if (value.getClassroom() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getClassroom());
        }
        if (value.getRawWeek() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getRawWeek());
        }
        if (value.getWeekModel() == null) {
          stmt.bindNull(12);
        } else {
          stmt.bindString(12, value.getWeekModel());
        }
        stmt.bindLong(13, value.getWeekBegin());
        stmt.bindLong(14, value.getWeekEnd());
        if (value.getType() == null) {
          stmt.bindNull(15);
        } else {
          stmt.bindString(15, value.getType());
        }
        stmt.bindLong(16, value.getPeriod());
        final String _tmp;
        _tmp = __intListStringConverter.intListToString(value.getWeek());
        if (_tmp == null) {
          stmt.bindNull(17);
        } else {
          stmt.bindString(17, _tmp);
        }
        final String _tmp_1;
        _tmp_1 = __classListStringConverter.strListToString(value.getClassNumber());
        if (_tmp_1 == null) {
          stmt.bindNull(18);
        } else {
          stmt.bindString(18, _tmp_1);
        }
      }
    };
    this.__updateAdapterOfCourse = new EntityDeletionOrUpdateAdapter<Course>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `courses` SET `courseId` = ?,`courseNum` = ?,`course` = ?,`hashDay` = ?,`hashLesson` = ?,`beginLesson` = ?,`day` = ?,`lesson` = ?,`teacher` = ?,`classroom` = ?,`rawWeek` = ?,`weekModel` = ?,`weekBegin` = ?,`weekEnd` = ?,`type` = ?,`period` = ?,`week` = ?,`classNumber` = ? WHERE `courseId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Course value) {
        stmt.bindLong(1, value.getCourseId());
        if (value.getCourseNum() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getCourseNum());
        }
        if (value.getCourse() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getCourse());
        }
        stmt.bindLong(4, value.getHashDay());
        stmt.bindLong(5, value.getHashLesson());
        stmt.bindLong(6, value.getBeginLesson());
        if (value.getDay() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDay());
        }
        if (value.getLesson() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getLesson());
        }
        if (value.getTeacher() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getTeacher());
        }
        if (value.getClassroom() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getClassroom());
        }
        if (value.getRawWeek() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getRawWeek());
        }
        if (value.getWeekModel() == null) {
          stmt.bindNull(12);
        } else {
          stmt.bindString(12, value.getWeekModel());
        }
        stmt.bindLong(13, value.getWeekBegin());
        stmt.bindLong(14, value.getWeekEnd());
        if (value.getType() == null) {
          stmt.bindNull(15);
        } else {
          stmt.bindString(15, value.getType());
        }
        stmt.bindLong(16, value.getPeriod());
        final String _tmp;
        _tmp = __intListStringConverter.intListToString(value.getWeek());
        if (_tmp == null) {
          stmt.bindNull(17);
        } else {
          stmt.bindString(17, _tmp);
        }
        final String _tmp_1;
        _tmp_1 = __classListStringConverter.strListToString(value.getClassNumber());
        if (_tmp_1 == null) {
          stmt.bindNull(18);
        } else {
          stmt.bindString(18, _tmp_1);
        }
        stmt.bindLong(19, value.getCourseId());
      }
    };
    this.__preparedStmtOfDeleteAllCourses = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM courses";
        return _query;
      }
    };
  }

  @Override
  public void insertCourses(final List<? extends Course> courses) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCourse.insert(courses);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertCourse(final Course course) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCourse.insert(course);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateCourse(final Course course) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfCourse.handle(course);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAllCourses() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllCourses.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAllCourses.release(_stmt);
    }
  }

  @Override
  public Flowable<List<Course>> queryAllCourses() {
    final String _sql = "SELECT * FROM courses";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[]{"courses"}, new Callable<List<Course>>() {
      @Override
      public List<Course> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCourseId = CursorUtil.getColumnIndexOrThrow(_cursor, "courseId");
          final int _cursorIndexOfCourseNum = CursorUtil.getColumnIndexOrThrow(_cursor, "courseNum");
          final int _cursorIndexOfCourse = CursorUtil.getColumnIndexOrThrow(_cursor, "course");
          final int _cursorIndexOfHashDay = CursorUtil.getColumnIndexOrThrow(_cursor, "hashDay");
          final int _cursorIndexOfHashLesson = CursorUtil.getColumnIndexOrThrow(_cursor, "hashLesson");
          final int _cursorIndexOfBeginLesson = CursorUtil.getColumnIndexOrThrow(_cursor, "beginLesson");
          final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
          final int _cursorIndexOfLesson = CursorUtil.getColumnIndexOrThrow(_cursor, "lesson");
          final int _cursorIndexOfTeacher = CursorUtil.getColumnIndexOrThrow(_cursor, "teacher");
          final int _cursorIndexOfClassroom = CursorUtil.getColumnIndexOrThrow(_cursor, "classroom");
          final int _cursorIndexOfRawWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "rawWeek");
          final int _cursorIndexOfWeekModel = CursorUtil.getColumnIndexOrThrow(_cursor, "weekModel");
          final int _cursorIndexOfWeekBegin = CursorUtil.getColumnIndexOrThrow(_cursor, "weekBegin");
          final int _cursorIndexOfWeekEnd = CursorUtil.getColumnIndexOrThrow(_cursor, "weekEnd");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPeriod = CursorUtil.getColumnIndexOrThrow(_cursor, "period");
          final int _cursorIndexOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "week");
          final int _cursorIndexOfClassNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "classNumber");
          final List<Course> _result = new ArrayList<Course>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Course _item;
            _item = new Course();
            final long _tmpCourseId;
            _tmpCourseId = _cursor.getLong(_cursorIndexOfCourseId);
            _item.setCourseId(_tmpCourseId);
            final String _tmpCourseNum;
            _tmpCourseNum = _cursor.getString(_cursorIndexOfCourseNum);
            _item.setCourseNum(_tmpCourseNum);
            final String _tmpCourse;
            _tmpCourse = _cursor.getString(_cursorIndexOfCourse);
            _item.setCourse(_tmpCourse);
            final int _tmpHashDay;
            _tmpHashDay = _cursor.getInt(_cursorIndexOfHashDay);
            _item.setHashDay(_tmpHashDay);
            final int _tmpHashLesson;
            _tmpHashLesson = _cursor.getInt(_cursorIndexOfHashLesson);
            _item.setHashLesson(_tmpHashLesson);
            final int _tmpBeginLesson;
            _tmpBeginLesson = _cursor.getInt(_cursorIndexOfBeginLesson);
            _item.setBeginLesson(_tmpBeginLesson);
            final String _tmpDay;
            _tmpDay = _cursor.getString(_cursorIndexOfDay);
            _item.setDay(_tmpDay);
            final String _tmpLesson;
            _tmpLesson = _cursor.getString(_cursorIndexOfLesson);
            _item.setLesson(_tmpLesson);
            final String _tmpTeacher;
            _tmpTeacher = _cursor.getString(_cursorIndexOfTeacher);
            _item.setTeacher(_tmpTeacher);
            final String _tmpClassroom;
            _tmpClassroom = _cursor.getString(_cursorIndexOfClassroom);
            _item.setClassroom(_tmpClassroom);
            final String _tmpRawWeek;
            _tmpRawWeek = _cursor.getString(_cursorIndexOfRawWeek);
            _item.setRawWeek(_tmpRawWeek);
            final String _tmpWeekModel;
            _tmpWeekModel = _cursor.getString(_cursorIndexOfWeekModel);
            _item.setWeekModel(_tmpWeekModel);
            final int _tmpWeekBegin;
            _tmpWeekBegin = _cursor.getInt(_cursorIndexOfWeekBegin);
            _item.setWeekBegin(_tmpWeekBegin);
            final int _tmpWeekEnd;
            _tmpWeekEnd = _cursor.getInt(_cursorIndexOfWeekEnd);
            _item.setWeekEnd(_tmpWeekEnd);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            _item.setType(_tmpType);
            final int _tmpPeriod;
            _tmpPeriod = _cursor.getInt(_cursorIndexOfPeriod);
            _item.setPeriod(_tmpPeriod);
            final List<Integer> _tmpWeek;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfWeek);
            _tmpWeek = __intListStringConverter.stringToIntList(_tmp);
            _item.setWeek(_tmpWeek);
            final List<String> _tmpClassNumber;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfClassNumber);
            _tmpClassNumber = __classListStringConverter.stringToStrList(_tmp_1);
            _item.setClassNumber(_tmpClassNumber);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }
}
