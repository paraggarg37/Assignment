package com.limetray.limetrayassignment.Fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limetray.limetrayassignment.Adapters.ExpensesAdapter;
import com.limetray.limetrayassignment.Interfaces.ExpenseData;
import com.limetray.limetrayassignment.Models.Expenses;
import com.limetray.limetrayassignment.MyProvider;
import com.limetray.limetrayassignment.R;

import java.util.ArrayList;

/**
 * Created by PG on 20/08/16.
 */
public class JsonBlobListFragment extends Fragment implements ExpenseData {

    JsonBlobAPIFragment ApiFragment;
    private static final String API_FRAGMENT_TAG_NAME = "api";


    protected RecyclerView mRecyclerView;
    protected ExpensesAdapter mAdapter;
    protected ArrayList<Expenses.data> mExpenses;
    protected ProgressBar mProgressBar;
    protected TextView mEmptyView;


    public static final String AUTHORITY = "com.example.android.datasync.provider";
    // Account
    public static final String ACCOUNT = "default_account";
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 1L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;

    final String ACCOUNT_NAME = "MyApp";
    final String ACCOUNT_TYPE = "com.myapp.account";
    final String PROVIDER = "com.myapp.provider";




    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jsonblob_list_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_view);
        mEmptyView = (TextView)view.findViewById(R.id.empty);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        ApiFragment = (JsonBlobAPIFragment) fm.findFragmentByTag(API_FRAGMENT_TAG_NAME);


        if (ApiFragment == null) {
            ApiFragment = new JsonBlobAPIFragment();
            ApiFragment.setTargetFragment(this, 0);
            fm.beginTransaction().add(ApiFragment, API_FRAGMENT_TAG_NAME).commit();
        }

        if(mExpenses == null){
            mEmptyView.setVisibility(View.VISIBLE);
            showProgress(true);
        }


        ApiFragment.setExpenseDataFragment(this);

        Account appAccount = new Account(ACCOUNT_NAME,ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(getActivity().getApplicationContext());
        if (accountManager.addAccountExplicitly(appAccount, null, null)) {
            ContentResolver.setIsSyncable(appAccount, PROVIDER, 1);
            ContentResolver.setMasterSyncAutomatically(true);
            ContentResolver.setSyncAutomatically(appAccount, PROVIDER, true);
        }

        mResolver = getActivity().getContentResolver();



        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(appAccount, AUTHORITY, settingsBundle);


        mResolver.setIsSyncable(appAccount, AUTHORITY, 1);
        mResolver.setSyncAutomatically(appAccount, AUTHORITY, true);

        mResolver.addPeriodicSync(
                appAccount,
                AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);


    }




    @Override
    public void onGetExpense(ArrayList<Expenses.data> mExpensesData) {
        this.mExpenses = mExpensesData;

        mAdapter = new ExpensesAdapter(mExpenses, getActivity().getApplicationContext(), this);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgress(false);
                mEmptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    public void showProgress(boolean val) {
        if (val)
            mProgressBar.setVisibility(View.VISIBLE);
        else
            mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void onUpdateExpense(ArrayList<Expenses.data> mExpensesData) {

        showProgress(true);

        Expenses expenses = new Expenses();
        expenses.setExpenses(mExpensesData);
        System.out.println("changed json ");
        System.out.println(expenses.toString());

        Gson gson = new GsonBuilder().setDateFormat(JsonBlobAPIFragment.DATETIME_FORMAT).create();
        String jsonBody = gson.toJson(expenses);
        System.out.println(jsonBody);

        ApiFragment.updateToServer(jsonBody);

    }

    public void filter(String f){
        if(mAdapter!=null){
            mAdapter.filter(f);
        }
    }

    @Override
    public void onError() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),getResources().getString(R.string.error_message),Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiFragment.setExpenseDataFragment(null);
    }
}
