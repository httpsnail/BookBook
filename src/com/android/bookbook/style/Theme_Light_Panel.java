package com.android.bookbook.style;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.bookbook.activity.MainActivity;
import com.android.bookbook.R;

public class Theme_Light_Panel extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.theme_panel);
		//String conent=getIntent().getStringExtra("content");
		TextView text=(TextView)findViewById(R.id.them_panel);
		text.setText("书籍已经在书架中");
		Intent intent =new Intent();
		intent.setClass(Theme_Light_Panel.this, MainActivity.class);
		
		
	}
	

}
