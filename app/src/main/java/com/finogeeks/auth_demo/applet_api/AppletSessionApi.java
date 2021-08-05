package com.finogeeks.auth_demo.applet_api;

import com.finogeeks.auth_demo.user.UserManager;
import com.finogeeks.lib.applet.api.AbsApi;
import com.finogeeks.lib.applet.interfaces.ICallback;

import org.json.JSONException;
import org.json.JSONObject;

public class AppletSessionApi extends AbsApi {

    /**
     * 检查用户token是否有效
     */
    public static final String API_NAME_CHECK_SESSION = "checkSession";

    @Override
    public String[] apis() {
        return new String[]{API_NAME_CHECK_SESSION};
    }

    @Override
    public void invoke(String api, JSONObject jsonObject, ICallback iCallback) {
        switch (api) {
            case API_NAME_CHECK_SESSION:
                checkSession(jsonObject, iCallback);
                break;
            default:
                iCallback.onFail();
                break;
        }
    }

    private void checkSession(JSONObject jsonObject, ICallback iCallback) {
        try {
            String token = jsonObject.getString("token");
            boolean tokenValid = UserManager.getInstance().checkAppletTokenSession(token);
            if (tokenValid) {
                JSONObject resultJson = new JSONObject();
                resultJson.put("valid", true);
                iCallback.onSuccess(resultJson);
            } else {
                iCallback.onFail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            iCallback.onFail();
        }
    }

}
