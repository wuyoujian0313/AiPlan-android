package com.ai.aiplan;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
        startActivity(intent);
        finish();
    }
}
