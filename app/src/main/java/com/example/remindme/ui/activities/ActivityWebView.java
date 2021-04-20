package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;

public class ActivityWebView extends AppCompatActivity {

    public static final String URL = "URL";
    private WebView web_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        final Intent intent = getIntent();
        ActivityHelper.setTitle(this, intent.getAction());

        String url = intent.getStringExtra(URL);

        if (StringHelper.isNullOrEmpty(url)) {
            finish();
        }

        web_view = findViewById(R.id.web_view);

        if (OsHelper.isKitkatOrLater()) {
            WebSettings settings = web_view.getSettings();
            if (settings != null) {
                settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
        }

        web_view.loadUrl(url);
    }
}