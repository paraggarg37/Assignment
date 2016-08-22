package com.limetray.limetrayassignment.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.limetray.limetrayassignment.Interfaces.ExpenseData;
import com.limetray.limetrayassignment.MainActivity;
import com.limetray.limetrayassignment.Models.Expenses;
import com.limetray.limetrayassignment.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PG on 21/08/16.
 */
public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.MyViewHolder> {

    private ArrayList<Expenses.data> expensesList;
    private HashMap<String,Expenses.data> dataMap;

    private static String filterBy = MainActivity.CONSTANT_ALL;

    private Context mContext = null;

    public static final String CONSTANT_VERIFIED = "VERIFIED";
    public static final String CONSTANT_UNVERIFIED = "UNVERIFIED";
    public static final String CONSTANT_FRAUD = "FRAUD";

    public static final int CONSTANT_VERIFY_MESSAGE=R.string.verified_msg;
    public static final int CONSTANT_FRAUD_MESSAGE=R.string.fraud_msg;
    public static final int CONSTANT_UN_VERIFY_MESSAGE=R.string.unverified_msg;

    public static final int verifiedIcon = R.drawable.ic_check_black_24dp;
    public static final int fraudIcon = R.drawable.ic_block_black_24dp;

    public static final int verifiedColor = R.color.verifiedColor;
    public static final int fraudColor = R.color.blockedColor;
    public static final int defaultColor = R.color.defaultColor;



    protected ExpenseData expenseDataCallback;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jsonblob_list_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Expenses.data data = expensesList.get(position);
        holder.category.setText(data.getCategory());
        holder.description.setText(data.getDescription());
        holder.amount.setText(data.getAmount());
        holder.time.setText(data.getTime());

        holder.verified.setImageDrawable(getVerifiedDrawable(data.getState()));
        holder.block.setImageDrawable(getFraudDrawable(data.getState()));
    }

    public Drawable getVerifiedDrawable(String state) {

        if (state.contentEquals(CONSTANT_VERIFIED)) {
            return getTintedDrawable(verifiedIcon, verifiedColor);
        }
        return getTintedDrawable(verifiedIcon, defaultColor);
    }

    public Drawable getFraudDrawable(String state) {
        if (state.contentEquals(CONSTANT_FRAUD)) {
            return getTintedDrawable(fraudIcon, fraudColor);
        }
        return getTintedDrawable(fraudIcon, defaultColor);
    }

    public Drawable getTintedDrawable(@DrawableRes int drawableResId, @ColorRes int colorResId) {
        Resources res = mContext.getResources();
        Drawable drawable = res.getDrawable(drawableResId);
        int color = res.getColor(colorResId);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }


    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public ExpensesAdapter(ArrayList<Expenses.data> data, Context context,ExpenseData callback) {
        this.expensesList = (ArrayList<Expenses.data>) data.clone();
        this.mContext = context;
        this.expenseDataCallback = callback;

        dataMap = new HashMap<>();
        for(Expenses.data d : expensesList){
            dataMap.put(d.getId(),d);
        }

        filter(filterBy);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView category, description, time, amount;
        public ImageButton verified, block;

        public MyViewHolder(View view) {
            super(view);
            category = (TextView) view.findViewById(R.id.category);
            description = (TextView) view.findViewById(R.id.description);
            amount = (TextView) view.findViewById(R.id.amount);
            time = (TextView) view.findViewById(R.id.time);
            verified = (ImageButton) view.findViewById(R.id.verified);
            verified.setTag(this);
            block = (ImageButton) view.findViewById(R.id.block);
            block.setTag(this);
            verified.setOnClickListener(this);
            block.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();

            Expenses.data data = expensesList.get(pos);


            switch (v.getId()) {
                case R.id.verified:
                    toggleVerify(data);
                    break;
                case R.id.block:
                    toggleBlock(data);
                    break;
                default:
                    break;

            }



            verified.setImageDrawable(getVerifiedDrawable(data.getState()));
            block.setImageDrawable(getFraudDrawable(data.getState()));
            time.setText(data.getTime());

            updateDataToServer();

        }
    }

    public void updateDataToServer(){

        HashMap<String,Expenses.data> temp = new HashMap<>();

        for(Expenses.data d : expensesList){
            temp.put(d.getId(),d);
        }

        for (HashMap.Entry<String, Expenses.data> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            Expenses.data value = entry.getValue();
            if(!temp.containsKey(key)){
                temp.put(key,value);
            }
        }

        dataMap = (HashMap<String, Expenses.data>) temp.clone();

        ArrayList<Expenses.data> tmpArrayList = new ArrayList<>();

        for (HashMap.Entry<String, Expenses.data> entry : dataMap.entrySet()) {
            Expenses.data value = entry.getValue();
            tmpArrayList.add(value);
        }

        expenseDataCallback.onUpdateExpense(tmpArrayList);
    }


    public void toggleVerify(Expenses.data data) {

        if (data.getState().contentEquals(CONSTANT_VERIFIED)) {
            data.setState(CONSTANT_UNVERIFIED);
            Toast.makeText(mContext,mContext.getString(CONSTANT_UN_VERIFY_MESSAGE),Toast.LENGTH_SHORT).show();
        } else {
            data.setState(CONSTANT_VERIFIED);
            Toast.makeText(mContext,mContext.getString(CONSTANT_VERIFY_MESSAGE),Toast.LENGTH_SHORT).show();
        }

    }

    public void filter(String f){
        filterBy = f;
        expensesList.clear();

        for (HashMap.Entry<String, Expenses.data> entry : dataMap.entrySet()) {
            //String key = entry.getKey();
            Expenses.data value = entry.getValue();
            if(f.contentEquals(MainActivity.CONSTANT_ALL) || value.getCategory().equalsIgnoreCase(f)){
                expensesList.add(value);
            }
        }


        notifyDataSetChanged();
    }

    public void toggleBlock(Expenses.data data) {

        if (data.getState().contentEquals(CONSTANT_FRAUD)) {
            data.setState(CONSTANT_UNVERIFIED);
            Toast.makeText(mContext,mContext.getString(CONSTANT_UN_VERIFY_MESSAGE),Toast.LENGTH_SHORT).show();
        } else {
            data.setState(CONSTANT_FRAUD);
            Toast.makeText(mContext,mContext.getString(CONSTANT_FRAUD_MESSAGE),Toast.LENGTH_SHORT).show();
        }

    }


}
