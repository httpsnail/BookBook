package com.android.bookbook.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.android.bookbook.R;
import com.android.bookbook.database.DBbookHelper;
import com.android.bookbook.database.OperateFavor;
import com.android.bookbook.model.BookInfo;
import com.android.bookbook.view.adapter.BookListAdapter;

public class EntryActivity extends Activity implements Serializable {

	private static final long serialVersionUID = -6128857648045533709L;
	ListView listview;
	private Button addbook_btn;
	BookListAdapter adapter;
	BookInfo bookinfo;
	List<BookInfo> listData = new ArrayList<BookInfo>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry);
		findViewById();
		initLinster();
		//
		checkShowEmptyView();
	}

	private void findViewById() {
		addbook_btn = (Button) this.findViewById(R.id.addBook);
		listview = (ListView) findViewById(R.id.book_listview);
	}

	private void initLinster() {
		// Add button
		addbook_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new
				// Intent(EntryActivity.this,InputISBNActivity.class);
				Intent intent = new Intent(EntryActivity.this,
						CaptureActivity.class);
				EntryActivity.this.startActivity(intent);
			}

		});

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				bookinfo = listData.get(position);
				Intent intent = new Intent(EntryActivity.this, ShowBook.class);
				intent.putExtra("bookinfo", bookinfo);
				EntryActivity.this.startActivity(intent);

			}
		});
	}

	private void updateBookInfo() {
		/*
		 * DBbookHelper mDBHelper = new DBbookHelper(EntryActivity.this);
		 * listData.clear(); adapter.notifyDataSetChanged(); Cursor c =
		 * mDBHelper.getReadableDatabase().query("bookinfos", new String[] {
		 * "id", "name", "url", "author", "ISBN" }, null, null, null, null,
		 * null);
		 * 
		 * while (c.moveToNext()) { HashMap<String, Object> map = new
		 * HashMap<String, Object>(); String imagePath = "/sdcard/shuji/images/"
		 * + c.getString(c.getColumnIndex("ISBN")) + ".jpg"; map.put("icon",
		 * BitmapFactory.decodeFile(imagePath)); map.put("id", c.getString(0));
		 * map.put("title", c.getString(1)); map.put("author",
		 * c.getString(c.getColumnIndex("author"))); map.put("ISBN",
		 * c.getString(c.getColumnIndex("ISBN"))); // map.put("image",
		 * c.getString(3)); listData.add(map); } mDBHelper.close();
		 */
	}

	private void checkShowEmptyView() {

		OperateFavor opfavor = new OperateFavor();
		List<BookInfo> favor_book_list = opfavor.queryBookList();
		listData.clear();
		listData.addAll(favor_book_list);
		adapter = new BookListAdapter(this, listData);
		listview.setAdapter(adapter);
		longPressed();
	}

	private void longPressed() {
		// 处理长时间按住删除
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				new AlertDialog.Builder(EntryActivity.this)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setMessage(R.string.sure_delete)
						.setPositiveButton(R.string.sure,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
										int mListPos = info.position;
										BookInfo map = listData.get(mListPos);
										int id = map.getId();
										if (id != 0) {
											DBbookHelper mDBHelper = new DBbookHelper(
													EntryActivity.this);
											String[] args = { String
													.valueOf(id) };

											mDBHelper.getWritableDatabase()
													.delete("bookinfos",
															"id=?", args);
											checkShowEmptyView();
											mDBHelper.close();
											// updateBookInfo();
										}
									}
								})
						.setNegativeButton(R.string.cancel,
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
