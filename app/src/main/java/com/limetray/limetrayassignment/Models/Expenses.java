package com.limetray.limetrayassignment.Models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by PG on 20/08/16.
 */
public class Expenses {
    ArrayList<data> expenses;


    public ArrayList<data> getExpenses() {
        return expenses;
    }
    public Expenses(){}

    public void setExpenses(ArrayList<data> expenses) {
        this.expenses = expenses;
    }

    @Override
    public String toString() {

        String out =  "";
        for(data d : expenses){
            out+=d.toString()+" \n";
        }
        return out;
    }


    public class data{
        int amount;
        String category;
        String description;
        String id;
        String state;
        Date time;

        public String getTime() {
            String pattern = "yyyy-MM-dd HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(time);
            return date;
        }

        public String getAmount() {
            return amount+"";
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
            time = new Date();
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return "amount : "+amount+" category : "+category+" description : "+description+" id : "+id+" state : "+state+" time : "+time;
        }
    }
}




