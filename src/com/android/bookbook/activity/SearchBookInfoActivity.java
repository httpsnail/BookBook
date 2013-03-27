package com.android.bookbook.activity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.android.bookbook.BookbookApp;
import com.android.bookbook.R;
import com.android.bookbook.database.OperateFavor;
import com.android.bookbook.model.BookInfo;
import com.android.bookbook.util.AppCommonUtil;
import com.android.bookbook.util.BookbookApiUtil;
import com.android.bookbook.util.HttpUtil;
import com.android.bookbook.util.ImageManager2;
import com.android.bookbook.util.PicUtil;

@SuppressLint("ShowToast")
public class SearchBookInfoActivity extends Activity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1154263263314742973L;

	private String buyurl = "http://www.douban.com";
	BookInfo bookinfo = new BookInfo();
	OperateFavor opFav = new OperateFavor();
	String ISBN = "";
	Bitmap bimage = null;
	List<Map<String, Object>> priceList;
	private ProgressDialog dialog;
	public ListView b2cListView;
	SimpleAdapter adapter = null;
	LayoutInflater inflater = null;
	private TextView bookName;
	private TextView authorName;
	private ImageView bookImage;
	private TextView addToShelf;
	private TextView summary;
	private TabHost tabHost;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.success);
		findViewById();
		Bundle bun = getIntent().getExtras();
		String tempIsbn = bun.getString("result");
		ISBN = tempIsbn;

		if (!AppCommonUtil.isNetworkAvailable(SearchBookInfoActivity.this)) {
			Toast.makeText(SearchBookInfoActivity.this, R.string.network_unavailable, 500).show();
			this.finish();
			return;
		}
		if (AppCommonUtil.isNetworkAvailable(BookbookApp.getInstance())) {
			dialog = ProgressDialog.show(this, null, "程序正在加载，请稍候...", true, false);
			GetBookInfoTask gbit = new GetBookInfoTask();
			gbit.execute(ISBN);
		}
		initListener();
	}
	private void createTabs() {
		addTab(0, R.string.bookSummary,-1,R.id.Summary);
		addTab(1, R.string.comparePrice, -1, R.id.MarketListview);
		addTab(2, R.string.bookComments, -1, R.id.DoubanListview);
	}
	
	private void addTab(int labelId, int label, int drawableId, int contentId) {
		TabHost.TabSpec spec = tabHost.newTabSpec(labelId + "");
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator,tabHost.getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(label);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		if (drawableId != -1) {
			icon.setImageResource(drawableId);
		}
		spec.setIndicator(tabIndicator);
		spec.setContent(contentId);
		tabHost.addTab(spec);
	}

	private void findViewById() {
		bookName = (TextView) findViewById(R.id.bookNameTextview);
		authorName = (TextView) findViewById(R.id.authorTextview);
		bookImage = (ImageView) findViewById(R.id.BookImage);
		addToShelf = (TextView) findViewById(R.id.addToShelf);
		summary = (TextView) findViewById(R.id.Summary);
		b2cListView = (ListView) findViewById(R.id.MarketListview);
		tabHost = (TabHost) findViewById(R.id.c92_tabhost);
	}

	private void initListener() {
		addToShelf.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Save image into sdCard
				saveImage2SdCard();
				opFav.insertIntoBookList(bookinfo);
				Intent intent = new Intent();
				intent.putExtra("bookinfo", bookinfo);
				intent.setClass(SearchBookInfoActivity.this, MainActivity.class);
				SearchBookInfoActivity.this.startActivity(intent);
				SearchBookInfoActivity.this.finish();
			}
		});
		b2cListView.setOnItemClickListener((new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				buyurl = (String) priceList.get(arg2).get("MarketLink");
				intent.putExtra("buyurl", buyurl);
				intent.setClass(SearchBookInfoActivity.this, BuyBookActivity.class);
				SearchBookInfoActivity.this.startActivity(intent);
			}
		}));
	}

	private void downBookPhoto(String bookImageURls) {
		if (bookImageURls != null) {
			JSONObject resultJson = JSONObject.parseObject(bookImageURls);
			// 取中间像素的图片
			String bookImageURl = resultJson.getString("medium");
			if (bookImageURl != null) {
				ImageManager2.from().displayImage(bookImage, bookImageURl, -1);
			}
		}
	}

	public Bitmap getBitmapFromURL(String src) {
		Bitmap bitmap = null;
		try {
			JSONObject resultJson = JSONObject.parseObject(src);
			// 取中间像素的图片
			String middle = resultJson.getString("medium");
			byte[] byteArray = PicUtil.loadImageFromNetwork(middle);
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Exception", e.getMessage());
		}
		return bitmap;
	}

	/* 获取比价信息 */
	private void getResultBySubject() {
		
		try {
			String response = HttpUtil.getMethod(BookbookApiUtil.getBookPriceApiHost(bookinfo.getDoubanId()));
			priceList = new ArrayList<Map<String, Object>>();
			String regex = "([京东|亚|当])(.*?)</a>([\\s\\S]*?) href=\"(.*?)\">(.*?)</a>";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(response.toString());
			String val = null;
			while (m.find()) {
				val = m.group();
				Map<String, Object> map = new HashMap<String, Object>();

				if ((m.group(1) + m.group(2)).equals("京东商城")) {
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
				//Log.e("Url", URLDecoder.decode(m.group(4), "UTF-8"));
				String tempUrl = URLDecoder.decode(m.group(4), "UTF-8");
				String regexParttern ="http://www.douban.com/([\\s\\S]*?)url=(.*)";
				Pattern p1 = Pattern.compile(regexParttern);
				Matcher m1 = p1.matcher(tempUrl);
				while(m1.find()){
					String tempBookUrl=m1.group(2);
					String bookUrl=tempBookUrl;
					if(tempBookUrl.contains("P-306226-0-s6021440")){
						bookUrl = tempBookUrl.replaceAll("P-306226", "p-314973");
					}
					if(tempBookUrl.contains("douban-23")){
						bookUrl = tempBookUrl.replaceAll("douban-23", "bookbook-23");
					}
					
					//to do jingdong
					
					map.put("MarketLink", bookUrl);
					Log.e("MarketLink", bookUrl);
				}
				priceList.add(map);
			}
			adapter = new SimpleAdapter(this, (List<Map<String, Object>>) priceList, R.layout.b2citem, new String[] { "PIC", "Price" }, new int[] { R.id.b2cImage, R.id.price });
			b2cListView.setAdapter(adapter);
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
			MediaStore.Images.Media.insertImage(getContentResolver(), filename.getAbsolutePath(), filename.getName(), filename.getName());
			Toast.makeText(getApplicationContext(), "File is Saved in  " + filename, 500).show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class GetBookInfoTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String ret;
			String isbn = params[0];
			String getUrl = BookbookApiUtil.getBookisbnApiHost() + isbn;
			try {
				ret = HttpUtil.getMethod(getUrl);
			} catch (Exception e) {
				ret = null;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			if (SearchBookInfoActivity.this == null || SearchBookInfoActivity.this.isFinishing()) {
				return;
			}
			if (result != null && result.length() > 150) {
				JSONObject resultJson = JSONObject.parseObject(result);
				dialog.dismiss();
				bookName.setText(resultJson.getString("title"));
				authorName.setText(resultJson.getString("author"));
				summary.setText(resultJson.getString("summary"));
				// load image
				downBookPhoto(resultJson.getString("images"));
				// set bookinfo
				bookinfo.setAuthor(resultJson.getString("author"));
				bookinfo.setBookName(resultJson.getString("title"));
				bookinfo.setUrl(BookbookApiUtil.getBookisbnApiHost() + ISBN);
				bookinfo.setISBN(ISBN);
				bookinfo.setSummary(resultJson.getString("summary"));
				bookinfo.setImageUrl(resultJson.getString("images"));
				bookinfo.setDoubanId(resultJson.getString("id"));
				getResultBySubject();

			} else {
				Toast.makeText(SearchBookInfoActivity.this, R.string.data_error, 500);
				SearchBookInfoActivity.this.finish();
			}

		}
	}

}
