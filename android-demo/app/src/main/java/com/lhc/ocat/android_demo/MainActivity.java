package com.lhc.ocat.android_demo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lhc.ocat.android_demo.manager.Callback;
import com.lhc.ocat.android_demo.manager.PackageError;
import com.lhc.ocat.android_demo.manager.PackageManager;
import com.lhc.ocat.android_demo.manager.PackageSettings;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private static final String PRE_PACKAGE_VERSION = "1.0.0";
    private static final String ONLINE_URL = "http://192.168.1.118:8080";
    public static final String ACTIVITY_INTENT_URL_KEY = "ACTIVITY_INTENT_URL_KEY";

    private TextView currentTextView;
    private ProgressDialog progressDialog;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageSettings packageSettings = new PackageSettings(
                "8405288128",
                "c9663f262eb24fe3a859f0113c98efca",
                "http://192.168.1.118:8800",
                PRE_PACKAGE_VERSION);
        PackageManager.manageWithSettings(packageSettings).launch(MainActivity.this);

        TextView inbuiltTextView = findViewById(R.id.inbuiltVersion);
        currentTextView = findViewById(R.id.currentVersion);
        Button seeOfflineButton = findViewById(R.id.seeOffline);
        Button seeOnlineButton = findViewById(R.id.seeOnline);
        Button updatePatchButton = findViewById(R.id.updatePatch);
        Button clearCacheButton = findViewById(R.id.clearCache);
        seeOfflineButton.setOnClickListener(this);
        seeOnlineButton.setOnClickListener(this);
        updatePatchButton.setOnClickListener(this);
        clearCacheButton.setOnClickListener(this);
        inbuiltTextView.setText(String.format("内置版本:%s", packageSettings.getInbuiltPackageVersion()));
        currentTextView.setText(String.format("当前版本:%s", PackageManager.sharedInstance().getActivePackageVersion(MainActivity.this)));
        PackageManager.sharedInstance().setCallback(new PackageManagerCallback());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seeOffline:
                String offlineUrl = PackageManager.sharedInstance().getOfflineServerUrl();
                Intent offlineIntent = new Intent(MainActivity.this, WebActivity.class);
                offlineIntent.putExtra(ACTIVITY_INTENT_URL_KEY, offlineUrl);
                startActivity(offlineIntent);
                break;
            case R.id.seeOnline:
                Intent onlineIntent = new Intent(MainActivity.this, WebActivity.class);
                onlineIntent.putExtra(ACTIVITY_INTENT_URL_KEY, ONLINE_URL);
                startActivity(onlineIntent);
                break;
            case R.id.updatePatch:
                showLoading("正在更新补丁");
                PackageManager.sharedInstance().updateLatestPatch(MainActivity.this);
                break;
            case R.id.clearCache:
                break;
        }
    }

    private class PackageManagerCallback implements Callback {

        @Override
        public void onLaunchFinished(PackageManager packageManager) {
            Log.i(TAG, "onLaunchFinished");
            currentTextView.setText(String.format("当前版本:%s", packageManager.getActivePackageVersion(MainActivity.this)));
            Toast.makeText(MainActivity.this, "包管理器启动完成", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLaunchError(PackageManager packageManager, PackageError error) {
            Log.i(TAG, "onLaunchError:"+error.toString());
            Toast.makeText(MainActivity.this, "包管理器启动失败,"+error.getReason(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUpdateFinished(PackageManager packageManager) {
            Log.i(TAG, "onUpdateFinished");
            String version = packageManager.getActivePackageVersion(MainActivity.this);
            String text = String.format("当前版本:%s", version);
            hideLoading();
            Toast.makeText(MainActivity.this, "更新完成,"+text, Toast.LENGTH_SHORT).show();
            currentTextView.setText(text);
        }

        @Override
        public void onUpdateError(PackageManager packageManager, PackageError error) {
            Log.i(TAG, "onUpdateError:" + error.toString());
            Toast.makeText(MainActivity.this, "更新失败,"+error.getReason(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(String text) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage(text);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void hideLoading() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
