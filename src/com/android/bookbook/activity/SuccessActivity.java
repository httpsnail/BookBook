package com.android.bookbook.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.bookbook.R;
import com.android.bookbook.database.DBbookHelper;
import com.android.bookbook.model.BookInfo;
import com.android.bookbook.util.HttpUtil;
import com.android.bookbook.util.PicUtil;

public class SuccessActivity extends Activity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1154263263314742973L;
	private static String url = "http://api.douban.com/v2/book/isbn/";
	private static String bijiaUrl = "";
	BookInfo bookinfo;
	String ISBN = "";
	Bitmap bimage = null;
	List<Map<String, Object>> priceList;
	private ProgressDialog dialog;
	private boolean isLoadFinish = false;
	public ListView b2cListView = null;
	SimpleAdapter adapter = null;
	LayoutInflater inflater = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.success);
		inflater = LayoutInflater.from(this);
		TabHost tabs = (TabHost) findViewById(R.id.c92_tabhost);
		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec("Tag1");
		spec.setContent(R.id.Summary);
		spec.setIndicator("简介", null);
		tabs.addTab(spec);
		/* （2）增加第2页 */
		spec = tabs.newTabSpec("Tag2");
		spec.setContent(R.id.MarketListview);
		spec.setIndicator("比价", null);
		tabs.addTab(spec);
		// 步骤3：可通过setCurrentTab(index)指定显示的页，从0开始计算。
		tabs.setCurrentTab(0);
		// 设置一个progressdialog的弹窗

		final TabWidget tabWidget = tabs.getTabWidget();

		for (int i = 0; i < tabWidget.getChildCount(); i++) {

			tabWidget.getChildAt(i).getLayoutParams().height = 45;
		}
		dialog = ProgressDialog.show(this, null, "程序正在加载，请稍候...", true, false);
		final TextView bookName = (TextView) findViewById(R.id.bookNameTextview);
		final TextView authorName = (TextView) findViewById(R.id.authorTextview);
		final TextView ISBNview = (TextView) findViewById(R.id.ISBNTextview);
		final ImageView bookImage = (ImageView) findViewById(R.id.BookImage);
		final Button addToShelf = (Button) findViewById(R.id.addToShelf);
		final TextView summary = (TextView) findViewById(R.id.Summary);

		Bundle bun = getIntent().getExtras();
		String tempIsbn = bun.getString("result");
		ISBN = tempIsbn;

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					bookinfo = getResultByIsbn(ISBN);
					bimage = getBitmapFromURL(bookinfo.getImageUrl());
					// 获取图书比价
					// getResultBySubject();
					isLoadFinish = true;
					// 添加图书到书架
					addToShelf.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// Save image into sdCard
							saveImage2SdCard();
							ContentValues values = new ContentValues();
							values.put("author", bookinfo.getAuthor());
							values.put("name", bookinfo.getBookName());
							values.put("url", bookinfo.getUrl());
							values.put("ISBN", bookinfo.getISBN());
							values.put("summary", bookinfo.getSummary());
							DBbookHelper dbHelp = new DBbookHelper(
									SuccessActivity.this);
							SQLiteDatabase db = dbHelp.getWritableDatabase();
							db.insert("bookinfos", null, values);
							db.close();
							Intent intent = new Intent();
							intent.putExtra("bookinfo", bookinfo);
							intent.setClass(SuccessActivity.this,
									EntryActivity.class);
							SuccessActivity.this.startActivity(intent);
							SuccessActivity.this.finish();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// Update UI
						if (isLoadFinish) {
							dialog.dismiss();
							bookName.setText(bookinfo.getBookName());
							authorName.setText(bookinfo.getAuthor());
							ISBNview.setText(bookinfo.getISBN());
							bookImage.setImageBitmap(bimage);
							summary.setText(bookinfo.getSummary());
							//b2cListView.setAdapter(adapter);
						}
					}
				});

			}
		}).start();

	}

	public static Bitmap getBitmapFromURL(String src) {
		Bitmap bitmap = null;
		try {
			JSONObject resultJson = JSONObject.parseObject(src);
			// 取中间像素的图片
			String middle = resultJson.getString("medium");
			byte[] byteArray = PicUtil.loadImageFromNetwork(middle);
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
					byteArray.length);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Exception", e.getMessage());

		}
		return bitmap;
	}

	/* 根据ISBN获取书籍的内容 */
	private BookInfo getResultByIsbn(String isbn) throws Exception {
		String getUrl = url + isbn;
		String content = HttpUtil.getMethod(getUrl);
		JSONObject resultJson = JSONObject.parseObject(content);
		bookinfo = new BookInfo();
		bookinfo.setAuthor(resultJson.getString("author"));
		bookinfo.setISBN(isbn);
		bookinfo.setBookName(resultJson.getString("title"));
		bookinfo.setSummary(resultJson.getString("summary"));
		bookinfo.setUrl(getUrl);
		bookinfo.setImageUrl(resultJson.getString("images"));
		return bookinfo;
	}

	/* 获取比价信息 */
	private void getResultBySubject() {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://" + bijiaUrl.substring(11)
				+ "/buylinks?sortby=price");
		HttpResponse response;
		try {
			response = client.execute(get);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			priceList = new ArrayList<Map<String, Object>>();
			String regex = "([京东|亚|当])(.*?)</a>(.*?) href=\"(.*?)\">(.*?)</a>";
			// String regex = "京东网上商城";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(sb.toString());
			String val = null;
			while (m.find()) {
				val = m.group();
				Map<String, Object> map = new HashMap<String, Object>();
				if ((m.group(1) + m.group(2)).equals("京东网上商城")) {
					map.put("PIC", R.drawable.jingdong);
				}
				if ((m.group(1) + m.group(2)).equals("亚马逊")) {
					map.put("PIC", R.drawable.joyo);
				}
				if ((m.group(1) + m.group(2)).equals("当当网")) {
					map.put("PIC", R.drawable.dangdang);
				}
				map.put("Price", m.group(5));
				map.put("MarketLink", m.group(4));
				priceList.add(map);
			}

			b2cListView = (ListView) findViewById(R.id.MarketListview);
			adapter = new SimpleAdapter(this,
					(List<Map<String, Object>>) priceList, R.layout.b2citem,
					new String[] { "PIC", "Price" }, new int[] { R.id.b2cImage,
							R.id.price });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void saveImage2SdCard() {
		File filename;
		try {
			File sd = Environment.getExternalStorageDirectory();
			String path = sd.getPath() + "/shuji/images";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			// use isbn as images' name
			filename = new File(path + "/" + bookinfo.getISBN() + ".jpg");
			Log.i("in save()", filename.getPath().toString());
			FileOutputStream out = new FileOutputStream(filename);
			bimage.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

			MediaStore.Images.Media.insertImage(getContentResolver(),
					filename.getAbsolutePath(), filename.getName(),
					filename.getName());

			Toast.makeText(getApplicationContext(),

			"File is Saved in  " + filename, 500).show();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}
