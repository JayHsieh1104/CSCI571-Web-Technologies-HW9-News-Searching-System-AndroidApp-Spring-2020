package com.example.NewsApp;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;

public class JSONSharedPreferences {
    private static final String PREFIX = "jsonBookmark";
    private static final String prefName = "jsonBookmark";
//    public static void saveJSONObject(Context c, String prefName, String key, JSONObject object) {
//        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(JSONSharedPreferences.PREFIX+key, object.toString());
//        editor.commit();
//    }

    public static void saveJSONArray(Context c, JSONArray array) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(JSONSharedPreferences.PREFIX, array.toString());
        editor.commit();
    }

//    public static JSONObject loadJSONObject(Context c, String prefName, String key) throws JSONException {
//        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
//        return new JSONObject(settings.getString(JSONSharedPreferences.PREFIX+key, "{}"));
//    }

    public static JSONArray loadJSONArray(Context c) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return new JSONArray(settings.getString(JSONSharedPreferences.PREFIX, "[]"));
    }

    public static void clearJSONArray(Context c) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        if (settings.contains(JSONSharedPreferences.PREFIX)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(JSONSharedPreferences.PREFIX);
            editor.commit();
        }
    }

    public static void addNews(Context c, String newTitle, String newsTime, String newsSection, String newsID, String newsImageUrl) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        JSONArray mBookmarkJSONArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("newsTitle", newTitle);
            jsonObject.put("newsTime", newsTime);
            jsonObject.put("newsSection", newsSection);
            jsonObject.put("newsID", newsID);
            jsonObject.put("newsImageUrl", newsImageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (settings.contains(JSONSharedPreferences.PREFIX)) {
            mBookmarkJSONArray = loadJSONArray(c);
            mBookmarkJSONArray.put(jsonObject);
            clearJSONArray(c);
            saveJSONArray(c, mBookmarkJSONArray);
        }
        else {
            mBookmarkJSONArray.put(jsonObject);
            saveJSONArray(c, mBookmarkJSONArray);
        }
    }

    public static void removeNews(Context c, String newsID) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        JSONArray mBookmarkJSONArray = loadJSONArray(c);

        for (int i = 0; i < mBookmarkJSONArray.length(); i++) {
            JSONObject jsonObject = mBookmarkJSONArray.getJSONObject(i);
            if(jsonObject.getString("newsID").equals(newsID)) {
                mBookmarkJSONArray.remove(i);
                clearJSONArray(c);
                saveJSONArray(c, mBookmarkJSONArray);
            }
        }
    }

    public static boolean isEmpty(Context c) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        if (settings.contains(JSONSharedPreferences.PREFIX)) {
            JSONArray mBookmarkJSONArray = new JSONArray();
            mBookmarkJSONArray = loadJSONArray(c);
            if (mBookmarkJSONArray.length() == 0) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }

    public static boolean isExisted(Context c, String newsID) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        if (settings.contains(JSONSharedPreferences.PREFIX)) {
            JSONArray mBookmarkJSONArray = new JSONArray();
            mBookmarkJSONArray = loadJSONArray(c);
            for (int i = 0; i < mBookmarkJSONArray.length(); i++) {
                JSONObject jsonObject = mBookmarkJSONArray.getJSONObject(i);
                if (jsonObject.getString("newsID").equals(newsID)) {
                    return true;
                }
            }
        }
        return false;
    }
}