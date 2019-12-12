package com.lhc.ocat.android_demo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by lhc on 2019/11/8.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    private FileUtils(){}

    static void copyFiles(String fromPath, String toPath) {
        File fromPathFile = new File(fromPath);
        if (fromPathFile.exists() && fromPathFile.isDirectory()) {
            File[] files = fromPathFile.listFiles();
            for (File file :
                    files) {
                String filename = file.getName();
                String targetPath = getPaths(fromPath, filename);
                String destPath = getPaths(toPath, filename);

                File targetFile = new File(targetPath);
                File destFile = new File(destPath);
                if (file.isDirectory()) {
                    boolean result = destFile.mkdirs();
                    if (result) {
                        copyFiles(targetPath, destPath);
                    }
                } else {
                    try {
                        if (!destFile.getParentFile().exists()) {
                            destFile.getParentFile().mkdirs();
                        }
                        org.apache.commons.io.FileUtils.copyFile(targetFile, destFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static void removeFiles(String targetPath) {

    }

    public static void copyAssetsFile(String filename, Context context, File destination) throws Exception{
        if (!destination.exists()) {
            if (!destination.getParentFile().exists()) {
                boolean dirsResult = destination.getParentFile().mkdirs();
                Log.d(TAG, "创建文件夹是否成功:"+dirsResult);
            }
            boolean result = destination.createNewFile();
            Log.d(TAG, "创建文件是否成功:"+result);
        }
        AssetManager assetManager = context.getResources().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            out = new FileOutputStream(destination);
            copyFileStream(in, out);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void copyFileStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    static void unZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String  szName;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()){
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

    /**
     * 拼接文件路径
     * @param first 第一个路径
     * @param more 多个路径的字符串数组
     * @return 拼接好的路径 a/b/c
     */
    static String getPaths(String first, String... more) {
        StringBuilder stringBuilder = new StringBuilder(first);
        int i = 0;
        while (i < more.length) {
            String path = more[i];
            stringBuilder
                    .append(File.separator)
                    .append(path);
            i++;
        }
        return stringBuilder.toString();
    }

    /**
     * 从 SharedPreferences 中获取激活的版本号
     * @param context 上下文
     * @return 当前激活版本号
     */
    static String getActivePackageVersion(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PackageManager.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(PackageManager.ACTIVE_PACKAGE_VERSION_SP_KEY, PackageManager.NONE);
    }

    /**
     * 在 SharedPreferences 中设置激活的版本号
     * @param context 上下文
     * @param version 要设置的版本号
     */
    static void setActivePackageVersion(Context context, String version) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PackageManager.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PackageManager.ACTIVE_PACKAGE_VERSION_SP_KEY, version);
        editor.apply();
    }

    static String getWebPath(Context context) {
        File appInsideFile = context.getFilesDir();
        return FileUtils.getPaths(appInsideFile.getAbsolutePath(), PackageManager.WWW_COMPOSE_PATH);
    }

    static String getPatchPath(Context context) {
        File appInsideFile = context.getFilesDir();
        return FileUtils.getPaths(appInsideFile.getAbsolutePath(), PackageManager.PATCH_COMPOSE_PATH);
    }

    static String getVersionWebPath(Context context, String version) {
        return FileUtils.getPaths(getWebPath(context), version);
    }

}
