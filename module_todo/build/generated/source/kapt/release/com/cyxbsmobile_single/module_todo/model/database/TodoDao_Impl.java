package com.cyxbsmobile_single.module_todo.model.database;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.RxRoom;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode;
import com.cyxbsmobile_single.module_todo.model.bean.Todo;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class TodoDao_Impl implements TodoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Todo> __insertionAdapterOfTodo;

  private final Convert __convert = new Convert();

  private final EntityDeletionOrUpdateAdapter<Todo> __updateAdapterOfTodo;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllTodo;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTodoById;

  public TodoDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTodo = new EntityInsertionAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `todo_list` (`todoId`,`title`,`detail`,`isChecked`,`remindMode`,`lastModifyTime`,`repeatStatus`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.getTodoId());
        if (value.getTitle() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTitle());
        }
        if (value.getDetail() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDetail());
        }
        stmt.bindLong(4, value.isChecked());
        final String _tmp;
        _tmp = __convert.remindMode2String(value.getRemindMode());
        if (_tmp == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, _tmp);
        }
        stmt.bindLong(6, value.getLastModifyTime());
        stmt.bindLong(7, value.getRepeatStatus());
      }
    };
    this.__updateAdapterOfTodo = new EntityDeletionOrUpdateAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `todo_list` SET `todoId` = ?,`title` = ?,`detail` = ?,`isChecked` = ?,`remindMode` = ?,`lastModifyTime` = ?,`repeatStatus` = ? WHERE `todoId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.getTodoId());
        if (value.getTitle() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTitle());
        }
        if (value.getDetail() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDetail());
        }
        stmt.bindLong(4, value.isChecked());
        final String _tmp;
        _tmp = __convert.remindMode2String(value.getRemindMode());
        if (_tmp == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, _tmp);
        }
        stmt.bindLong(6, value.getLastModifyTime());
        stmt.bindLong(7, value.getRepeatStatus());
        stmt.bindLong(8, value.getTodoId());
      }
    };
    this.__preparedStmtOfDeleteAllTodo = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM todo_list";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteTodoById = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM todo_list WHERE todoId = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertTodoList(final List<Todo> todoList) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTodo.insert(todoList);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Single<Long> insertTodo(final Todo todo) {
    return Single.fromCallable(new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfTodo.insertAndReturnId(todo);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public void updateTodo(final Todo todo) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfTodo.handle(todo);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateTodoList(final List<Todo> todoList) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfTodo.handleMultiple(todoList);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAllTodo() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllTodo.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAllTodo.release(_stmt);
    }
  }

  @Override
  public void deleteTodoById(final long todoId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTodoById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, todoId);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteTodoById.release(_stmt);
    }
  }

  @Override
  public Flowable<List<Todo>> queryAllTodo() {
    final String _sql = "SELECT * FROM todo_list ORDER by lastModifyTime desc";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[]{"todo_list"}, new Callable<List<Todo>>() {
      @Override
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTodoId = CursorUtil.getColumnIndexOrThrow(_cursor, "todoId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDetail = CursorUtil.getColumnIndexOrThrow(_cursor, "detail");
          final int _cursorIndexOfIsChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecked");
          final int _cursorIndexOfRemindMode = CursorUtil.getColumnIndexOrThrow(_cursor, "remindMode");
          final int _cursorIndexOfLastModifyTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifyTime");
          final int _cursorIndexOfRepeatStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatStatus");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpTodoId;
            _tmpTodoId = _cursor.getLong(_cursorIndexOfTodoId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDetail;
            _tmpDetail = _cursor.getString(_cursorIndexOfDetail);
            final int _tmpIsChecked;
            _tmpIsChecked = _cursor.getInt(_cursorIndexOfIsChecked);
            final RemindMode _tmpRemindMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRemindMode);
            _tmpRemindMode = __convert.string2RemindMode(_tmp);
            final long _tmpLastModifyTime;
            _tmpLastModifyTime = _cursor.getLong(_cursorIndexOfLastModifyTime);
            final int _tmpRepeatStatus;
            _tmpRepeatStatus = _cursor.getInt(_cursorIndexOfRepeatStatus);
            _item = new Todo(_tmpTodoId,_tmpTitle,_tmpDetail,_tmpIsChecked,_tmpRemindMode,_tmpLastModifyTime,_tmpRepeatStatus);
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

  @Override
  public Flowable<Todo> queryTodoById(final long todoId) {
    final String _sql = "SELECT * FROM todo_list WHERE todoId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, todoId);
    return RxRoom.createFlowable(__db, false, new String[]{"todo_list"}, new Callable<Todo>() {
      @Override
      public Todo call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTodoId = CursorUtil.getColumnIndexOrThrow(_cursor, "todoId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDetail = CursorUtil.getColumnIndexOrThrow(_cursor, "detail");
          final int _cursorIndexOfIsChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecked");
          final int _cursorIndexOfRemindMode = CursorUtil.getColumnIndexOrThrow(_cursor, "remindMode");
          final int _cursorIndexOfLastModifyTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifyTime");
          final int _cursorIndexOfRepeatStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatStatus");
          final Todo _result;
          if(_cursor.moveToFirst()) {
            final long _tmpTodoId;
            _tmpTodoId = _cursor.getLong(_cursorIndexOfTodoId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDetail;
            _tmpDetail = _cursor.getString(_cursorIndexOfDetail);
            final int _tmpIsChecked;
            _tmpIsChecked = _cursor.getInt(_cursorIndexOfIsChecked);
            final RemindMode _tmpRemindMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRemindMode);
            _tmpRemindMode = __convert.string2RemindMode(_tmp);
            final long _tmpLastModifyTime;
            _tmpLastModifyTime = _cursor.getLong(_cursorIndexOfLastModifyTime);
            final int _tmpRepeatStatus;
            _tmpRepeatStatus = _cursor.getInt(_cursorIndexOfRepeatStatus);
            _result = new Todo(_tmpTodoId,_tmpTitle,_tmpDetail,_tmpIsChecked,_tmpRemindMode,_tmpLastModifyTime,_tmpRepeatStatus);
          } else {
            _result = null;
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

  @Override
  public Flowable<List<Todo>> queryTodoByWeatherDone(final int isChecked) {
    final String _sql = "SELECT * FROM todo_list WHERE isChecked = ? ORDER by lastModifyTime desc";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, isChecked);
    return RxRoom.createFlowable(__db, false, new String[]{"todo_list"}, new Callable<List<Todo>>() {
      @Override
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTodoId = CursorUtil.getColumnIndexOrThrow(_cursor, "todoId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDetail = CursorUtil.getColumnIndexOrThrow(_cursor, "detail");
          final int _cursorIndexOfIsChecked = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecked");
          final int _cursorIndexOfRemindMode = CursorUtil.getColumnIndexOrThrow(_cursor, "remindMode");
          final int _cursorIndexOfLastModifyTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModifyTime");
          final int _cursorIndexOfRepeatStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatStatus");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpTodoId;
            _tmpTodoId = _cursor.getLong(_cursorIndexOfTodoId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDetail;
            _tmpDetail = _cursor.getString(_cursorIndexOfDetail);
            final int _tmpIsChecked;
            _tmpIsChecked = _cursor.getInt(_cursorIndexOfIsChecked);
            final RemindMode _tmpRemindMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRemindMode);
            _tmpRemindMode = __convert.string2RemindMode(_tmp);
            final long _tmpLastModifyTime;
            _tmpLastModifyTime = _cursor.getLong(_cursorIndexOfLastModifyTime);
            final int _tmpRepeatStatus;
            _tmpRepeatStatus = _cursor.getInt(_cursorIndexOfRepeatStatus);
            _item = new Todo(_tmpTodoId,_tmpTitle,_tmpDetail,_tmpIsChecked,_tmpRemindMode,_tmpLastModifyTime,_tmpRepeatStatus);
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

  @Override
  public Flowable<List<Todo>> queryTodoByIdList(final SupportSQLiteQuery query) {
    final SupportSQLiteQuery _internalQuery = query;
    return RxRoom.createFlowable(__db, false, new String[]{"todo_list"}, new Callable<List<Todo>>() {
      @Override
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _internalQuery, false, null);
        try {
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Todo _item;
            _item = __entityCursorConverter_comCyxbsmobileSingleModuleTodoModelBeanTodo(_cursor);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }
    });
  }

  private Todo __entityCursorConverter_comCyxbsmobileSingleModuleTodoModelBeanTodo(Cursor cursor) {
    final Todo _entity;
    final int _cursorIndexOfTodoId = cursor.getColumnIndex("todoId");
    final int _cursorIndexOfTitle = cursor.getColumnIndex("title");
    final int _cursorIndexOfDetail = cursor.getColumnIndex("detail");
    final int _cursorIndexOfIsChecked = cursor.getColumnIndex("isChecked");
    final int _cursorIndexOfRemindMode = cursor.getColumnIndex("remindMode");
    final int _cursorIndexOfLastModifyTime = cursor.getColumnIndex("lastModifyTime");
    final int _cursorIndexOfRepeatStatus = cursor.getColumnIndex("repeatStatus");
    final long _tmpTodoId;
    if (_cursorIndexOfTodoId == -1) {
      _tmpTodoId = 0;
    } else {
      _tmpTodoId = cursor.getLong(_cursorIndexOfTodoId);
    }
    final String _tmpTitle;
    if (_cursorIndexOfTitle == -1) {
      _tmpTitle = null;
    } else {
      _tmpTitle = cursor.getString(_cursorIndexOfTitle);
    }
    final String _tmpDetail;
    if (_cursorIndexOfDetail == -1) {
      _tmpDetail = null;
    } else {
      _tmpDetail = cursor.getString(_cursorIndexOfDetail);
    }
    final int _tmpIsChecked;
    if (_cursorIndexOfIsChecked == -1) {
      _tmpIsChecked = 0;
    } else {
      _tmpIsChecked = cursor.getInt(_cursorIndexOfIsChecked);
    }
    final RemindMode _tmpRemindMode;
    if (_cursorIndexOfRemindMode == -1) {
      _tmpRemindMode = null;
    } else {
      final String _tmp;
      _tmp = cursor.getString(_cursorIndexOfRemindMode);
      _tmpRemindMode = __convert.string2RemindMode(_tmp);
    }
    final long _tmpLastModifyTime;
    if (_cursorIndexOfLastModifyTime == -1) {
      _tmpLastModifyTime = 0;
    } else {
      _tmpLastModifyTime = cursor.getLong(_cursorIndexOfLastModifyTime);
    }
    final int _tmpRepeatStatus;
    if (_cursorIndexOfRepeatStatus == -1) {
      _tmpRepeatStatus = 0;
    } else {
      _tmpRepeatStatus = cursor.getInt(_cursorIndexOfRepeatStatus);
    }
    _entity = new Todo(_tmpTodoId,_tmpTitle,_tmpDetail,_tmpIsChecked,_tmpRemindMode,_tmpLastModifyTime,_tmpRepeatStatus);
    return _entity;
  }
}
