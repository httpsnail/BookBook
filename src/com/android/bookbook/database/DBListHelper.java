package com.android.bookbook.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBListHelper extends SQLiteOpenHelper{
	public DBListHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}



	private final static String DATABASE_NAME ="LIST.db";
	

	public void onCreate(SQLiteDatabase db){
		
		String sql = "";
		db.execSQL(sql);
	}
	


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
