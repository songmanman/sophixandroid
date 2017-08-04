package com.alibaba.sophix.demo;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * User: xingzhi.wap
 * Date:16/5/17
 */
public class MainApplication extends Application {
    public interface MsgDisplayListener {
        void handle(String msg);
    }

    public static MsgDisplayListener msgDisplayListener = null;
    public static StringBuilder cacheMsg = new StringBuilder();

    @Override
    public void onCreate() {
        super.onCreate();
        initHotfix();
    }

    private void initHotfix() {
        String appVersion;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "1.0";
        }

        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                //.setAesKey("0123456789123456")
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        String msg = new StringBuilder("").append("Mode:").append(mode)
                                .append(" Code:").append(code)
                                .append(" Info:").append(info)
                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();
                        Log.d("", msg);
                        PatchBean bean = new PatchBean();
                        bean.setCode(code);
                        bean.setHandlePatchVersion(handlePatchVersion);
                        bean.setInfo(info);
                        bean.setMode(mode);
                        Gson gson = new Gson();
                        if (msgDisplayListener != null) {
                            msgDisplayListener.handle(gson.toJson(bean));
                        } else {
                            cacheMsg.append("\n").append(gson.toJson(bean));
                        }
                    }
                }).initialize();
    }
}
