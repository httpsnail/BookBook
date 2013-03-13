package com.android.bookbook.view.adapter;

import java.util.List;
import com.android.bookbook.R;
import com.android.bookbook.model.BookInfo;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookListAdapter extends ArrayAdapter<BookInfo> {
	/** 上下文关联对象 */
	private Context mActivity;
	private List<BookInfo> mListData;
	private ViewHolder holder = new ViewHolder();
	public BookListAdapter(Context activity, List<BookInfo> listData) {
		super(activity, R.string.no_data, listData);
		mActivity = activity;
		mListData = listData;
		Log.e("bookbook", "size: "+mListData.size());
	}

	/**
	 * 存放列表项控件句柄
	 */
	private class ViewHolder {
		public ImageView bookImage;
		public TextView bookTitle;
		public TextView bookAuthor;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e("bookbook", "entry getView");
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.book_list_item, null);
			holder = new ViewHolder();
			holder.bookImage = (ImageView) convertView.findViewById(R.id.bookImageView);
			holder.bookAuthor = (TextView) convertView.findViewById(R.id.authorNameTextView);
			holder.bookTitle = (TextView) convertView.findViewById(R.id.bookNameTextView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (mListData == null || position >= mListData.size())
			return convertView;
		BookInfo bookinfo = mListData.get(position);
		String imagePath = "/sdcard/shuji/images/"
				+ bookinfo.getISBN() + ".jpg";
		if(imagePath.length()>10){
			holder.bookImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));		
		}	
		holder.bookAuthor.setText(bookinfo.getAuthor());
		holder.bookTitle.setText(bookinfo.getBookName());
		return convertView;
	}

}
