package com.lhc.ocat.android_demo.manager;

import android.content.Context;
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
    public static String INBUILT_PACKAGE_ASSETS_PATH = "pre-package/";

    private Server mServer;
    private boolean isRestartWebServer;
    private PackageSettings settings;
    private Callback callback;
    private int webServerStartType = WEB_SERVER_START_TYPE_RESET;
    private static final int WEB_SERVER_START_TYPE_RESET = 0;
    private static final int WEB_SERVER_START_TYPE_LAUNCH = 1;
    private static final int WEB_SERVER_START_TYPE_UPDATE = 2;

    private PackageManager() {
    }

    private static class PackageManagerHolder {
        private static PackageManager INSTANCE = new PackageManager();
    }

    public static PackageManager sharedInstance() {
        return PackageManagerHolder.INSTANCE;
    }

    public static PackageManager manageWithSettings(PackageSettings settings) {
        PackageManager packageManager = PackageManager.sharedInstance();
        packageManager.setSettings(settings);
        return packageManager;
    }

    public PackageSettings getSettings() {
        return settings;
    }

    private void setSettings(PackageSettings settings) {
        this.settings = settings;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void launch(Context context) {
        if (this.getSettings() == null) {
            // 报错
            Log.e(TAG, "未给包管理器设置 PackageSettings 对象");
            PackageError error = new PackageError(-7, PackageError.LAUNCH_FAIL_DESCRIPTION);
            error.setReason("离线包管理器没有配置类无法使用");
            error.setSuggestion("使用 manageWithSettings 方法初始化管理器");
            onLaunchError(error);
            return;
        }
        // 初始化 web server
        initWebCoreServer(context);
        // 取偏好设置中的激活版本号
        String activePackageVersion = FileUtils.getActivePackageVersion(context);
        if (NONE.equals(activePackageVersion)) {
            // 使用预置包（首次打开）
            usePrePackage(this.getSettings().getInbuiltPackageVersion(), context);
        } else {
            String activeVersionPath = FileUtils.getVersionWebPath(context, activePackageVersion);
            File activeVersionFile = new File(activeVersionPath);
            // 判断当前激活版本号的路径是否存在
            if (activeVersionFile.exists()) {
                // 起服务到激活版本
                startupWebServer(WEB_SERVER_START_TYPE_LAUNCH);
            } else {
                // 异常情况：当前版本号存在，资源不存在，这种情况切到预置包应急。
                usePrePackage(this.getSettings().getInbuiltPackageVersion(), context);
            }
        }
    }

    public void updateLatestPatch(Context context) {
        if (this.getSettings() == null) {
            // 报错
            Log.e(TAG, "未给包管理器设置 PackageSettings 对象");
            PackageError error = new PackageError(-7, PackageError.LAUNCH_FAIL_DESCRIPTION);
            error.setReason("离线包管理器没有配置类无法使用");
            error.setSuggestion("使用 manageWithSettings 方法初始化管理器");
            onLaunchError(error);
            return;
        }
        // 初始化 web server
        initWebCoreServer(context);
        // 取偏好设置中的激活版本号
        String activePackageVersion = FileUtils.getActivePackageVersion(context);
        checkPatchResource(activePackageVersion, context);
    }

    private void usePrePackage(String prePackageVersion, Context context) {
        String appWebPath = FileUtils.getWebPath(context);
        // 复制 assert 预置压缩包文件
        String copyDestinationPath = FileUtils.getPaths(appWebPath, ALL_PACKAGE_NAME);
        String preAllPackagePath = INBUILT_PACKAGE_ASSETS_PATH + ALL_PACKAGE_NAME;
        try {
            FileUtils.copyAssetsFile(preAllPackagePath,
                    context,
                    new File(copyDestinationPath));
        } catch (Exception e) {
            Log.e(TAG, "包管理中断，复制预置包失败！");
            PackageError error = new PackageError(-1, PackageError.LAUNCH_FAIL_DESCRIPTION, e);
            error.setReason("复制预置包到 web 路径报错");
            error.setSuggestion("重新启动 APP 重试");
            onLaunchError(error);
            return;
        }
        // 解压预置包文件
        try {
            FileUtils.unZipFolder(copyDestinationPath, appWebPath);
        } catch (Exception e) {
            e.printStackTrace();
            PackageError error = new PackageError(-2, PackageError.LAUNCH_FAIL_DESCRIPTION, e);
            error.setReason("解压缩预置包报错");
            error.setSuggestion("请检查预置 assets 中的包是否完整");
            onLaunchError(error);
            return;
        }
        String preVersionPath = FileUtils.getPaths(appWebPath, prePackageVersion);
        File preVersionFile = new File(preVersionPath);
        if (!preVersionFile.exists()) {
            Log.e(TAG, "预置包文件夹名与预置版本号不符合");
            PackageError error = new PackageError(-3, PackageError.LAUNCH_FAIL_DESCRIPTION);
            error.setReason("预置包文件夹名与预置版本号不符合");
            error.setSuggestion("请检查预置 assets 中的预置压缩包文件夹版本号");
            onLaunchError(error);
            return;
        }
        // 保存版本号
        FileUtils.setActivePackageVersion(context, prePackageVersion);
        // 起服务到预置版本
        startupWebServer(WEB_SERVER_START_TYPE_LAUNCH);
    }

    private void checkPatchResource(final String activeVersion, final Context context) {
        String url = this.getSettings().getServerBaseUrl() + "/package/fetch";
        Map<String, Object> params = new HashMap<>();
        params.put("versionName", activeVersion);
        params.put("appId", this.getSettings().getAppId());
        params.put("appSecret", this.getSettings().getAppSecret());
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
                                // 不需要更新补丁, delegate
                                Log.i(TAG, "当前版本不需要更新");
                                PackageError error = new PackageError(-7, PackageError.UPDATE_FAIL_DESCRIPTION);
                                error.setReason("当前版本不需要更新");
                                error.setSuggestion("不需要更新");
                                onUpdateError(error);
                            } else {
                                String patchUrl = patchJsonObj.getString("downloadUrl");
                                final JSONArray changeResourceArray = patchJsonObj.getJSONArray("changeResourceInfo");
                                final JSONArray removeResourceArray = patchJsonObj.getJSONArray("removeResourceInfo");
                                // 下载补丁
                                final String patchPath = FileUtils.getPatchPath(context);
                                final String patchName = newVersion + "_" + oldVersion + ".zip";
                                String patchZipPath = FileUtils.getPaths(patchPath, patchName);
                                File patchFilePath = new File(patchZipPath);
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
                                            PackageError error = new PackageError(-8, PackageError.UPDATE_FAIL_DESCRIPTION, e);
                                            error.setReason("更新补丁包解压缩失败");
                                            error.setSuggestion("请检查后台下发的补丁包能否正常解压缩");
                                            onUpdateError(error);
                                        }
                                        mergePatch(context,
                                                oldVersion,
                                                newVersion,
                                                changeResourceArray,
                                                removeResourceArray);
                                    }
                                });
                            }
                        } else {
                            String message = jsonObject.getString("message");
                            PackageError error = new PackageError(-6, PackageError.UPDATE_FAIL_DESCRIPTION);
                            error.setReason(message);
                            error.setSuggestion("请重试");
                            onUpdateError(error);
                            Log.e(TAG, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        PackageError error = new PackageError(-5, PackageError.UPDATE_FAIL_DESCRIPTION, e);
                        error.setReason("补丁更新请求结果转 JSON 失败");
                        error.setSuggestion("请重试");
                        onUpdateError(error);
                    }
                } else {
                    PackageError error = new PackageError(-4, PackageError.UPDATE_FAIL_DESCRIPTION);
                    error.setReason("补丁更新请求失败，未响应");
                    error.setSuggestion("请重试");
                    onUpdateError(error);
                }
            }
        });
    }

    private void mergePatch(Context context,
                            String oldVersion,
                            String newVersion,
                            JSONArray changeResourceArray,
                            JSONArray removeResourceArray) {
        final String patchPath = FileUtils.getPatchPath(context);
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
                    PackageError error = new PackageError(-11, PackageError.UPDATE_FAIL_DESCRIPTION);
                    error.setReason("合并补丁出错，删除文件失败");
                    error.setSuggestion("请重试");
                    onUpdateError(error);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                PackageError error = new PackageError(-12, PackageError.UPDATE_FAIL_DESCRIPTION, e);
                error.setReason("获取补丁信息出错");
                error.setSuggestion("请重试");
                onUpdateError(error);
                return;
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
                    PackageError error = new PackageError(-13, PackageError.UPDATE_FAIL_DESCRIPTION, e);
                    error.setReason("合并补丁出错");
                    error.setSuggestion("请重试");
                    onUpdateError(error);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                PackageError error = new PackageError(-14, PackageError.UPDATE_FAIL_DESCRIPTION, e);
                error.setReason("获取补丁信息出错");
                error.setSuggestion("请重试");
                onUpdateError(error);
                return;
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

    static String getActivePackageWebPath(Context context) {
        String version = FileUtils.getActivePackageVersion(context);
        return FileUtils.getVersionWebPath(context, version);
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
                            if (webServerStartType == WEB_SERVER_START_TYPE_LAUNCH) {
                                onLaunchFinished();
                            } else if (webServerStartType == WEB_SERVER_START_TYPE_UPDATE) {
                                onUpdateFinished();
                            }
                            webServerStartType = WEB_SERVER_START_TYPE_RESET;
                        }

                        @Override
                        public void onStopped() {
                            Log.i(TAG, "App Web Server 已停止:"+mServer.getInetAddress().getHostAddress());
                            if (isRestartWebServer) {
                                isRestartWebServer = false;
                                Log.i(TAG, "B开始重新启动 web server");
                                startupWebServer(WEB_SERVER_START_TYPE_UPDATE);
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "App Web Server 发生异常:"+e.getMessage());

                            if (webServerStartType == WEB_SERVER_START_TYPE_LAUNCH) {
                                PackageError error = new PackageError(-9, PackageError.LAUNCH_FAIL_DESCRIPTION, e);
                                error.setReason("Web 服务启动失败");
                                error.setSuggestion("请重试");
                                onLaunchError(error);
                            } else if (webServerStartType == WEB_SERVER_START_TYPE_UPDATE) {
                                PackageError error = new PackageError(-10, PackageError.UPDATE_FAIL_DESCRIPTION, e);
                                error.setReason("Web 服务启动失败");
                                error.setSuggestion("请重试");
                                onUpdateError(error);
                            }
                            webServerStartType = WEB_SERVER_START_TYPE_RESET;
                        }
                    })
                    .build();
        }
    }

    private void startupWebServer(int type) {
        webServerStartType = type;
        if (mServer.isRunning()) {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            // notify server running
            Log.i(TAG, "App Web Server 正在运行，不要重复启动。ip is:"+hostAddress);
        } else {
            mServer.startup();
        }
    }

    private void restartWebServer() {
        if (mServer.isRunning()) {
            isRestartWebServer = true;
            mServer.shutdown();
        } else {
            Log.i(TAG, "A开始重新启动 web server");
            startupWebServer(WEB_SERVER_START_TYPE_UPDATE);
        }
    }

    private void onLaunchFinished() {
        if (this.callback != null) {
            this.callback.onLaunchFinished(this);
        }
    }

    private void onLaunchError(PackageError error) {
        if (this.callback != null) {
            this.callback.onLaunchError(this, error);
        }
    }

    private void onUpdateFinished() {
        if (this.callback != null) {
            this.callback.onUpdateFinished(this);
        }
    }

    private void onUpdateError(PackageError error) {
        if (this.callback != null) {
            this.callback.onUpdateError(this, error);
        }
    }
}
