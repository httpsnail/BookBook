package com.android.bookbook.activity;

import java.net.URLDecoder;
import java.net.URLEncoder;

import com.android.bookbook.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class BuyBookActivity extends Activity {
	private WebView webView;
	private ProgressBar progressBar;
	// private String url = "http://www.haozu.com/pay/personal/send/";
	private String url = "http://douban.com";
	static private String cookie = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buybook_activity);
		init();
		initListener();
	}

	private void init() {
		Intent intent = this.getIntent();
		url = intent.getStringExtra("buyurl");
		Log.e("bookbok", url);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		webView = (WebView) findViewById(R.id.web_view);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				progressBar.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				progressBar.setVisibility(View.INVISIBLE);
				super.onPageFinished(view, url);
			}

			
		});
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.loadUrl(url);
	}

	private void initListener() {

	}

	@Override
	public void onBackPressed() {
		finish();
	}

}
