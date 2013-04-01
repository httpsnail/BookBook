package com.android.bookbook.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.bookbook.R;
import com.android.bookbook.model.BookInfo;
import com.android.bookbook.util.ImageManager2;

public class ShowBook extends Activity{
	private TextView bookName;
	private TextView authorName;
	private TextView ISBNview;
	private ImageView bookImage;
	private TextView summary;
	private TextView bookComments;
	private BookInfo bookinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showbook);
		findViewById();
		initValues();
		createTabs();	
	}
	
	private void createTabs(){
		addTab(0,"简介",-1,R.id.Summary);
		addTab(1,"豆瓣书评",-1,R.id.BookComments);
	}
	private void addTab(int labelId, String label, int drawableId, int ContentId) {
		TabHost tabHost = (TabHost) findViewById(R.id.c92_tabhost);
		tabHost.setup();
		TabHost.TabSpec spec = tabHost.newTabSpec(labelId + "");
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_summary_indicator,tabHost.getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(label);
/*		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		if (drawableId != -1) {
			icon.setImageResource(drawableId);
		}*/
		spec.setIndicator(tabIndicator);
		spec.setContent(ContentId);
		tabHost.addTab(spec);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		boolean isExsit = getIntent().getBooleanExtra("isExist", false);
		if (isExsit) {
			Toast.makeText(getApplicationContext(), "书籍已在书架中", Toast.LENGTH_LONG).show();
		}
		isExsit = false;
	}

	private void findViewById() {
		bookName = (TextView) findViewById(R.id.bookNameTextview);
		authorName = (TextView) findViewById(R.id.authorTextview);
		ISBNview = (TextView) findViewById(R.id.ISBNTextview);
		bookImage = (ImageView) findViewById(R.id.BookImage);
		summary = (TextView) findViewById(R.id.Summary);
		bookComments = (TextView) findViewById(R.id.BookComments);

	}

	private void initValues() {
		bookinfo = (BookInfo) getIntent().getExtras().get("bookinfo");
		JSONObject resultJson = JSONObject.parseObject(bookinfo.getImageUrl());
		// 取中间像素的图片
		if (resultJson != null) {
			String bookImageURl = resultJson.getString("medium");
			if (bookImageURl != null) {
				ImageManager2.from().displayImage(bookImage, bookImageURl, -1);
			}
		}
		bookName.setText(bookinfo.getBookName());
		authorName.setText("作者: " + bookinfo.getAuthor());
		ISBNview.setText("ISBN: " + bookinfo.getISBN());
		bookComments.setText("比价内容");
		summary.setText(bookinfo.getSummary());
	}

}
