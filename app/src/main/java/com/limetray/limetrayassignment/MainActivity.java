package com.limetray.limetrayassignment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.limetray.limetrayassignment.Fragments.JsonBlobListFragment;

public class MainActivity extends AppCompatActivity {

    public static final String CONSTANT_ALL = "all";
    public static final String CONSTANT_RENT = "rent";
    public static final String CONSTANT_RECHARGE = "recharge";

    protected JsonBlobListFragment container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        container = (JsonBlobListFragment)getSupportFragmentManager().findFragmentById(R.id.container);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void filter(String f){
        container.filter(f);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.all) {
            filter(CONSTANT_ALL);
            return true;
        }else if(id == R.id.recharge){
            filter(CONSTANT_RECHARGE);
            return true;
        }
        else if(id == R.id.rent){
            filter(CONSTANT_RENT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
