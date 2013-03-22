package com.android.bookbook.util;

public class BookbookApiUtil {
	public static String getBookisbnApiHost() {
		return "http://api.douban.com/v2/book/isbn/";
	}
	public static String getBookPriceApiHost(String doubanId) {
		return "http://book.douban.com/subject/"+doubanId+"/buylinks?sortby=price";
	}

}
