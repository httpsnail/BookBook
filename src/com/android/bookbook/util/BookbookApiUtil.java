package com.android.bookbook.util;

import android.util.Log;

public class BookbookApiUtil {
	public static String getBookisbnApiHost() {
		return "http://api.douban.com/v2/book/isbn/";
	}
	public static String getBookPriceApiHost(String doubanId) {
		Log.e("BookURL", "http://book.douban.com/subject/"+doubanId+"/buylinks?sortby=price");
		return "http://book.douban.com/subject/"+doubanId+"/buylinks?sortby=price";
	}

}
