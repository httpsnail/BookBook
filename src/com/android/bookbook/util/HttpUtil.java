package com.android.bookbook.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	private static HttpUtil _instance;
	private static HttpClient _httpClient;

	public static void initialize() {
		if (_instance == null) {
			_instance = new HttpUtil();
		}
	}

	private HttpUtil() {
	}
	
	public static String getMethod(String url) throws ClientProtocolException, IOException {
		HttpEntity entity = null;
		HttpGet method = new HttpGet(url);
		HttpResponse response = getHttpClient().execute(method);
		entity = response.getEntity();
		return EntityUtils.toString(entity);
	}
	
	public static InputStream getInputStreamMethod(String url) throws ClientProtocolException, IOException {
		HttpEntity entity = null;
		HttpGet method = new HttpGet(url);
		HttpResponse response = getHttpClient().execute(method);
		entity = response.getEntity();
		return entity.getContent();
	}

	public static String postMethod(String url, HashMap<String, String> params) throws ClientProtocolException, IOException {
		HttpPost method = new HttpPost(url);
		if (params.size() > 0) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			method.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		}
		HttpResponse response = getHttpClient().execute(method);
		return EntityUtils.toString(response.getEntity());
	}

	public synchronized static HttpClient getHttpClient() {
		if (_httpClient == null) {
			HttpParams params = new BasicHttpParams();

			// Turn off stale checking. Our connections break all the time
			// anyway,
			// and it's not worth it to pay the penalty of checking every time.
			HttpConnectionParams.setStaleCheckingEnabled(params, false);

			// Default connection and socket timeout of 20 seconds. Tweak to
			// taste.
			HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
			HttpConnectionParams.setSoTimeout(params, 20 * 1000);
			HttpConnectionParams.setSocketBufferSize(params, 8192);

			// Don't handle redirects -- return them to the caller. Our code
			// often wants to re-POST after a redirect, which we must do
			// ourselves.

			HttpClientParams.setRedirecting(params, true);

			// Set the specified user agent and register standard protocols.
			HttpProtocolParams.setUserAgent(params, HttpUtil.class.getName());
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));

			ClientConnectionManager manager = new ThreadSafeClientConnManager(
					params, schemeRegistry);

			_httpClient = new DefaultHttpClient(manager, params);
		}
		return _httpClient;
	}

	private static HttpResponse execute(HttpRequestBase method)
			throws ClientProtocolException, IOException {
		String url = method.getURI().toString();
		String type = "";
		String status = "";

		HttpResponse response = null;
		String apiStartTime = "";
		String apiEndTime = "";
		String ext = "";
		try {
			apiStartTime = DateUtil.formatTime(System.currentTimeMillis());
			response = getHttpClient().execute(method);
			apiEndTime = DateUtil.formatTime(System.currentTimeMillis());
			status = "succ";
		} catch (Exception e) {
			status = String.format("fail:%1$s", String.valueOf(e));
			return null;
		} finally {
			ext = String.format("%1$s,%2$s,%3$s,%4$s", status, apiStartTime,
					apiEndTime, type);
			System.out.println(ext);
		}
		return response;
	}

}
