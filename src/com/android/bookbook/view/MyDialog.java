package com.android.bookbook.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.bookbook.R;


public class MyDialog extends Dialog {

	public MyDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private Button dialogCancelButton;
	private Button dialogOkButton;
	private TextView title;
	private TextView message;
	private String stitle;
	private String smessage;
/*
	public MyDialog(Context context) {
		super(context, R.style.MyDialog);
		this.setCanceledOnTouchOutside(true);
	}
	
	public MyDialog(Context context, String stitle, String smessage) {
		super(context, R.style.MyDialog);
		this.stitle = stitle;
		this.smessage = smessage;
		this.setCanceledOnTouchOutside(true);
	}*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {

/*		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mydialog);
		dialogOkButton = (Button) this.findViewById(R.id.dialog_button_ok);
		dialogCancelButton = (Button) this.findViewById(R.id.dialog_button_cancel);
		title = (TextView) this.findViewById(R.id.title);
		message = (TextView) this.findViewById(R.id.message);
		this.title.setText(stitle);
		this.message.setText(smessage);*/
	}

	public void setTitle(String text){
		title.setText(text);
	}
	
	public void setMessage(String text){
		message.setText(text);
	}
	
	public void setOkBtnText(String text){
		dialogOkButton.setText(text);
	}
	
	public void setCancelBtnText(String text){
		dialogCancelButton.setText(text);
	}
	
	public void setOkBtnOnClickListener(android.view.View.OnClickListener mOkOnClickListener) {
		dialogOkButton.setVisibility(View.VISIBLE);
		dialogOkButton.setOnClickListener(mOkOnClickListener);
	}

	public void setCancelBtnOnClickListener(android.view.View.OnClickListener mCancelOnClickListener) {
		dialogCancelButton.setVisibility(View.VISIBLE);
		dialogCancelButton.setOnClickListener(mCancelOnClickListener);
	}
	
	public void showDefaultOkBtn() {
		dialogOkButton.setVisibility(View.VISIBLE);
		dialogOkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyDialog.this.dismiss();
			}
		});
	}
	
	public void showDefaultCancelBtn() {
		dialogCancelButton.setVisibility(View.VISIBLE);
		dialogCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyDialog.this.dismiss();
			}
		});
	}
	
	@Override
	public void show() {
		super.show();
		dialogOkButton.setVisibility(View.GONE);
		dialogCancelButton.setVisibility(View.GONE);
	}
}