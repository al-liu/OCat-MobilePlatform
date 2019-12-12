package com.lhc.ocat.android_demo.manager;

import android.content.Context;

import com.yanzhenjie.andserver.annotation.Config;
import com.yanzhenjie.andserver.framework.config.WebConfig;
import com.yanzhenjie.andserver.framework.website.StorageWebsite;


/**
 * @author lhc
 */
@Config
public class AppWebConfig implements WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
        delegate.addWebsite(new StorageWebsite(PackageManager.getActivePackageWebPath(context), "index.html"));
    }
}
