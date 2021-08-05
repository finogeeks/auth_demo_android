package com.finogeeks.auth_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.finogeeks.auth_demo.applet_api.LoginApi;
import com.finogeeks.auth_demo.user.UserManager;
import com.finogeeks.lib.applet.client.FinAppClient;
import com.finogeeks.lib.applet.interfaces.FinCallback;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMainProcessCallHandler();
        login();
    }

    /**
     * 初始化跨进程调用主进程Api的Handler，可以根据需要，在其它合适的地方进行初始化操作
     */
    private void initMainProcessCallHandler() {
        FinAppClient.INSTANCE
                .getAppletApiManager()
                .setAppletProcessCallHandler((api, params, finCallback) -> {
                    if (finCallback == null) {
                        return;
                    }
                    switch (api) {
                        case LoginApi.API_NAME_LOGIN:
                            appletAuthLogin(finCallback);
                            break;
                    }
                });
    }

    /**
     * 模拟宿主app的登录
     * 由于仅在主进程中登录，且数据均存于主进程中
     * 因此获取用户信息时必须在主进程中获取
     */
    private void login() {
        UserManager.getInstance().login();
    }

    /**
     * 小程序授权登录，获得用户信息token
     */
    private void appletAuthLogin(FinCallback<String> finCallback) {
        try {
            String token = UserManager.getInstance().appletLogin();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", token);
            finCallback.onSuccess(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
            finCallback.onError(-1, e.getMessage());
        }
    }

    public void launchApplet(View view) {
        FinAppClient.INSTANCE.getAppletApiManager().startApplet(this, "60f521c8525ea10001c0bd60"); // 修改为你的appId
    }
}