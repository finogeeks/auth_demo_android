package com.finogeeks.auth_demo.applet_api;

import com.finogeeks.auth_demo.user.UserManager;
import com.finogeeks.lib.applet.api.AbsApi;
import com.finogeeks.lib.applet.interfaces.ICallback;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileApi extends AbsApi {

    /**
     * 获取宿主App的用户信息
     */
    public final static String API_NAME_GET_USER_PROFILE = "getUserProfile";

    @Override
    public String[] apis() {
        return new String[]{API_NAME_GET_USER_PROFILE};
    }

    @Override
    public void invoke(String api, JSONObject jsonObject, ICallback iCallback) {
        switch (api) {
            case API_NAME_GET_USER_PROFILE:
                getUserProfile(jsonObject, iCallback);
                break;
            default:
                iCallback.onFail();
                break;
        }
    }

    /**
     * 此示例中 ProfileApi 直接注册在了主进程的扩展 api 中
     * 因此该 api 是在主进程中执行，可以直接获取数据
     */
    private void getUserProfile(JSONObject jsonObject, ICallback iCallback) {
        try {
            String token = jsonObject.optString("token");
            JSONObject resultJsonObj = UserManager.getInstance().getUserProfileFromApplet(token);
            if (resultJsonObj != null) {
                iCallback.onSuccess(resultJsonObj);
            } else {
                iCallback.onFail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            iCallback.onFail();
        }
    }

}
