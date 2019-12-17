package com.lhc.ocat.android_demo.manager;

/**
 * Created by lhc on 2019/12/16.
 */

public class PackageSettings {

    private String appId;
    private String appSecret;
    private String serverBaseUrl;
    private String inbuiltPackageVersion;

    public PackageSettings(String appId, String appSecret, String serverBaseUrl, String inbuiltPackageVersion) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.serverBaseUrl = serverBaseUrl;
        this.inbuiltPackageVersion = inbuiltPackageVersion;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getServerBaseUrl() {
        return serverBaseUrl;
    }

    public String getInbuiltPackageVersion() {
        return inbuiltPackageVersion;
    }

    @Override
    public String toString() {
        return "PackageSettings{" +
                "appId='" + appId + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", serverBaseUrl='" + serverBaseUrl + '\'' +
                ", inbuiltPackageVersion='" + inbuiltPackageVersion + '\'' +
                '}';
    }
}
