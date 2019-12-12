package com.lhc.ocat.android_demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.lhc.ocat.android_demo.manager.PackageManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String PRE_PACKAGE_VERSION = "1.0.0";

    private WebView mWebView;
    private Button loadButton;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager
                .sharedInstance()
                .startUp(PRE_PACKAGE_VERSION, MainActivity.this);

        mWebView = findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        loadButton = findViewById(R.id.loadTest);
        loadButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadTest:
                mWebView.loadUrl("http://localhost:8887");
                break;
        }
    }
}
