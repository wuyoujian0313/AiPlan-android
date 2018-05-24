package com.ai.aiplan;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import com.ai.base.AIBaseActivity;
import com.ai.webplugin.AIWebViewActivity;
import com.ai.webplugin.config.GlobalCfg;

import java.io.InputStream;

public class SplashActivity extends AIBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        initParam();
        setContentView(R.layout.splash_layout);
        enterHomeActivity();
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

    private void enterHomeActivity() {
        Intent intent = new Intent(this, AIWebViewActivity.class);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final  int color = typedValue.data;
        Log.d("backgroudColor",AIWebViewActivity.backgroundColorKey);
        intent.putExtra(AIWebViewActivity.backgroundColorKey,color);
        intent.putExtra(AIWebViewActivity.backgroundResIdKey,R.color.colorPrimary);
        intent.putExtra(AIWebViewActivity.welcomeImageResId,R.mipmap.startpage);

        try {
            GlobalCfg globalCfg = GlobalCfg.getInstance();
            String flavor = BuildConfig.FLAVOR;
            if (flavor.equalsIgnoreCase("AIPlan")) {
                intent.putExtra(AIWebViewActivity.webViewURLKey,globalCfg.attr("online.addr"));
            } else {
                intent.putExtra(AIWebViewActivity.webViewURLKey,globalCfg.attr("online.addrTest"));
            }

            startActivity(intent);
            finish();
        } catch (Exception e) {
        }
    }
}
