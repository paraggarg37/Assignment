package com.limetray.limetrayassignment.Interfaces;

/**
 * Created by PG on 21/08/16.
 */
public interface RestCallback {
    public void onSuccess(String response);
    public void onError(Exception e);
}
