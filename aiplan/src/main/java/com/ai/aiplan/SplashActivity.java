package com.ai.aiplan;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.TypedValue;

import com.ai.base.AIBaseActivity;
import com.ai.base.SourceManager.app.MobileAppInfo;
import com.ai.base.okHttp.OkHttpBaseAPI;
import com.ai.base.util.FileUtilCommon;
import com.ai.base.util.LogUtil;
import com.ai.base.util.PermissionUitls;
import com.ai.webplugin.WebViewKitActivity;
import com.ai.webplugin.config.GlobalCfg;
import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AIBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.splash_layout);
        initParam();
        checkVersion();
    }

    private void initParam() {
        GlobalCfg globalCfg = GlobalCfg.getInstance();
        try {
            InputStream is = getResources().getAssets().open("global.properties");
            globalCfg.parseConfig(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPermissionByCode(final int permissionCode,String[] permissions) {
        PermissionUitls.PermissionListener permissionListener = new PermissionUitls.PermissionListener() {
            @Override
            public void permissionAgree() {
                switch (permissionCode){
                    case PermissionUitls.PERMISSION_STORAGE_CODE :
                        LogUtil.d("permission",permissionCode+"");
                        break;
                }
            }

            @Override
            public void permissionReject() {

            }
        };
        PermissionUitls permissionUitls = PermissionUitls.getInstance(null, permissionListener);
        permissionUitls.permssionCheck(permissionCode,permissions);
    }

    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                GlobalCfg globalCfg = GlobalCfg.getInstance();
                String url = globalCfg.attr("online.versionURL");
                String locationVersion = globalCfg.attr(GlobalCfg.CONFIG_FIELD_VERSION);
                String flavor = BuildConfig.FLAVOR;
                if (!flavor.equalsIgnoreCase("AIPlan")) {
                    url = globalCfg.attr("online.versionURLTest");
                }

                OkHttpBaseAPI okHttpBaseAPI = new OkHttpBaseAPI();
                String data = okHttpBaseAPI.httpGetTask(url, "getVersion");
                try{
                    Gson gson = new Gson();
                    VersionBean versionInfo = gson.fromJson(data, VersionBean.class);
                    String platform = versionInfo.getPlatform();
                    final String versionURL = versionInfo.getVersionURL();
                    if (platform.equalsIgnoreCase("android")) {
                        String versionNumber = versionInfo.getVersionNumber();
                        if (!versionNumber.equalsIgnoreCase(locationVersion)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkUpdate(versionURL);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            PermissionUitls.mContext = SplashActivity.this;
                                            final String checkPermissinos [] = {Manifest.permission.INTERNET,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                            checkPermissionByCode(PermissionUitls.PERMISSION_STORAGE_CODE,checkPermissinos);
                                            enterHomeActivity();
                                        }
                                    }, 2000);
                                }
                            });

                        }
                    }
                }catch (Exception e){
                }
            }
        }).start();
    }

    private void checkUpdate(final String apkURL) {
        // 创建构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置参数
        builder.setTitle("提示").setIcon(R.mipmap.ic_launcher)
                .setMessage("远端发现新版本请更新后重新启动应用")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUitls.mContext = SplashActivity.this;
                        final String checkPermissinos [] = {Manifest.permission.INTERNET,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        checkPermissionByCode(PermissionUitls.PERMISSION_STORAGE_CODE,checkPermissinos);

                        updateApk(apkURL);
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private ProgressDialog dialog;
    private void updateApk(final String apkURL) {
        dialog = ProgressDialog.show(this, "", "信.点兵下载中……", true, false, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpBaseAPI okHttpBaseAPI = new OkHttpBaseAPI();
                byte[] data = okHttpBaseAPI.httpGetFileDataTask(apkURL, "apkDonwload");

                String filePath = MobileAppInfo.getSdcardPath() + "/" + "apk";
                final String apkPath = filePath + "/AIPlan.apk";
                FileUtilCommon.writeByte2File(filePath, "AIPlan.apk", data, "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (isExist(apkPath)){
                            installApkarchive(apkPath);
                        }
                    }
                });
            }
        }).start();
    }

    public void installApkarchive(String apkFilePath) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uriPath = Uri.fromFile(new File(apkFilePath));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            //7.0+版本手机
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uriPath = FileProvider.getUriForFile(this,"com.ai.aiplan.fileprovider",new File(apkFilePath));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uriPath, "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
        Runtime.getRuntime().exit(0);
    }

    private boolean isExist(String apkFilePath) {
        File file = new File(apkFilePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, WebViewKitActivity.class);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final  int color = typedValue.data;
        Log.d("backgroudColor",WebViewKitActivity.backgroundColorKey);
        intent.putExtra(WebViewKitActivity.backgroundColorKey,color);

        try {
            GlobalCfg globalCfg = GlobalCfg.getInstance();
            String ua = globalCfg.attr("userAgent");
            intent.putExtra(WebViewKitActivity.webViewUserAgentKey,ua);
            String flavor = BuildConfig.FLAVOR;
            if (flavor.equalsIgnoreCase("AIPlan")) {
                intent.putExtra(WebViewKitActivity.webViewURLKey,globalCfg.attr("online.addr"));
            } else {
                intent.putExtra(WebViewKitActivity.webViewURLKey,globalCfg.attr("online.addrTest"));
            }

            startActivity(intent);
            finish();
        } catch (Exception e) {
        }
    }
}
