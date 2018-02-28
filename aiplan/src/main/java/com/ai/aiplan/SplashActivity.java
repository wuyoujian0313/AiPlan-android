package com.ai.aiplan;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;

import com.ai.base.AIBaseActivity;
import com.ai.webplugin.WebViewKitActivity;

public class SplashActivity extends AIBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.splash_layout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterHomeActivity();
            }
        }, 2000);
    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, WebViewKitActivity.class);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final  int color = typedValue.data;
        Log.d("backgroudColor",WebViewKitActivity.backgroundColorKey);
        intent.putExtra(WebViewKitActivity.backgroundColorKey,color);
        //intent.putExtra(WebViewKitActivity.webViewURLKey,"http://www.baidu.com");
        startActivity(intent);
        finish();
    }
}
