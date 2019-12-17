package com.lhc.ocat.android_demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lhc.ocat.android_demo.manager.PackageManager;
import com.lhc.ocat.android_demo.manager.PackageSettings;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String PRE_PACKAGE_VERSION = "1.0.0";

    private TextView inbuiltTextView, currentTextView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageSettings packageSettings = new PackageSettings(
                "8405288128",
                "c9663f262eb24fe3a859f0113c98efca",
                "http://localhost:8800",
                PRE_PACKAGE_VERSION);
        PackageManager.manageWithSettings(packageSettings).launch(MainActivity.this);

        inbuiltTextView = findViewById(R.id.inbuiltVersion);
        currentTextView = findViewById(R.id.currentVersion);
        Button seeOfflineButton = findViewById(R.id.seeOffline);
        Button seeOnlineButton = findViewById(R.id.seeOnline);
        Button updatePatchButton = findViewById(R.id.updatePatch);
        Button clearCacheButton = findViewById(R.id.clearCache);
        seeOfflineButton.setOnClickListener(this);
        seeOnlineButton.setOnClickListener(this);
        updatePatchButton.setOnClickListener(this);
        clearCacheButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seeOffline:
                break;
            case R.id.seeOnline:
                break;
            case R.id.updatePatch:
                PackageManager.sharedInstance().updateLatestPatch(MainActivity.this);
                break;
            case R.id.clearCache:
                break;
        }
    }
}
