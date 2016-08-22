package com.limetray.limetrayassignment.Fragments;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limetray.limetrayassignment.Api.JsonBlobApiClient;
import com.limetray.limetrayassignment.Interfaces.ExpenseData;
import com.limetray.limetrayassignment.Interfaces.RestCallback;
import com.limetray.limetrayassignment.Models.Expenses;
import com.limetray.limetrayassignment.MyProvider;
import com.limetray.limetrayassignment.R;


import java.util.ArrayList;

/**
 * Created by PG on 20/08/16.
 */
public class JsonBlobAPIFragment extends Fragment {

    public static final String JSON_BLOB_OBJECT_ID = "570de811e4b01190df5dafec";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private ExpenseData expenseDataFragment = null;
    private Expenses currentExpenses = null;
    boolean mReady = false;
    private Context appContext;
    private ContentObserver mObserver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retain the fragment
        setRetainInstance(true);
        ContentResolver mResolver;
        mResolver = getActivity().getContentResolver();
        appContext =  getActivity().getApplicationContext();

        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean selfChange) {
                // Do something.

                SharedPreferences prefs =appContext.getSharedPreferences("data", Context.MODE_PRIVATE);
                String restoredText = prefs.getString("data", null);
                Log.d("fr","data changed" +restoredText);
                if(restoredText!=null){
                    Gson gson = new GsonBuilder().setDateFormat(DATETIME_FORMAT).create();
                    Expenses p = gson.fromJson(restoredText, Expenses.class);
                    setCurrentExpenses(p);
                }

            }
        };
        mResolver.registerContentObserver(MyProvider.CONTENT_URI,true,mObserver);


    }

    public void setExpenseDataFragment(ExpenseData fm){
        expenseDataFragment = fm;
    }


    public void getJsonBlob() {
        JsonBlobApiClient.get(JSON_BLOB_OBJECT_ID, new RestCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("response "+response);
                Gson gson = new GsonBuilder().setDateFormat(DATETIME_FORMAT).create();
                Expenses p = gson.fromJson(response, Expenses.class);
                System.out.println(p.toString());
                setCurrentExpenses(p);
            }

            @Override
            public void onError(Exception e) {
                System.out.println("on get "+e.getCause());
                e.printStackTrace();
                showError();
            }
        });
    }

    public void showError(){
        expenseDataFragment.onError();
    }

    public void setCurrentExpenses(Expenses currentExpenses) {
        this.currentExpenses = currentExpenses;
        updateDataToActivity();
    }

    public void updateToServer(String jsonBody){

        JsonBlobApiClient.put(JSON_BLOB_OBJECT_ID,jsonBody,new RestCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("response on success  "+response);
                Gson gson = new GsonBuilder().setDateFormat(DATETIME_FORMAT).create();
                Expenses p = gson.fromJson(response, Expenses.class);
                System.out.println(p.toString());
                setCurrentExpenses(p);
            }

            @Override
            public void onError(Exception e) {
                System.out.println("on put "+e.getCause());
                e.printStackTrace();
                showError();
            }
        });
    }

    public ArrayList<Expenses.data> getCurrentExpenses() {
        if(currentExpenses == null) return null;
        return currentExpenses.getExpenses();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mReady = true;
        init();
    }

    public void init(){
        if(mReady){


            if(getCurrentExpenses()==null)
                getJsonBlob();
            updateDataToActivity();
        }
    }


    public void updateDataToActivity(){

        if(getCurrentExpenses()!=null && expenseDataFragment!=null && mReady){
            System.out.println("updating to activity");
            expenseDataFragment.onGetExpense(getCurrentExpenses());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mReady = false;
        setExpenseDataFragment(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
