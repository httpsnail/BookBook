package com.android.bookbook.database;

import java.util.ArrayList;
import java.util.List;
import com.android.bookbook.model.BookInfo;
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
						"summary" // 5
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
				list.add(bookinfo);
			}
			cursor.close();
		}
		return list;
	}

	/* 更新操作 */

	/* 删除操作 */

}
