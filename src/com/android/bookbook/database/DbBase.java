package com.android.bookbook.database;

import android.database.sqlite.SQLiteDatabase;

import com.android.bookbook.*;

/**
 * @author zhupeng 
 * @date 2012-7-10
 */
public class DbBase {

	private static DBbookHelper dbHelper;

	private static SQLiteDatabase mSQLiteDatabase;

	public DbBase() {
		if (dbHelper == null) {
			dbHelper = DBbookHelper.getInsance(BookbookApp.getInstance());
		}

	}
	public DbBase(SQLiteDatabase mSQLiteDatabase) {

		this.mSQLiteDatabase = mSQLiteDatabase;

	}

	/**
	 * 关闭数据库
	 */
	public static void close() {
		if (dbHelper !=null && mSQLiteDatabase != null && mSQLiteDatabase.isOpen()) {
			dbHelper.close();

		}
	}

	/**
	 * 打开数据库
	 */

	public static SQLiteDatabase getSQLiteDatabase() {
		if (mSQLiteDatabase == null
				|| (mSQLiteDatabase != null && !mSQLiteDatabase.isOpen())) {
			if (dbHelper == null) {
				dbHelper = DBbookHelper.getInsance(BookbookApp.getInstance());
			}
			mSQLiteDatabase = dbHelper.getWritableDatabase();

		}
		return mSQLiteDatabase;
	}

	public static void tranBegin() {

		if (getSQLiteDatabase() != null) {
			getSQLiteDatabase().beginTransaction();
		}
	}

	public static void tranSuccess() {
		if (getSQLiteDatabase() != null) {
			getSQLiteDatabase().setTransactionSuccessful();
		}
	}

	public static void tranEnd() {
		if (getSQLiteDatabase() != null) {
			getSQLiteDatabase().endTransaction();
		}

	}

}
