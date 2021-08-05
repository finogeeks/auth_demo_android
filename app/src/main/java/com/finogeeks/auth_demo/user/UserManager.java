package com.finogeeks.auth_demo.user;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private UserManager() {
    }

    private static volatile UserManager INSTANCE = null;

    public static UserManager getInstance() {
        if (INSTANCE == null) {
            synchronized (UserManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserManager();
                }
            }
        }
        return INSTANCE;
    }

    private String userId;

    private String nickName;

    private Map<Double, Long> appletTokenMap = new HashMap<>();

    /**
     * 模拟宿主app的登录
     * <p>
     * 由于仅在主进程中登录，且数据均存于主进程中
     * <p>
     * 因此获取用户信息时必须在主进程中获取
     */
    public void login() {
        userId = "1";
        nickName = "测试用户";
    }

    /**
     * 提供给小程序进行授权登录
     * <p>
     * 生成 token 和有效时间，将 token 返回给小程序
     */
    public String appletLogin() {
        double token = Math.random();
        while (appletTokenMap.containsKey(token)) {
            token = Math.random();
        }
        // 模拟Token有效时间为1分钟
        appletTokenMap.put(token, System.currentTimeMillis() + 60 * 1000);
        return String.valueOf(token);
    }

    /**
     * 小程序使用 token 申请获取用户信息
     */
    public JSONObject getUserProfileFromApplet(String token) {
        // 检查token
        double tokenDouble = -1;
        try {
            tokenDouble = Double.parseDouble(token);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Long validTime = appletTokenMap.get(tokenDouble);
        // token无效或已过期
//        if (!appletTokenMap.containsKey(tokenDouble) || validTime == null || validTime < System.currentTimeMillis()) {
//            return null;
//        }
        // 返回数据
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            jsonObject.put("nickName", nickName);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断小程序携带的用户token是否有效
     */
    public boolean checkAppletTokenSession(String token) {
        try {
            Double tokenDouble = Double.parseDouble(token);
            Long validTime = appletTokenMap.get(tokenDouble);
            if (appletTokenMap.containsKey(tokenDouble) && validTime != null && validTime >= System.currentTimeMillis()) {
                return true;
            } else {
                appletTokenMap.remove(tokenDouble);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
