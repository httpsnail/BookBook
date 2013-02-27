package com.android.bookbook.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.bookbook.R;
import com.android.bookbook.DBHelper.DBbookHelper;
import com.android.bookbook.model.BookInfo;
import com.android.bookbook.util.ImageSimpleAdapter;

public class EntryActivity extends Activity implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6128857648045533709L;
    // TextView record=null;
    ListView listview = null;
    SimpleAdapter adapter = null;
    BookInfo bookinfo;
    ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
    LayoutInflater inflater = null;
    LinearLayout lin = null;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.entry);
	
	inflater =LayoutInflater.from(this);
	lin=(LinearLayout) findViewById(R.id.LinearLayoutContainer);
	// record=(TextView)this.findViewById(R.id.recordDB);
	//listview = (ListView) this.findViewById(R.id.listview);
	Button button = (Button) this.findViewById(R.id.addBook);

	// Add button
	button.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		 //Intent intent = new Intent(EntryActivity.this,InputISBNActivity.class);
		Intent intent = new Intent(EntryActivity.this,CaptureActivity.class);
		EntryActivity.this.startActivity(intent);
	    }

	});

	adapter = new ImageSimpleAdapter(EntryActivity.this, listData,
		R.layout.message_list,
		new String[] { "icon", "title", "author" }, new int[] {
			R.id.ml_icon, R.id.ml_title, R.id.ml_short_content });
	
	checkShowEmptyView();
//	listview.setAdapter(adapter);
	//updateBookInfo();

	 


    }

    @Override
    protected void onRestart() {
	// TODO Auto-generated method stub
	super.onRestart();
	//updateBookInfo();

    }

    @Override
    protected void onStart() {
	// TODO Auto-generated method stub
	super.onStart();

//	boolean isExsit = getIntent().getBooleanExtra("isExist", false);
//	if (isExsit) {
//	    Toast.makeText(getApplicationContext(), "书籍已在书架中",
//		    Toast.LENGTH_LONG).show();
//	}
//	isExsit = false;

    }

    private void updateBookInfo() {
	DBbookHelper mDBHelper = new DBbookHelper(EntryActivity.this);
	listData.clear();
	adapter.notifyDataSetChanged();
	Cursor c = mDBHelper.getReadableDatabase().query("bookinfos",
		new String[] { "id", "name", "url", "author", "ISBN" }, null,
		null, null, null, null);

	while (c.moveToNext()) {
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    String imagePath = "/sdcard/shuji/images/"
		    + c.getString(c.getColumnIndex("ISBN")) + ".jpg";
	    map.put("icon", BitmapFactory.decodeFile(imagePath));
	    map.put("id", c.getString(0));
	    map.put("title", c.getString(1));
	    map.put("author", c.getString(c.getColumnIndex("author")));
	    map.put("ISBN", c.getString(c.getColumnIndex("ISBN")));
	    // map.put("image", c.getString(3));
	    listData.add(map);
	}
	mDBHelper.close();
    }

    private void checkShowEmptyView() {
	
	DBbookHelper mDBHelper = new DBbookHelper(EntryActivity.this);
	Cursor c = mDBHelper.getReadableDatabase().query("bookinfos",
		new String[] { "id", "name", "url", "author", "ISBN","summary"}, null,
		null, null, null, null);

	if (c.moveToNext()) {
	     c = mDBHelper.getReadableDatabase().query("bookinfos",
			new String[] { "id", "name", "url", "author", "ISBN","summary"}, null,
			null, null, null, null);
	    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.listview, null).findViewById(R.id.LinearLayoutListView);
	    listview = (ListView) layout.getChildAt(0);
	    listData.clear();
	    
	    adapter.notifyDataSetChanged();
	    while (c.moveToNext()) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String imagePath = "/sdcard/shuji/images/"
			+ c.getString(c.getColumnIndex("ISBN")) + ".jpg";
		map.put("icon", BitmapFactory.decodeFile(imagePath));
		map.put("id", c.getString(0));
		map.put("title", c.getString(1));
		map.put("author", c.getString(c.getColumnIndex("author")));
		map.put("ISBN", c.getString(c.getColumnIndex("ISBN")));
		map.put("summary", c.getString(c.getColumnIndex("summary")));
		// map.put("image", c.getString(3));
		listData.add(map);
	    }
	    listview.setAdapter(adapter);
		lin.removeAllViews();
	        lin.addView(layout);
	        showBookInfo();
	        longPressed();
	        

	} else {
	    
	    LinearLayout layout = (LinearLayout) inflater.inflate(
                    R.layout.initview, null).findViewById(R.id.LinearLayoutInitView);
	    Button button =(Button) layout.getChildAt(1);
	    button.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
		    // TODO Auto-generated method stub
		    	//Intent intent = new Intent(EntryActivity.this,InputISBNActivity.class);
			Intent intent = new Intent(EntryActivity.this,CaptureActivity.class);
			EntryActivity.this.startActivity(intent);
		    
		}
		
	    });
	    lin.removeAllViews();
            lin.addView(layout);
            mDBHelper.close();
	}
    }

    private void showBookInfo(){
	// 处理列表项的点击事件
	listview.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> av, View v, int position,
		    long id) {
		HashMap<String, Object> map = listData.get(position);

		bookinfo = new BookInfo();
		bookinfo.setAuthor(map.get("author").toString());
		bookinfo.setISBN(map.get("ISBN").toString());
		bookinfo.setBookName(map.get("title").toString());
		bookinfo.setSummary(map.get("summary").toString());
		// bookinfo.setImage((Bitmap) map.get("icon"));
		Intent intent = new Intent(EntryActivity.this, ShowBook.class);
		intent.putExtra("bookinfo", bookinfo);
		EntryActivity.this.startActivity(intent);

	    }
	});
    }
    private void longPressed(){
	//处理长时间按住删除
	listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
	    @Override
	    public void onCreateContextMenu(ContextMenu menu, View v,
		    ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		new AlertDialog.Builder(EntryActivity.this)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setMessage("确定删除")
			.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialoginterface,int i) {
					int mListPos = info.position;
					HashMap<String, Object> map = listData.get(mListPos);

					int id = Integer.valueOf((map.get("id")
						.toString()));

					if (id != 0) {
					    DBbookHelper mDBHelper = new DBbookHelper(EntryActivity.this);
					    System.out.println();
					    String[] args = { String.valueOf(id) };

					    mDBHelper.getWritableDatabase().delete("bookinfos","id=?", args);
					    checkShowEmptyView();
					    mDBHelper.close();
					    //updateBookInfo();
					}

				    }
				})
			.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
				    public void onClick(
					    DialogInterface dialoginterface,
					    int i) {

				    }
				}).show();

	    }

	});
    }
}
