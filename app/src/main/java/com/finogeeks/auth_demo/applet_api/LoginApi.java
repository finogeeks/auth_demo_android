package com.finogeeks.auth_demo.applet_api;

import android.app.Activity;
import android.app.AlertDialog;

import com.finogeeks.lib.applet.api.AbsApi;
import com.finogeeks.lib.applet.client.FinAppProcessClient;
import com.finogeeks.lib.applet.interfaces.FinCallback;
import com.finogeeks.lib.applet.interfaces.ICallback;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginApi extends AbsApi {

    private Activity activity;

    public LoginApi(Activity activity) {
        this.activity = activity;
    }

    /**
     * 授权登录，获得宿主App的用户token
     */
    public static final String API_NAME_LOGIN = "login";

    @Override
    public String[] apis() {
        return new String[]{API_NAME_LOGIN};
    }

    @Override
    public void invoke(String api, JSONObject jsonObject, ICallback iCallback) {
        switch (api) {
            case API_NAME_LOGIN:
                showAuthDialog(iCallback);
                break;
            default:
                iCallback.onFail();
                break;
        }
    }

    private void showAuthDialog(ICallback iCallback) {
        new AlertDialog.Builder(activity)
                .setTitle("授权登录")
                .setMessage("是否授权该小程序获取用户信息？")
                .setCancelable(false)
                .setPositiveButton("确定", (dialog, which) -> authLoginOnMainProcess(iCallback))
                .setNegativeButton("取消", (dialog, which) -> iCallback.onFail())
                .show();
    }

    /**
     * 由于用户信息一般只会存储在主进程中，在小程序进程中直接调用取不到数据
     * 因此要使用 callInMainProcess 方法跨进程调用，在主进程中获取到信息后，再回传给小程序进程
     */
    private void authLoginOnMainProcess(ICallback iCallback) {
        FinAppProcessClient.INSTANCE
                .getAppletProcessApiManager()
                .callInMainProcess(API_NAME_LOGIN,
                        null,
                        new FinCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    activity.runOnUiThread(() -> iCallback.onSuccess(jsonObject));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    activity.runOnUiThread(iCallback::onFail);

                                }
                            }

                            @Override
                            public void onError(int code, String error) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("code", code);
                                    jsonObject.put("error", error);
                                    activity.runOnUiThread(() -> iCallback.onFail(jsonObject));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    activity.runOnUiThread(iCallback::onFail);
                                }
                            }

                            @Override
                            public void onProgress(int status, String info) {

                            }
                        });
    }

}
