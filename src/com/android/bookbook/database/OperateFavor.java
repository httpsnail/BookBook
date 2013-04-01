package com.android.bookbook.database;

import java.util.ArrayList;
import java.util.List;

import com.android.bookbook.activity.MainActivity;
import com.android.bookbook.activity.SearchBookInfoActivity;
import com.android.bookbook.model.BookInfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OperateFavor extends DbBase {

	public OperateFavor(SQLiteDatabase mSQLiteDatabase) {
		super(mSQLiteDatabase);
	}

	public OperateFavor() {
		super();
	}

	/* 查询操作 */
	public List<BookInfo> queryBookList() {
		Cursor cursor = getSQLiteDatabase().query("bookinfos",
				new String[] { "id", // 0
						"name", // 1
						"url", // 2
						"author", // 3
						"ISBN",// 4
						"summary", // 5
						"imageUrl" //6
				}, null, null, null, null, null);

		List<BookInfo> list = new ArrayList<BookInfo>();
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				BookInfo bookinfo = new BookInfo();
				bookinfo.setId(cursor.getInt(0));
				bookinfo.setAuthor(cursor.getString(3));
				bookinfo.setBookName(cursor.getString(1));
				// bookinfo.setImage(image);
				bookinfo.setISBN(cursor.getString(4));
				bookinfo.setSummary(cursor.getString(5));
				bookinfo.setImageUrl(cursor.getString(6));
				list.add(bookinfo);
			}
			cursor.close();
		}
		return list;
	}

	/* 插入操作 */
	
	public void insertIntoBookList(BookInfo bookinfo){
		ContentValues values = new ContentValues();
		values.put("author", bookinfo.getAuthor());
		values.put("name", bookinfo.getBookName());
		values.put("url", bookinfo.getUrl());
		values.put("imageUrl", bookinfo.getImageUrl());
		values.put("ISBN", bookinfo.getISBN());
		values.put("summary", bookinfo.getSummary());
		getSQLiteDatabase().insert("bookinfos", null, values);
	}

	/* 删除操作 */
	public void delteteBook(String table,String column,String value){		
		String[] args = { value };
		DbBase.getSQLiteDatabase().delete(table,column+"=?",args);
		
	}

}
