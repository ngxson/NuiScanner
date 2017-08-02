package com.example.xson_laptop.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    LinearLayout resLayout;
    TextView vehople;
    TextView masove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resLayout = (LinearLayout) findViewById(R.id.showResult);
        vehople =(TextView) findViewById(R.id.vehople);
        masove = (TextView) findViewById(R.id.masove);

        String vehopleTxt = getIntent().getStringExtra("vehopleTxt");
        String masoveTxt = getIntent().getStringExtra("masoveTxt");

        if (vehopleTxt.contains("Xin ch"))
            resLayout.setBackgroundColor(0xffddffdd);
        else
            resLayout.setBackgroundColor(0xffffdddd);

        vehople.setText(vehopleTxt);
        masove.setText(masoveTxt);
    }

    public void okaybtn(View v) {
        //ScannerActivity.calledResult = false;
        finish();
    }
}
