package com.android.bookbook.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.bookbook.R;
import com.android.bookbook.database.OperateFavor;
import com.android.bookbook.model.BookInfo;
import com.android.bookbook.view.adapter.BookListAdapter;

public class BookShelfActivity extends Activity implements Serializable{
	private static final long serialVersionUID = -6128857648045533709L;
	ListView listview;
	BookListAdapter adapter;
	BookInfo bookinfo;
	List<BookInfo> listData = new ArrayList<BookInfo>();
	OperateFavor opfavor = new OperateFavor();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry);
		findViewById();
		initLinster();
		LoadBookListData();
	}
	private void findViewById() {
		listview = (ListView) findViewById(R.id.book_listview);
	}

	private void initLinster() {

		/**
		 * click one item listener
		 * */
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				bookinfo = listData.get(position);
				Intent intent = new Intent(BookShelfActivity.this, ShowBook.class);
				intent.putExtra("bookinfo", bookinfo);
				BookShelfActivity.this.startActivity(intent);
			}
		});
		/**
		 * long pressed the book item,and choice to delete or not.
		 * */
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				new AlertDialog.Builder(BookShelfActivity.this).setIcon(android.R.drawable.ic_dialog_info).setMessage(R.string.sure_delete)
						.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								int mListPos = info.position;
								BookInfo map = listData.get(mListPos);
								int id = map.getId();
								if (id != 0) {
									opfavor.delteteBook("bookinfos", "id", String.valueOf(id));
									listData.remove(mListPos);
									adapter.notifyDataSetChanged();
								}
							}
						}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
							}
						}).show();
			}

		});
	}

	private void LoadBookListData() {
		List<BookInfo> favor_book_list = opfavor.queryBookList();
		listData.clear();
		listData.addAll(favor_book_list);
		adapter = new BookListAdapter(this, listData);
		listview.setAdapter(adapter);
	}


}
