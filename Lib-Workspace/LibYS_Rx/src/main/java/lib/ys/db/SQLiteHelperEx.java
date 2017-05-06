package lib.ys.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.ys.ConstantsEx;
import lib.ys.LogMgr;

abstract public class SQLiteHelperEx {

    protected String TAG = getClass().getSimpleName();

    protected static final String KId = "_id";
    protected static final String KSplit = ", ";
    protected static final String KFrom = " from ";
    protected static final String KOrderBy = " order by ";

    private List<String> mFields;

    protected boolean mIsDataSetChanged;

    private DBHelper mDBHelper;

    protected SQLiteHelperEx(Context context) {
        mDBHelper = new DBHelper(context, getDbName(), getDbVersion());
        mFields = new ArrayList<String>();
    }

    protected SQLiteDatabase getWritableDatabase() {
        return mDBHelper.getWritableDatabase();
    }

    protected SQLiteDatabase getReadableDatabase() {
        return mDBHelper.getReadableDatabase();
    }

    protected SQLiteDatabase getTransactionDatabase() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        return db;
    }

    protected void closeCursor(Cursor cursor) {
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }
    }

    protected void closeStatement(SQLiteStatement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }
    }

    protected void endTransaction(SQLiteDatabase db) {
        try {
            if (db != null) {
                db.endTransaction();
            }
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }
    }

    protected void closeDB(SQLiteDatabase db) {
        try {
            if (db != null) {
                db.close();
            }
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }
    }

    public void closeDataBase() {
        try {
            mDBHelper.close();
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }
    }

    protected void closeCursorAndDB(Cursor cursor, SQLiteDatabase db) {
        try {
            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }
    }

    protected void closeStatementAndDB(SQLiteStatement statement, SQLiteDatabase db) {
        try {
            if (statement != null) {
                statement.close();
            }

            if (db != null) {
                db.close();
            }
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }
    }

    protected void endTransactionAndCloseDB(SQLiteDatabase db) {
        try {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        }
    }

    private class DBHelper extends SQLiteOpenHelper {

        protected DBHelper(Context context, String dbName, int version) {
            super(context, dbName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            SQLiteHelperEx.this.onCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            SQLiteHelperEx.this.onUpgrade(db, oldVersion, newVersion);
        }
    }

    protected void createTable(SQLiteDatabase db, String tableName) {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("CREATE TABLE IF NOT EXISTS ");
        sBuffer.append(tableName);
        sBuffer.append(" (");
        sBuffer.append(KId);
        sBuffer.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");

        for (int i = 0; i < mFields.size(); ++i) {
            sBuffer.append(mFields.get(i));
            if (i == mFields.size() - 1) {
                sBuffer.append(" TEXT");
                sBuffer.append(")");
            } else {
                sBuffer.append(" TEXT, ");
            }
        }

        db.execSQL(sBuffer.toString());
    }

    protected void deleteTable(SQLiteDatabase db, String tableName) {
        String sql = "DELETE FROM " + tableName + ";";
        db.execSQL(sql);
    }

    protected void dropTable(SQLiteDatabase db, String tableName) {
        String sql = " DROP TABLE IF EXISTS " + tableName;
        db.execSQL(sql);
    }

    protected String getInsertSql(String tableName) {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("INSERT INTO ");
        sBuffer.append(tableName);
        sBuffer.append(" (");

        int len = mFields.size();
        for (int i = 0; i < len; ++i) {
            sBuffer.append(mFields.get(i));
            if (i == len - 1) {
                sBuffer.append(") ");
            } else {
                sBuffer.append(KSplit);
            }
        }

        sBuffer.append("values (?");
        for (int i = 0; i < len - 1; ++i) {
            sBuffer.append(",?");
        }
        sBuffer.append(")");

        return sBuffer.toString();
    }

    protected void delete(String tableName, String field, String value) {
        SQLiteDatabase db = null;
        try {
            String sql = "DELETE FROM " + tableName + " WHERE " + field + " = ?";
            db = getWritableDatabase();
            db.execSQL(sql, new String[]{value});
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        } finally {
            closeDB(db);
        }
    }

    protected String getSelectSql(String tableName) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT ");
        sqlBuffer.append(KId);

        for (int i = 0; i < mFields.size(); ++i) {
            sqlBuffer.append(KSplit);
            sqlBuffer.append(mFields.get(i));
        }

        sqlBuffer.append(KFrom);
        sqlBuffer.append(tableName);

        return sqlBuffer.toString();
    }

    protected String getSelectSqlByOrder(String tableName, String orderField, boolean isOrder) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(getSelectSql(tableName));

        sqlBuffer.append(KOrderBy);
        sqlBuffer.append(orderField);
        if (isOrder) {
            sqlBuffer.append(" ASC");
        } else {
            sqlBuffer.append(" DESC");
        }

        return sqlBuffer.toString();
    }

    protected int getSize(String tableName) {
        int size = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT ");
            sqlBuffer.append(KId);
            sqlBuffer.append(KSplit);

            for (int i = 0; i < mFields.size(); ++i) {
                sqlBuffer.append(mFields.get(i));
                if (i == mFields.size() - 1) {
                    sqlBuffer.append(KFrom);
                } else {
                    sqlBuffer.append(KSplit);
                }
            }
            sqlBuffer.append(tableName);

            db = getReadableDatabase();
            cursor = db.rawQuery(sqlBuffer.toString(), null);
            while (cursor.moveToNext()) {
                size++;
            }

        } catch (Exception e) {
            LogMgr.e(TAG, e);
        } finally {
            closeCursorAndDB(cursor, db);
        }

        return size;
    }

    protected void addField(String field) {
        mFields.add(field);
    }

    protected boolean insert(String tableName, ContentValues values) {
        SQLiteDatabase db = null;
        long flag = ConstantsEx.KErrNotFound;
        try {
            db = getWritableDatabase();
            flag = db.insert(tableName, null, values);
        } catch (Exception e) {
            LogMgr.e(TAG, e);
        } finally {
            closeDB(db);
        }

        return flag != ConstantsEx.KErrNotFound;
    }

    public void setDataSetChangedState(boolean changed) {
        mIsDataSetChanged = changed;
    }

    public boolean isDataSetChanged() {
        return mIsDataSetChanged;
    }

    /**
     * 获取一个ContentValue的map
     *
     * @param tableName
     * @param field
     * @param selection
     * @return
     */
    protected List<Map<String, String>> getSelectionFieldList(String tableName, String field, String selection) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            String sql = "select * from " + tableName + " where " + field + " like '" + selection + "'";
            cursor = db.rawQuery(sql, null);
            int cols_len = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < cols_len; i++) {
                    String cols_name = cursor.getColumnName(i);
                    String cols_values = cursor.getString(cursor.getColumnIndex(cols_name));
                    if (cols_values == null) {
                        cols_values = "";
                    }
                    map.put(cols_name, cols_values);
                }
                list.add(map);
            }

        } catch (Exception e) {
            LogMgr.e(TAG, e);
        } finally {
            closeCursorAndDB(cursor, db);
        }
        return list;
    }

    abstract protected String getDbName();

    abstract protected int getDbVersion();

    abstract protected void onCreate(SQLiteDatabase db);

    abstract protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
