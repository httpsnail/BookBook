package com.android.bookbook.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.android.bookbook.BookbookApp;


public class AppCommonUtil {
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}


	

	public static String Html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		java.util.regex.Pattern p_html1;
		java.util.regex.Matcher m_html1;

		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			String regEx_html2 = "&nbsp;";
			String regEx_html3 = "&ldquo;";
			String regEx_html4 = "&rdquo;";
			String regEx_html5 = "&hellip;";
			String regEx_html6 = "&mdash;";
			String regEx_html7 = "&nbsp";
			String regEx_html8 = "&lt;";
			String regEx_html9 = "&gt;";
			String regEx_html10 = "<br/>";

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html2, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(" "); // 过滤html中的空格

			p_html1 = Pattern.compile(regEx_html3, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll("\""); // 过滤html中的"

			p_html1 = Pattern.compile(regEx_html4, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll("\""); // 过滤html中的"

			p_html1 = Pattern.compile(regEx_html5, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll("..."); // 过滤html中的....

			p_html1 = Pattern.compile(regEx_html6, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll("-"); // 过滤html中的-

			p_html1 = Pattern.compile(regEx_html7, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(" "); // 过滤html中的&nbsp

			p_html1 = Pattern.compile(regEx_html8, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll("<"); // 过滤html中的&lt

			p_html1 = Pattern.compile(regEx_html9, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(">"); // 过滤html中的&gt

			p_html1 = Pattern.compile(regEx_html10, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll("\n"); // 过滤html中的&gt

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;// 返回文本字符串
	}

	/**
	 * return the phone network is available or not
	 * 
	 * @param context
	 * @return available true, not available false
	 */
	public static Boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo info[] = manager.getAllNetworkInfo();
		if (info == null) {
			return false;
		}
		for (int i = 0; i < info.length; i++) {
			if (info[i].getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get the login state
	 * 
	 * @return login return true, logout return false
	 */
/*	public static Boolean isLogin() {
		String sUserName = ModelManager.getUserStatesModel().getUserState(UserStatesModel.USER_STATE_USERNAME);
		String sPassword = ModelManager.getUserStatesModel().getUserState(UserStatesModel.USER_STATE_PASSWORD);
		// password length must more 6
		return sUserName != null && sUserName.length() > 0 && sPassword != null && sPassword.length() >= 6;
	}*/

	/** 注销用户 */
/*	public static void logout() {
		// ModelManager.getUserStatesModel().setUserState(UserStatesModel.USER_STATE_USERNAME, "");
		ModelManager.getUserStatesModel().setUserState(UserStatesModel.USER_STATE_PASSWORD, "");
		ModelManager.getUserStatesModel().setUserState(UserStatesModel.USER_STATE_UID, "");
		ModelManager.getUserStatesModel().localizeUserStates();
	}*/

	/**
	 * 得到ip地址
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		String ret = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ret = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception ex) {
		}
		return ret;
	}

	public static String getNetIsWifiOr3G() {
		String ret = "";

		Context context = BookbookApp.getInstance();// 获取应用上下文
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();// 获取网络的连接情况
		if (activeNetInfo == null) {
			return "none";
		}
		if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			// 判断WIFI网
			ret = "WIFI";
		} else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			// 判断3G网
			ret = "2G3G";

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			//ret = NetworkUtils.getNetworkTypeName(tm.getNetworkType());
			BookbookApp.getInstance().networkTypeString = ret;
		}

		return ret;
	}


	private static HttpClient httpClient;

	/**
	 * 判断网络是否可以Ping通URL
	 * 
	 * 
	 * */
	public static Boolean isConnectionTimeout() {

		boolean reachable = false;

		HttpParams params = new BasicHttpParams();
		// Turn off stale checking. Our connections break all the time
		// anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		// Default connection and socket timeout of 20 seconds. Tweak to
		// taste.
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 3000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// Don't handle redirects -- return them to the caller. Our code
		// often wants to re-POST after a redirect, which we must do
		// ourselves.

		// DOES WE NEED REDIRECTING?
		HttpClientParams.setRedirecting(params, true);

		// Set the specified user agent and register standard protocols.
		HttpProtocolParams.setUserAgent(params, HttpUtil.class.getName());
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);

		if (httpClient == null)
			httpClient = new DefaultHttpClient(manager, params);

		HttpPost method = new HttpPost("http://api.douban.com/v2/book/");
		try {
			httpClient.execute(method);
			reachable = true;
		} catch (Exception e) {
			e.printStackTrace();
			reachable = false;
		}

		return reachable;
	}

}
