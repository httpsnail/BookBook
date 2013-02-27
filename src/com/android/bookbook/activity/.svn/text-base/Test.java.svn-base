package com.android.bookbook.activity;

import com.android.bookbook.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Test extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.test);
	TextView temp = (TextView) findViewById(R.id.testISBN);
	Bundle bun = getIntent().getExtras();
	String tempIsbn = bun.getString("result");
	// String ISBN=tempIsbn.substring(7);
	temp.setText(tempIsbn);
    }

}
