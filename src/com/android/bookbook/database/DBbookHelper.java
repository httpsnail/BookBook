package com.android.bookbook.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBbookHelper extends SQLiteOpenHelper {

   private static final String DATABASE_NAME = "shuji.db";
   private static final int DATABASE_VERSION = 3;
   private static final String TABLE_NAME = "bookinfos";
   public static final String FIELD_TITLE = "title";
   public static final String FIELD_name = "name";
   public static final String FIELD_url = "url";
   public static final String FIELD_author = "author";
   private static DBbookHelper mInstance;
   
	public static synchronized DBbookHelper getInsance(Context context) {
		if (mInstance == null) {
			mInstance = new DBbookHelper(context);
		}
		return mInstance;
	}
   
   public DBbookHelper(Context context) {
       this(context, DATABASE_NAME, DATABASE_VERSION);
   }
   
   public DBbookHelper(Context context, String name, int version) {  
	      this(context, name,null, version);  
	  
	  }  
   public DBbookHelper(Context context, String name,  
		              CursorFactory factory, int version) {  
		          super(context, name, factory, version);  
		 
		     }  


   @Override
   public void onCreate(SQLiteDatabase db) {
	   Log.d("init","init the database");
	  // db.execSQL("CREATE TABLE IF NOT EXISTS bookinfo (bookinfo integer primary key autoincrement, name varchar(20), author varchar(20),url varchar(20))");  
	   db.execSQL("CREATE TABLE IF NOT EXISTS bookinfos (id integer primary key autoincrement, name varchar(20), author varchar(20),url varchar(20),imageUrl varchar(50),doubanId varchar(10),ISBN varchar(20),summary text)");  
	   //db.execSQL("CREATE TABLE IF NOT EXISTS info_list (id integer primary key autoincrement, name varchar(20), author varchar(20),url varchar(20))");  
   }
 
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       String sql=" DROP TABLE IF EXISTS "+TABLE_NAME;
       db.execSQL(sql);
       onCreate(db);
   }
   public boolean queryExsit(){
	   return true;
   }
   
}