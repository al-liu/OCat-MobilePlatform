/*
 * Copyright © 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lhc.ocat.android_demo.manager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.util.concurrent.TimeUnit;

/**
 * @author lhc
 */
public class AppWebCoreService extends Service {

    private static final String TAG = "AppWebCoreService";

    private Server mServer;

    @Override
    public void onCreate() {
        mServer = AndServer.serverBuilder(this)
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
                }

                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "App Web Server 发生异常:"+e.getMessage());
                }
            })
            .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    /**
     * Start server.
     */
    private void startServer() {
        if (mServer.isRunning()) {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            // notify server running
            Log.i(TAG, "App Web Server 正在运行，不要重复启动。ip is:"+hostAddress);
        } else {
            mServer.startup();
        }
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        if (mServer.isRunning()) {
            mServer.shutdown();
        } else {
            Log.i(TAG, "App Web Server 未启动，不能停止。");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}