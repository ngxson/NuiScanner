package com.example.xson_laptop.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class ScannerActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;
    SharedPreferences sharedPref;
    EditText manual_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        resultTextView = (TextView) findViewById(R.id.qrdecodertxt);
        manual_et = (EditText) findViewById(R.id.editText);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        sharedPref = this.getSharedPreferences("nui", Context.MODE_PRIVATE);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(4000L);

        if (sharedPref.getBoolean("camsau", true)) {
            qrCodeReaderView.setBackCamera();
        } else {
            qrCodeReaderView.setFrontCamera();
        }

        // Use this function to enable/disable Torch
        // qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        // qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        //qrCodeReaderView.setBackCamera();
        startScanner();
    }

    void startScanner() {
        qrCodeReaderView.startCamera();
        qrCodeReaderView.setQRDecodingEnabled(true);
    }

    public static boolean calledResult = false;
    public static String lastcheck = "";

    void showResult(String txt) {
        //resLayout.setVisibility(View.VISIBLE);
        if (txt.length() != 8) {
            openResult("Vé không hợp lệ #1", txt);
        } else {
            try {
                String code = "";
                if (txt.startsWith("H")) code = "1";
                else if (txt.startsWith("C")) code = "2";
                else if (txt.startsWith("A")) code = "3";
                else if (txt.startsWith("P")) code = "4";
                else if (txt.startsWith("E")) code = "5";
                else if (txt.startsWith("K")) code = "6";
                else if (txt.startsWith("B")) code = "7";
                else if (txt.startsWith("Z")) code = "8";
                else if (txt.startsWith("X")) code = "9";

                code = code+""+txt.charAt(1)+""+txt.charAt(2);
                String verify = txt.charAt(4)+""+txt.charAt(5)+""+txt.charAt(6);
                String db = sharedPref.getString("checkedCodes","");

                //Log.d("checkve", " == " + code+" - "+verify);

                long isCheckedTicket = isCheckedTicket(db, code);
                long now = System.currentTimeMillis();

                if (isCheckedTicket != 0) {
                    long interval = (now - isCheckedTicket)/1000;
                    openResult("Vé đã check-in từ\n"+
                            (interval/60)+"p"+(interval%60)+"s trước.", code);
                } else {
                    int check1 = Integer.parseInt(verify);
                    int check2 = Integer.parseInt(txt.charAt(7)+"");

                    //Log.d("checkve", (check1 == (1024-Integer.parseInt(code))?"1":"0") + " mmmmm");

                    if (check1 == (1024-Integer.parseInt(code)) &&
                            check2 == Integer.parseInt(code)%5 &&
                            Integer.parseInt(code) >= 101 &&
                            Integer.parseInt(code) <= 800) {
                        sharedPref.edit().putString("checkedCodes", db+code+"-"+now+",").commit();
                        if (!lastcheck.equals(code))
                            openResult("Xin chào!", code);
                        lastcheck = code;
                        //calledResult = false;
                    } else {
                        openResult("Vé không hợp lệ #3", txt);
                    }
                }
            } catch (Exception e) {
                openResult("Vé không hợp lệ #2", txt);
            }
        }
    }

    public void nhapthucong(View v) {
        String code = manual_et.getText().toString();
        manual_et.setText("");
        if (code != null && !code.equals("") && code.length() == 3) {
            String db = sharedPref.getString("checkedCodes","");
            long isCheckedTicket = isCheckedTicket(db, code);
            long now = System.currentTimeMillis();

            if (isCheckedTicket != 0) {
                long interval = (now - isCheckedTicket)/1000;
                openResult("Vé đã check-in từ\n"+
                        (interval/60)+"p"+(interval%60)+"s trước.", code);
            } else {
                if (Integer.parseInt(code) >= 101 &&
                        Integer.parseInt(code) <= 800) {
                    sharedPref.edit().putString("checkedCodes", db+code+"-"+now+",").commit();
                    if (!lastcheck.equals(code))
                        openResult("Xin chào!", code);
                    lastcheck = code;
                    //calledResult = false;
                } else {
                    openResult("Vé không hợp lệ #3", code);
                }
            }
        }
    }

    public long isCheckedTicket(String db, String code) {
        if (db.length() < 8) return 0;
        else {
            String t[] = db.split(",");
            for (int i=0 ; i<t.length ; i++) {
                if (t[i].contains(code)) {
                    String temp[] = t[i].split("-");
                    try {
                        return Long.parseLong(temp[1]);
                    } catch (Exception e) {
                        return 0;
                    }
                }
            }
            return 0;
        }
    }

    public void openResult(String vehopleTxt, String masoveTxt) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("vehopleTxt", vehopleTxt);
        intent.putExtra("masoveTxt", masoveTxt);
        startActivity(intent);
    }

    public void okaybtn(View v) {
        qrCodeReaderView.setQRDecodingEnabled(true);
        startScanner();
        calledResult = false;
        qrCodeReaderView.setQRDecodingEnabled(true);
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        resultTextView.setText(text);
        if (!calledResult) {
            showResult(text);
            qrCodeReaderView.stopCamera();
        }
        calledResult = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
        //calledResult = false;
        Handler handlerTimer = new Handler();
        handlerTimer.postDelayed(new Runnable(){
            public void run() {
                calledResult = false;
            }}, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
