package com.lhc.ocat.android_demo.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lhc
 */
public class PackageManager {

    private static final String TAG = "PackageManager";

    static final String ACTIVE_PACKAGE_VERSION_SP_KEY = "ActivePackageVersionKey";
    static final String SHARED_PREFERENCES_NAME = "OCatPackageManagerSP";
    static final String NONE = "NONE";

    static final String WWW_COMPOSE_PATH = "www";
    static final String PATCH_COMPOSE_PATH = "patch";
    private static final String ALL_PACKAGE_NAME = "all.zip";

    private Server mServer;
    private boolean isRestartWebServer;

    private PackageManager() {
    }

    private static class PackageManagerHolder {
        private static PackageManager INSTANCE = new PackageManager();
    }

    public static PackageManager sharedInstance() {
        return PackageManagerHolder.INSTANCE;
    }

    public void startUp(String prePackageVersion, Context context) {
        initWebCoreServer(context);
        // 取偏好设置中的激活版本号
        String activePackageVersion = FileUtils.getActivePackageVersion(context);
        String appWebPath = FileUtils.getWebPath(context);

        // 判断是否有激活版本号
        if (NONE.equals(activePackageVersion)) {
            // 使用预置包（首次打开）
            usePrePackage(appWebPath, prePackageVersion, context);
        } else {
            String activeVersionPath = FileUtils.getVersionWebPath(context, activePackageVersion);
            File activeVersionFile = new File(activeVersionPath);
            // 判断当前激活版本号的路径是否存在
            if (activeVersionFile.exists()) {
                // 起服务到激活版本
                startupWebServer();
            } else {
                // 异常情况：当前版本号存在，资源不存在，这种情况切到预置包应急。
                // 判断预置版本是否存在，如果存在切到预置版本
                String preVersionPath = FileUtils.getPaths(appWebPath, prePackageVersion);
                File preVersionFile = new File(preVersionPath);
                if (!preVersionFile.exists()) {
                    usePrePackage(appWebPath, prePackageVersion, context);
                }
                // 起服务到预置版本
                startupWebServer();
            }
        }
        // 查询补丁信息
        activePackageVersion = FileUtils.getActivePackageVersion(context);
        checkPatchResource(activePackageVersion, context);
    }

    private void usePrePackage(String appWebPath, String prePackageVersion, Context context) {
        // 复制 assert 预置压缩包文件
        String copyDestinationPath = FileUtils.getPaths(appWebPath, ALL_PACKAGE_NAME);
        String preAllPackagePath = "pre-package/" + ALL_PACKAGE_NAME;
        try {
            FileUtils.copyAssetsFile(preAllPackagePath,
                    context,
                    new File(copyDestinationPath));
        } catch (Exception e) {
            Log.e(TAG, "包管理中断，复制预置包失败！");
            return;
        }
        // 解压预置包文件
        try {
            FileUtils.unZipFolder(copyDestinationPath, appWebPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 保存版本号
        FileUtils.setActivePackageVersion(context, prePackageVersion);

    }

    private void checkPatchResource(final String activeVersion, final Context context) {
        String url = "http://192.168.1.108:8800/package/fetch";
        Map<String, Object> params = new HashMap<>();
        params.put("versionName", activeVersion);
        params.put("appId", "7385262242");
        params.put("appSecret", "1034722ea56c4198840d1ccf77de4cab");
        NetUtils.post(url, params, new NetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String code = jsonObject.getString("code");
                        if ("000000".equals(code)) {
                            JSONObject patchJsonObj = jsonObject.getJSONObject("data");
                            final String newVersion = patchJsonObj.getString("newVersion");
                            final String oldVersion = patchJsonObj.getString("oldVersion");
                            if (oldVersion.equals(newVersion)) {
                                // 不需要更新补丁
                            } else {
                                String patchUrl = patchJsonObj.getString("downloadUrl");
                                final JSONArray changeResourceArray = patchJsonObj.getJSONArray("changeResourceInfo");
                                final JSONArray removeResourceArray = patchJsonObj.getJSONArray("removeResourceInfo");
                                // 下载补丁
                                final String patchPath = FileUtils.getPatchPath(context);
                                final String patchName = newVersion + "_" + oldVersion + ".zip";
                                String patchZipPath = FileUtils.getPaths(patchPath, patchName);
                                File patchFilePath = new File(patchZipPath);
                                if (!patchFilePath.exists()) {
                                    if (!patchFilePath.getParentFile().exists()) {
                                        boolean mkdirsResult = patchFilePath.getParentFile().mkdirs();
                                        Log.i(TAG, "创建补丁文件夹是否成功:"+mkdirsResult);
                                    }
                                    try {
                                        boolean mkFileResult = patchFilePath.createNewFile();
                                        Log.i(TAG, "创建输入流的补丁文件是否成功:"+mkFileResult);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
//                                patchUrl = patchUrl.replace("localhost", "192.168.1.108");
                                NetUtils.download(patchUrl, patchFilePath, new NetUtils.DownloadCallback() {
                                    @Override
                                    public void onFinished(File destination) {
                                        // 下载成功
                                        Log.i(TAG, "补丁文件下载成功");
                                        try {
                                            FileUtils.unZipFolder(destination.getAbsolutePath(), patchPath);
                                            Log.i(TAG, "补丁文件解压成功");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.i(TAG, "补丁文件解压失败");
                                        }
                                        // 创建新版本文件夹在 www 目录，并将旧版本代码复制到新文件夹中
                                        String webAppPath = FileUtils.getWebPath(context);
                                        String oldResourcePath = FileUtils.getPaths(webAppPath, oldVersion);
                                        String newResourcePath = FileUtils.getPaths(webAppPath, newVersion);
                                        FileUtils.copyFiles(oldResourcePath, newResourcePath);

                                        // 根据补丁信息删除新版本文件夹的文件

                                        for (int i = 0; i < removeResourceArray.length(); i++) {
                                            try {
                                                String path = removeResourceArray.getString(i);
                                                path = path.replace("./", "");
                                                String removePath = FileUtils.getPaths(newResourcePath, path);
                                                File removeFile = new File(removePath);
                                                boolean deleteResult = removeFile.delete();
                                                if (!deleteResult) {
                                                    Log.e(TAG, "补丁合并，删除文件失败!");
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        // 根据补丁信息复制新文件到新版本文件夹中
                                        final String patchPathName = newVersion + "_" + oldVersion;
                                        String patchFilePath = FileUtils.getPaths(patchPath, patchPathName);
                                        for (int i = 0; i < changeResourceArray.length(); i++) {
                                            try {
                                                String path = changeResourceArray.getString(i);
                                                String changeFilePath = path.replace("./", "");
                                                String fromPath = FileUtils.getPaths(patchFilePath, changeFilePath);
                                                String toPath = FileUtils.getPaths(newResourcePath, changeFilePath);

                                                File fromPathFile = new File(fromPath);
                                                File toPathFile = new File(toPath);
                                                if (!toPathFile.getParentFile().exists()) {
                                                    toPathFile.getParentFile().mkdirs();
                                                }
                                                try {
                                                    org.apache.commons.io.FileUtils.copyFile(fromPathFile, toPathFile);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        // 清除 patch 目录
                                        File appPatchFile = new File(patchPath);
                                        boolean deleteResult = appPatchFile.delete();
                                        if (deleteResult) {
                                            Log.i(TAG, "patch 文件夹删除成功！");
                                        }
                                        // 更新激活版本号
                                        FileUtils.setActivePackageVersion(context, newVersion);
                                        // 重新 web server
                                        restartWebServer();
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    static String getActivePackageWebPath(Context context) {
        String version = FileUtils.getActivePackageVersion(context);
        return FileUtils.getVersionWebPath(context, version);
    }

    private Intent appWebService;
    private Intent getAppWebService(Context context) {
        if (appWebService == null) {
            appWebService = new Intent(context, AppWebCoreService.class);
        }
        return appWebService;
    }

    private void startWebService(Context context) {
        context.startService(getAppWebService(context));
    }

    private void stopWebService(Context context) {
        context.stopService(getAppWebService(context));
    }

    private void initWebCoreServer(Context context) {
        if (mServer == null) {
            mServer = AndServer.serverBuilder(context)
                    .inetAddress(NetUtils.getLocalIPAddress())
                    .port(8887)
                    .timeout(10, TimeUnit.SECONDS)
                    .listener(new Server.ServerListener() {
                        @Override
                        public void onStarted() {
                            Log.i(TAG, "App Web Server 已启动:"+mServer.getInetAddress().getHostAddress());
                        }

                        @Override
                        public void onStopped() {
                            Log.i(TAG, "App Web Server 已停止:"+mServer.getInetAddress().getHostAddress());
                            if (isRestartWebServer) {
                                isRestartWebServer = false;
                                Log.i(TAG, "B开始重新启动 web server");
                                startupWebServer();
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "App Web Server 发生异常:"+e.getMessage());
                        }
                    })
                    .build();
        }
    }

    private void startupWebServer() {
        if (mServer.isRunning()) {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            // notify server running
            Log.i(TAG, "App Web Server 正在运行，不要重复启动。ip is:"+hostAddress);
        } else {
            mServer.startup();
        }
    }

    private void stopWebServer() {
        if (mServer.isRunning()) {
            mServer.shutdown();
        } else {
            Log.i(TAG, "App Web Server 未启动，不能停止。");
        }
    }

    private void restartWebServer() {
        if (mServer.isRunning()) {
            isRestartWebServer = true;
            mServer.shutdown();
        } else {
            Log.i(TAG, "A开始重新启动 web server");
            startupWebServer();
        }
    }
}
