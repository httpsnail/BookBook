package com.android.bookbook.model;

import java.io.Serializable;

import android.graphics.Bitmap;

public class BookInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8568074413308677580L;
	private Integer id;
	private String bookName;
	private String author;
	private String url;
	private String ISBN;
	private Bitmap image;
	private String summary;
	private String imageUrl;
	private String doubanId;
	
	public String getDoubanId() {
		return doubanId;
	}

	public void setDoubanId(String doubanId) {
		this.doubanId = doubanId;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id){
		this.id = id;
	}
	public String getSummary() {
	    return summary;
	}
	public void setSummary(String summary) {
	    this.summary = summary;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public String getISBN() {
		return ISBN;
	}
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	

}
