package com.android.bookbook.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;

import com.android.bookbook.BookbookApp;
import com.android.bookbook.BookbookStaticValue;

public class SharedPreferencesUtil {
	private SharedPreferences _settings;

	public SharedPreferencesUtil() {
		_settings = BookbookApp.getInstance().getSharedPreferences(BookbookStaticValue.BOOKBOOK_DATA, 0);
	}

	public void saveString(String key, String value) {
		_settings.edit().putString(key, value).commit();
	}

	public String getString(String key) {
		return _settings.getString(key, "");
	}

	public void saveInt(String key, int value) {
		_settings.edit().putInt(key, value).commit();
	}

	public int getInt(String key, int defaultValue) {
		return _settings.getInt(key, defaultValue);
	}

	public Double getOptDouble(String key) {
		String retStr = _settings.getString(key, null);
		Double ret = null;
		try {
			ret = Double.parseDouble(retStr);
		} catch (Exception e) {
		}
		return ret;
	}

	public Boolean getOptBoolean(String key) {
		String retStr = _settings.getString(key, null);
		Boolean ret = null;
		try {
			ret = Boolean.parseBoolean(retStr);
		} catch (Exception e) {
		}
		return ret;
	}

	// 公积金
	public Double getDouble(String key) {
		String retStr = _settings.getString(key, null);
		Double ret = null;
		try {
			if (retStr != null) {
				ret = Double.parseDouble(retStr);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		return ret;
	}

	public void saveHashMap(final String key, HashMap<String, String> map) {
		final JSONObject ret = new JSONObject(map);
		_settings.edit().putString(key, ret.toString()).commit();
	}

	public HashMap<String, String> getHashMapByKey(String key) {
		HashMap<String, String> ret = new HashMap<String, String>();
		String mapStr = _settings.getString(key, "{}");
		JSONObject mapJson = null;
		try {
			mapJson = new JSONObject(mapStr);
		} catch (Exception e) {
			return ret;
		}

		if (mapJson != null) {
			Iterator<String> it = mapJson.keys();
			while (it.hasNext()) {
				String theKey = it.next();
				String theValue = mapJson.optString(theKey);
				ret.put(theKey, theValue);
			}
		}
		return ret;
	}

	public void saveBoolean(String key, boolean bool) {
		_settings.edit().putBoolean(key, bool).commit();
	}

	public boolean getBoolean(String key) {
		return _settings.getBoolean(key, false);
	}

	public void saveJSONArray(String key, JSONArray ret) {
		if (ret == null) {
			return;
		}
		String value = "";
		try {
			value = ret.toString();
		} catch (OutOfMemoryError o) {
			ret = null;
			value = "";
		}
		_settings.edit().putString(key, value).commit();
	}

	public JSONArray getJSONArray(String key) {
		String listStr = _settings.getString(key, "{}");
		JSONArray listJson = null;
		try {
			listJson = new JSONArray(listStr);
		} catch (Exception e) {
			return listJson;
		}
		return listJson;
	}

	public void saveArrayList(String key, ArrayList<String> list) {
		JSONArray ret = new JSONArray(list);
		_settings.edit().putString(key, ret.toString()).commit();
	}

	public ArrayList<String> getArrayList(String key) {
		ArrayList<String> ret = new ArrayList<String>();
		String listStr = _settings.getString(key, "{}");
		JSONArray listJson = null;
		try {
			listJson = new JSONArray(listStr);
		} catch (Exception e) {
			return ret;
		}

		if (listJson != null) {
			for (int i = 0; i < listJson.length(); i++) {
				String temp = listJson.optString(i);
				ret.add(temp);
			}
		}
		return ret;
	}

	public void removeByKey(String key) {
		_settings.edit().remove(key).commit();
	}
}
