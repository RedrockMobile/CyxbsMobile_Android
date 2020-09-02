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
import com.mredrock.cyxbs.course.database.converter.DateListStringConverter;
import com.mredrock.cyxbs.course.network.Affair;
import io.reactivex.Flowable;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AffairDao_Impl implements AffairDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Affair> __insertionAdapterOfAffair;

  private final DateListStringConverter __dateListStringConverter = new DateListStringConverter();

  private final EntityDeletionOrUpdateAdapter<Affair> __updateAdapterOfAffair;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllAffairs;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAffairById;

  public AffairDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAffair = new EntityInsertionAdapter<Affair>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `affairs` (`id`,`time`,`title`,`content`,`updatedTime`,`date`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Affair value) {
        stmt.bindLong(1, value.getId());
        if (value.getTime() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTime());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTitle());
        }
        if (value.getContent() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getContent());
        }
        if (value.getUpdatedTime() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUpdatedTime());
        }
        final String _tmp;
        _tmp = __dateListStringConverter.dateListToString(value.getDate());
        if (_tmp == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, _tmp);
        }
      }
    };
    this.__updateAdapterOfAffair = new EntityDeletionOrUpdateAdapter<Affair>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `affairs` SET `id` = ?,`time` = ?,`title` = ?,`content` = ?,`updatedTime` = ?,`date` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Affair value) {
        stmt.bindLong(1, value.getId());
        if (value.getTime() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTime());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTitle());
        }
        if (value.getContent() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getContent());
        }
        if (value.getUpdatedTime() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUpdatedTime());
        }
        final String _tmp;
        _tmp = __dateListStringConverter.dateListToString(value.getDate());
        if (_tmp == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, _tmp);
        }
        stmt.bindLong(7, value.getId());
      }
    };
    this.__preparedStmtOfDeleteAllAffairs = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM affairs";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAffairById = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM affairs WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertAffairs(final List<Affair> affairs) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfAffair.insert(affairs);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertAffair(final Affair affair) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfAffair.insert(affair);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateAffair(final Affair affair) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfAffair.handle(affair);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAllAffairs() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllAffairs.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAllAffairs.release(_stmt);
    }
  }

  @Override
  public void deleteAffairById(final long id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAffairById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAffairById.release(_stmt);
    }
  }

  @Override
  public Flowable<List<Affair>> queryAllAffairs() {
    final String _sql = "SELECT * FROM affairs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[]{"affairs"}, new Callable<List<Affair>>() {
      @Override
      public List<Affair> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfUpdatedTime = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedTime");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<Affair> _result = new ArrayList<Affair>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Affair _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTime;
            _tmpTime = _cursor.getString(_cursorIndexOfTime);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpUpdatedTime;
            _tmpUpdatedTime = _cursor.getString(_cursorIndexOfUpdatedTime);
            final List<Affair.Date> _tmpDate;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __dateListStringConverter.stringToDateList(_tmp);
            _item = new Affair(_tmpId,_tmpTime,_tmpTitle,_tmpContent,_tmpUpdatedTime,_tmpDate);
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
