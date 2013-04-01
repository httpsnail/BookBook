package com.android.bookbook.activity;


import com.android.bookbook.R;
import com.android.bookbook.model.BookInfo;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AddtoShelf extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.addtoshelf);
	
	TextView bookName=(TextView)findViewById(R.id.bookNameTextview);
	TextView authorName=(TextView)findViewById(R.id.authorTextview);
	TextView ISBNview =(TextView)findViewById(R.id.ISBNTextview);
	ImageView bookImage =(ImageView)findViewById(R.id.BookImage);
	Button addToShelf =(Button)findViewById(R.id.addToShelf);
	
	
	BookInfo bookinfo=(BookInfo)getIntent().getExtras().get("bookinfo");
	
	String imagePath = "/sdcard/shuji/images/"+bookinfo.getISBN()+".jpg";  
	bookImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
	bookName.setText(bookinfo.getBookName());
	authorName.setText("作者: "+bookinfo.getAuthor());
	ISBNview.setText("ISBN: "+bookinfo.getISBN());
	addToShelf.setOnClickListener(new OnClickListener(){

	    @Override
	    public void onClick(View v) {
		
		
	    }
	    
	});
    }
    

}
