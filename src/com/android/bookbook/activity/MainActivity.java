package com.android.bookbook.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.android.bookbook.R;

public class MainActivity extends TabActivity implements OnTabChangeListener {
	private RelativeLayout addBookLayout;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		createTabs();
		getTabHost().setOnTabChangedListener(this);
		initValues();

	}

	private void initValues() {
		int screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		addBookLayout = (RelativeLayout) findViewById(R.id.add_book);
		LayoutParams lp = (LayoutParams) addBookLayout.getLayoutParams();
		lp.width = screenWidth / 3;
		addBookLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
	}

	/**
	 * 创建tab标签
	 * */
	private void createTabs() {
		addTab(0, "书架", -1, BookShelfActivity.class);
		addTab(1, "扫描", -1, CaptureActivity.class);
		addTab(2, "我的", -1, CaptureActivity.class);
	}

	private void addTab(int labelId, String label, int drawableId, Class<?> c) {
		TabHost tabHost = getTabHost();
		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = tabHost.newTabSpec(labelId + "");

		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(label);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		if (drawableId != -1) {
			icon.setImageResource(drawableId);
		}
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub

	}
}
