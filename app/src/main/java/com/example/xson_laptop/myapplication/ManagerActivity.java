package com.example.xson_laptop.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ManagerActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        sharedPref = this.getSharedPreferences("nui", Context.MODE_PRIVATE);
    }

    public void xoadata(View v) {
        sharedPref.edit().putString("checkedCodes", "").commit();
    }
}
