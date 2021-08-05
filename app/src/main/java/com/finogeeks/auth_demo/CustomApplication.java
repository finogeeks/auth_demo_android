package com.finogeeks.auth_demo;

import android.app.Activity;
import android.app.Application;

import com.finogeeks.auth_demo.applet_api.AppletSessionApi;
import com.finogeeks.auth_demo.applet_api.LoginApi;
import com.finogeeks.auth_demo.applet_api.ProfileApi;
import com.finogeeks.lib.applet.client.FinAppClient;
import com.finogeeks.lib.applet.client.FinAppConfig;
import com.finogeeks.lib.applet.client.FinAppProcessClient;
import com.finogeeks.lib.applet.client.FinStoreConfig;
import com.finogeeks.lib.applet.interfaces.FinCallback;
import com.finogeeks.lib.applet.interfaces.IApi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomApplication extends Application {

    List<FinStoreConfig> finStoreConfigs = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (FinAppClient.INSTANCE.isFinAppProcess(this)) {
            // 小程序进程
            initFinClipOnAppletProcess();
        } else {
            // 主进程
            initFinClipOnMainProcess();
        }
    }

    /**
     * 小程序进程启动时的一些初始化
     */
    private void initFinClipOnAppletProcess() {
        FinAppProcessClient.INSTANCE.setCallback(new FinAppProcessClient.Callback() {
            @Override
            public List<IApi> getRegisterExtensionApis(@NotNull Activity activity) {
                List<IApi> extensionApis = new ArrayList<>();
                // 由于获取用户id的LoginApi需要展示授权弹窗，即需要Activity实例
                // 因此将该Api在小程序进程中注册，可以获得小程序进程的FinAppActivity，用于展示授权弹窗
                // 真正获取用户信息的操作将交给主进程处理，因为用户信息一般只会在主进程中存储
                extensionApis.add(new LoginApi(activity));
                return extensionApis;
            }

            @Override
            public List<IApi> getRegisterExtensionWebApis(@NotNull Activity activity) {
                return null;
            }
        });
    }

    private void initFinClipOnMainProcess() {
        // FinStoreConfig
        finStoreConfigs.add(
                new FinStoreConfig(
                        // 修改为你的SDK Key
                        "22LyZEib0gLTQdU3MUauAcn8HOQZ3p5VIeAxxMyTzsA=",
                        // 修改为你的SDK Secret
                        "5de7f0e82c902090",
                        // 服务器地址
                        "https://api.finclip.com",
                        // 数据上报服务器地址
                        "https://api.finclip.com",
                        // 服务器接口请求路由前缀
                        "/api/v1/mop/",
                        "",
                        // 加密方式，国密:SM，md5: MD5
                        FinAppConfig.ENCRYPTION_TYPE_SM)
        );
        // FinAppConfig
        FinAppConfig finAppConfig = new FinAppConfig.Builder()
                .setDebugMode(true)
                .setFinStoreConfigs(finStoreConfigs)
                .build();
        // 初始化回调
        FinCallback<Object> initCallback = new FinCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                // 注册扩展Api，此处注册的Api将会在主进程中执行
                FinAppClient.INSTANCE
                        .getExtensionApiManager()
                        .registerApi(new ProfileApi());
                FinAppClient.INSTANCE
                        .getExtensionApiManager()
                        .registerApi(new AppletSessionApi());
            }

            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onProgress(int status, String error) {

            }
        };
        // Init
        FinAppClient.INSTANCE.init(this, finAppConfig, initCallback);
    }

}
