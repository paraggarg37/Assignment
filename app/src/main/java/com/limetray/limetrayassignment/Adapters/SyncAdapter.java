package com.limetray.limetrayassignment.Adapters;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limetray.limetrayassignment.Api.JsonBlobApiClient;
import com.limetray.limetrayassignment.Fragments.JsonBlobAPIFragment;
import com.limetray.limetrayassignment.Interfaces.RestCallback;
import com.limetray.limetrayassignment.Models.Expenses;
import com.limetray.limetrayassignment.MyProvider;

/**
 * Created by PG on 22/08/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver mContentResolver;
    Context context;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, final ContentProviderClient provider, SyncResult syncResult) {


        System.out.println("Perform sync called");
        Log.d("syncadapter","called sync");

        JsonBlobApiClient.get(JsonBlobAPIFragment.JSON_BLOB_OBJECT_ID, new RestCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("response "+response);
                Gson gson = new GsonBuilder().setDateFormat(JsonBlobAPIFragment.DATETIME_FORMAT).create();
                Expenses p = gson.fromJson(response, Expenses.class);
                System.out.println(p);

                ContentValues contentValues = new ContentValues();
                contentValues.put("data", response);
                try {
                    provider.insert(MyProvider.CONTENT_URI, contentValues);
                    getContext().getContentResolver().notifyChange(MyProvider.CONTENT_URI, null, false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                System.out.println("on get "+e.getCause());
                e.printStackTrace();
            }
        });

    }
}
