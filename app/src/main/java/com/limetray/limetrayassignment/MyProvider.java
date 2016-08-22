package com.limetray.limetrayassignment;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by PG on 23/08/16.
 */
public class MyProvider extends ContentProvider {
    public static final String PROVIDER_NAME= "com.example.android.datasync.provider";
    static final String URL = "content://" + PROVIDER_NAME + "/data";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static String lastSyncData = null;
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    public String getLastSyncData() {
        return lastSyncData;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        lastSyncData = values.getAsString("data");
        System.out.println("insert called with values"+lastSyncData);

        SharedPreferences.Editor editor = getContext().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("data", lastSyncData);
        editor.commit();

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
