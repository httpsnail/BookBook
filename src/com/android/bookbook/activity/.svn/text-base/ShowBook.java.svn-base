package com.android.bookbook.activity;

import java.util.HashMap;

import com.android.bookbook.R;
import com.android.bookbook.model.BookInfo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class ShowBook extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showbook);
		
		TextView bookName=(TextView)findViewById(R.id.bookNameTextview);
		TextView authorName=(TextView)findViewById(R.id.authorTextview);
		TextView ISBNview =(TextView)findViewById(R.id.ISBNTextview);
		ImageView bookImage =(ImageView)findViewById(R.id.BookImage);
		final TextView summary=(TextView)findViewById(R.id.Summary);
		final TextView bookComments=(TextView)findViewById(R.id.BookComments);
		
		BookInfo bookinfo=(BookInfo)getIntent().getExtras().get("bookinfo");
		
		String imagePath = "/sdcard/shuji/images/"+bookinfo.getISBN()+".jpg";  
		bookImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
		bookName.setText(bookinfo.getBookName());
		authorName.setText("作者: "+bookinfo.getAuthor());
		ISBNview.setText("ISBN: "+bookinfo.getISBN());
		bookComments.setText("比价内容");
		
		 TabHost tabs = (TabHost)findViewById(R.id.c92_tabhost);
		 tabs.setup();
 
		 TabHost.TabSpec spec = tabs.newTabSpec("Tag1");
		 spec.setContent(R.id.Summary);
		 spec.setIndicator("简介",null);
		 tabs.addTab(spec);
		 /*（2）增加第2页*/
		 spec = tabs.newTabSpec("Tag2");
		 spec.setContent(R.id.BookComments);
		 spec.setIndicator("书评",null);
		 tabs.addTab(spec);
		 //步骤3：可通过setCurrentTab(index)指定显示的页，从0开始计算。
		 tabs.setCurrentTab(0);
		 //设置一个progressdialog的弹窗  
		 
		 final TabWidget tabWidget = tabs.getTabWidget();
		 
		 for (int i =0; i < tabWidget.getChildCount(); i++) {  

		             tabWidget.getChildAt(i).getLayoutParams().height = 45;  	             
		 }
		 summary.setText(bookinfo.getSummary());
	}

	@Override
	protected void onStart() {
	    // TODO Auto-generated method stub
	    super.onStart();
		boolean isExsit = getIntent().getBooleanExtra("isExist", false);
		if (isExsit) {
		    Toast.makeText(getApplicationContext(), "书籍已在书架中",
			    Toast.LENGTH_LONG).show();
		}
		isExsit = false;
	}
	

}
