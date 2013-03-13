package com.android.bookbook;

import java.lang.reflect.Method;
import java.net.URLEncoder;

import com.android.bookbook.util.HttpUtil;
import com.android.bookbook.util.PicUtil;
import com.android.bookbook.util.SharedPreferencesUtil;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;


public class BookbookApp extends Application  {

	// 手机信息
	public String app;
	public String device;
	public String macId;
	public String ver;
	public String finalVer;
	public String mobile;
	public String system;
	public String description;
	public String myIMSI;
	public int versionCode;
	public String promotion = "b00";// 渠道编号
	private final int NOTIC_ID = 123;
	public String title;
	public String message;
	public String uMeng;
	public String networkTypeString;

	private static BookbookApp _instance;


	private NotificationManager notificationManager;

	public SharedPreferencesUtil sharedPreferencesUtil;

	public static BookbookApp getInstance() {
		if (_instance == null) {
			_instance = new BookbookApp();
		}
		return _instance;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		_instance = this;

		if (notificationManager != null) {
			notificationManager.cancel(NOTIC_ID);
		}

		//initUtil();// 初始化工具类

		//initOtherVars();// 初始化一般变量

		//new InitDatasInThread().execute();
	}


	private void initUtil() {
		sharedPreferencesUtil = new SharedPreferencesUtil();
	/*	DbUtil.initialize();
		HaozuApiUtil.initialize();*/
		HttpUtil.initialize();
		PicUtil.initialize();
	}


	private class InitDatasInThread extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			if (!sharedPreferencesUtil.getString("firstTime").equals("firstTime")) {
				// 从assets文件中写回sharedPerference
				sharedPreferencesUtil.saveBoolean("cityListDataFileIsAvaliable", true);
				sharedPreferencesUtil.saveString("firstTime", "firstTime");
			}

			return null;
		}


	}

	private void initOtherVars() {
		// DEBUG = MapViewUtilsBaidu.isDebugBuild(this);
		BookbookApp.getInstance().app = "BookBook";
		BookbookApp.getInstance().mobile = "Android-" + URLEncoder.encode(android.os.Build.MODEL);
		BookbookApp.getInstance().system = URLEncoder.encode(android.os.Build.VERSION.RELEASE);

		BookbookApp.getInstance().description = URLEncoder.encode(getBuildDescription());
		PackageManager manager = getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		BookbookApp.getInstance().ver = URLEncoder.encode(info.versionName);
		BookbookApp.getInstance().versionCode = info.versionCode;
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		BookbookApp.getInstance().myIMSI = mTelephonyMgr.getSubscriberId();
		BookbookApp.getInstance().device = mTelephonyMgr.getDeviceId();
		BookbookApp.getInstance().macId = getLocalMacAddress();
	}

	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wifi == null) {
			return "";
		}
		WifiInfo info = wifi.getConnectionInfo();
		if (info == null) {
			return "";
		}
		return info.getMacAddress();
	}

	public void setLastmodifyTime(String time) {
		sharedPreferencesUtil.saveString("lastModifyTime", time);
	}

	public String getLastModifyTime() {
		String lastModifyTime = sharedPreferencesUtil.getString("lastModifyTime");
		if (lastModifyTime == null || lastModifyTime.length() == 0) {
			return String.valueOf(0);
		}
		return lastModifyTime;
	}

	private String getBuildDescription() {
		String desc = "unknown";
		try {
			Class<?> clazz = Class.forName("android.os.Build");
			Class<?> paraTypes = Class.forName("java.lang.String");
			Method method = clazz.getDeclaredMethod("getString", paraTypes);
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			Build b = new Build();
			desc = (String) method.invoke(b, "ro.build.description");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return desc;
	}
	
	public void setVersion(String verName){
		this.ver = verName;
	}
	
	public void setDefaultVersion(){
		this.ver = finalVer;
	}

}