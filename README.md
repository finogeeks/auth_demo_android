<p align="center">
    <a href="https://www.finclip.com?from=github">
    <img width="auto" src="https://www.finclip.com/mop/document/images/logo.png">
    </a>
</p>

<p align="center"> 
    <strong>FinClip 第三方登录 Android DEMO</strong></br>
<p>
<p align="center"> 
        本项目提供在 Android 环境中通过第三方账户登录的 DEMO 样例
<p>

<p align="center"> 
	👉 <a href="https://www.finclip.com?from=github">https://www.finclip.com/</a> 👈
</p>

<div align="center">

<a href="#"><img src="https://img.shields.io/badge/%E4%B8%93%E5%B1%9E%E5%BC%80%E5%8F%91%E8%80%85-20000%2B-brightgreen"></a>
<a href="#"><img src="https://img.shields.io/badge/%E5%B7%B2%E4%B8%8A%E6%9E%B6%E5%B0%8F%E7%A8%8B%E5%BA%8F-6000%2B-blue"></a>
<a href="#"><img src="https://img.shields.io/badge/%E5%B7%B2%E9%9B%86%E6%88%90%E5%B0%8F%E7%A8%8B%E5%BA%8F%E5%BA%94%E7%94%A8-75%2B-yellow"></a>
<a href="#"><img src="https://img.shields.io/badge/%E5%AE%9E%E9%99%85%E8%A6%86%E7%9B%96%E7%94%A8%E6%88%B7-2500%20%E4%B8%87%2B-orange"></a>

<a href="https://www.zhihu.com/org/finchat"><img src="https://img.shields.io/badge/FinClip--lightgrey?logo=zhihu&style=social"></a>
<a href="https://www.finclip.com/blog/"><img src="https://img.shields.io/badge/FinClip%20Blog--lightgrey?logo=ghost&style=social"></a>



</div>

<p align="center">

<div align="center">

[官方网站](https://www.finclip.com/) | [示例小程序](https://www.finclip.com/#/market) | [开发文档](https://www.finclip.com/mop/document/) | [部署指南](https://www.finclip.com/mop/document/introduce/quickStart/cloud-server-deployment-guide.html) | [SDK 集成指南](https://www.finclip.com/mop/document/introduce/quickStart/intergration-guide.html) | [API 列表](https://www.finclip.com/mop/document/develop/api/overview.html) | [组件列表](https://www.finclip.com/mop/document/develop/component/overview.html) | [隐私承诺](https://www.finclip.com/mop/document/operate/safety.html)

</div>

-----
## 🤔 FinClip 是什么?

有没有**想过**，开发好的微信小程序能放在自己的 APP 里直接运行，只需要开发一次小程序，就能在不同的应用中打开它，是不是很不可思议？

有没有**试过**，在自己的 APP 中引入一个 SDK ，应用中不仅可以打开小程序，还能自定义小程序接口，修改小程序样式，是不是觉得更不可思议？

这就是 FinClip ，就是有这么多不可思议！

## 📦 使用注意
在本 DEMO 文档中，您需要根据实际使用情况，**集成 FinClip SDK，自定义小程序接口**，请在使用时注意。

> 本例中的参数在实际开发中由开发者自行制定，本例仅为示范作用。

### 第一步 自定义授权登录 login 接口
因本示例在授权登录时需要展示授权 Dialog，即需要获取 Activity 实例，因此我们需要将该 API 注册在小程序进程，可以方便的获取到展示小程序的 Activity 实例。

```java
public class LoginApi extends AbsApi {

    // 定义代码省略
    
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
       // 跨进程调用代码省略
    }
    
}
```
跨进程调用 API 的相关说明可以查看 FinClip 开发文档，[点我查看](https://www.finclip.com/mop/document/runtime-sdk/android/android-api.html#_1-21-%E8%B7%A8%E8%BF%9B%E7%A8%8B%E8%B0%83%E7%94%A8%E6%8E%A5%E5%8F%A3)

### 第二步 在小程序进程中注册自定义 API
```kotlin
if (FinAppClient.INSTANCE.isFinAppProcess(this)) {
    // 小程序进程
    initFinClipOnAppletProcess();
} else {
    // 主进程初始化代码省略
}
```

```java
/**
 * 将api注册到小程序进程中
 */
private void initFinClipOnAppletProcess() {
    FinAppProcessClient.INSTANCE.setCallback(new FinAppProcessClient.Callback() {
        @Override
        public List<IApi> getRegisterExtensionApis(@NotNull Activity activity) {
            List<IApi> extensionApis = new ArrayList<>();
            extensionApis.add(new LoginApi(activity));
            return extensionApis;
        }

        @Override
        public List<IApi> getRegisterExtensionWebApis(@NotNull Activity activity) {
            return null;
        }
    });
}
```
至此，小程序通过自定义 API 从 APP 获取用户 token 的整个流程就已经完成了。

> 请注意<br>如果产品需求不需要展示用户授权提示 Dialog，建议在主进程注册自定义 Api，从而省掉上述跨进程调用的过程。

### 第三步 自定义获取用户信息 getUserProfile 接口

```java
public class ProfileApi extends AbsApi {

  // 定义代码省略

    /**
     * 此示例中 ProfileApi 直接注册在了主进程的扩展 api 中
     * 因此该 api 是在主进程中执行，可以直接获取数据
     */
    private void getUserProfile(JSONObject jsonObject, ICallback iCallback) {
        // 获取用户信息过程省略
    }

}
```

### 第四步 在主进程中注册 API
```java
FinCallback<Object> initCallback = new FinCallback<Object>() {
    @Override
    public void onSuccess(Object result) {
        // 注册扩展Api，此处注册的Api将会在主进程中执行
        FinAppClient.INSTANCE
                .getExtensionApiManager()
                .registerApi(new ProfileApi());
    }

    @Override
    public void onError(int code, String error) {

    }

    @Override
    public void onProgress(int status, String error) {

    }
};
FinAppClient.INSTANCE.init(this, finAppConfig, initCallback);
```

至此，小程序通过自定义 APP 从 APP 获取用户信息的整个流程就已经完成了。您可以 [点击这里](https://mp.weixin.qq.com/s/v02uQTK6VSEjGdIxxbl_1g) 查看 iOS 端与第三方登录的相关内容。

## 🔗 常用链接
以下内容是您在 FinClip 进行开发与体验时，常见的问题与指引信息

- [FinClip 官网](https://www.finclip.com/#/home)
- [示例小程序](https://www.finclip.com/#/market)
- [文档中心](https://www.finclip.com/mop/document/)
- [SDK 部署指南](https://www.finclip.com/mop/document/introduce/quickStart/intergration-guide.html)
- [小程序代码结构](https://www.finclip.com/mop/document/develop/guide/structure.html)
- [iOS 集成指引](https://www.finclip.com/mop/document/runtime-sdk/ios/ios-integrate.html)
- [Android 集成指引](https://www.finclip.com/mop/document/runtime-sdk/android/android-integrate.html)
- [Flutter 集成指引](https://www.finclip.com/mop/document/runtime-sdk/flutter/flutter-integrate.html)

## ☎️ 联系我们
微信扫描下面二维码，关注官方公众号 **「凡泰极客」**，获取更多精彩内容。<br>
<img width="150px" src="https://www.finclip.com/mop/document/images/ic_qr.svg">

微信扫描下面二维码，加入官方微信交流群，获取更多精彩内容。<br>
<img width="150px" src="https://www-cdn.finclip.com/images/qrcode/qrcode_shequn_text.png">
