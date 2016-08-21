package com.limetray.limetrayassignment.Interfaces;

import com.limetray.limetrayassignment.Models.Expenses;

import java.util.ArrayList;

/**
 * Created by PG on 21/08/16.
 */
public interface ExpenseData {

    public void onGetExpense(ArrayList<Expenses.data> mExpensesData);
    public void onUpdateExpense(ArrayList<Expenses.data> mExpensesData);
    public void onError();
}
