package com.android.bookbook.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;


public class PicUtil {
	private static PicUtil _instance;

	public static void initialize() {
		if (_instance == null) {
			_instance = new PicUtil();
		}
	}

	private PicUtil() {

	}


	public static byte[] loadImageFromNetwork(String url) throws ClientProtocolException, IOException {
		HttpGet method = new HttpGet(url);
		HttpResponse response = HttpUtil.getHttpClient().execute(method);
		HttpEntity entity = response.getEntity();
		BufferedInputStream in = new BufferedInputStream(entity.getContent());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		while (true) {
			int len = in.read(buf);
			if (len < 0) {
				break;
			}
			out.write(buf, 0, len);
		}
		out.close();
		in.close();
		return out.toByteArray();
		// return BitmapFactory.decodeStream(entity.getContent());
	}
}