package com.android.bookbook.activity;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import com.android.bookbook.R;
import com.android.bookbook.DBHelper.DBbookHelper;
import com.android.bookbook.model.BookInfo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputISBNActivity extends Activity implements Serializable{
	 /**
     * 
     */
    private static final long serialVersionUID = 4971486568753723374L;

	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.inputisbnactivity);
	        
	        Button button = (Button)this.findViewById(R.id.searchDOUBAN);	      
	        button.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					EditText isbnEditText =(EditText)findViewById(R.id.editISBN);  
					String ISBN=isbnEditText.getText().toString();
					
					DBbookHelper mDBHelper=new DBbookHelper(InputISBNActivity.this);
					String[] parms={ISBN}; 
					Cursor c = mDBHelper.getReadableDatabase().query(
							"bookinfos",new String[]{"id","name","url","author","summary","ISBN"},"ISBN=?",parms,null,null,null); 					

					if(c.moveToNext()){
					    	BookInfo bookinfo =new BookInfo();
					    	bookinfo.setAuthor(c.getString(c.getColumnIndex("author")));
					    	String imagePath = "/sdcard/shuji/images/"
						    + c.getString(c.getColumnIndex("ISBN")) + ".jpg";
					    	//bookinfo.setImage( BitmapFactory.decodeFile(imagePath));
					    	bookinfo.setISBN(ISBN);
					    	bookinfo.setBookName(c.getString(c.getColumnIndex("name")));
					    	bookinfo.setSummary(c.getString(c.getColumnIndex("summary")));
					    	System.out.println(bookinfo.getSummary());
						Intent intent = new Intent(InputISBNActivity.this, ShowBook.class);	
						intent.putExtra("isExist",true);
						intent.putExtra("bookinfo", bookinfo);
						InputISBNActivity.this.startActivity(intent);		
						InputISBNActivity.this.finish();
					}
					else{
						Intent intent = new Intent(InputISBNActivity.this, SuccessActivity.class);
						intent.putExtra("result",ISBN);
						InputISBNActivity.this.startActivity(intent);
						InputISBNActivity.this.finish();
					}
					
					mDBHelper.close();
					
					
				}
	        	
	        });
     }

}
