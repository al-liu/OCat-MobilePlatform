package com.lhc.ocat.android_demo.manager;

/**
 * @author lhc
 */
public interface Callback {
    /**
     * 包管理器启动完成
     * @param packageManager 包管理器
     */
    void onLaunchFinished(PackageManager packageManager);

    /**
     * 包管理器启动错误
     * @param packageManager 包管理器
     * @param error 错误信息
     */
    void onLaunchError(PackageManager packageManager, PackageError error);

    /**
     * 包管理器更新补丁包完成
     * @param packageManager 包管理器
     */
    void onUpdateFinished(PackageManager packageManager);

    /**
     * 包管理器更新补丁失败
     * @param packageManager 包管理器
     * @param error 错误信息
     */
    void onUpdateError(PackageManager packageManager, PackageError error);
}
