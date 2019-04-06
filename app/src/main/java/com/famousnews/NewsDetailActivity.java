package com.famousnews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NewsDetailActivity extends AppCompatActivity {
    private WebView webView;
    private TextView tv_URL;
    private ProgressBar urlProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        tv_URL = findViewById(R.id.tv_url);
        webView = findViewById(R.id.webView);
        urlProgressBar = findViewById(R.id.url_progress_bar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        Intent i = getIntent();
        String mURL = i.getStringExtra("url");
        tv_URL.setText(mURL);
        initWebView(mURL);
    }

    private void initWebView(String url) {
        if (amIConnected()) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    tv_URL.setText(url);
                    return true;
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    urlProgressBar.setProgress(progress);
                    if (progress == 100) {
                        urlProgressBar.setVisibility(View.GONE);

                    } else {
                        urlProgressBar.setVisibility(View.VISIBLE);

                    }
                }
            });
        } else {
            showErrorMessage(url);
        }
    }

    private boolean amIConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private void showErrorMessage(final String url) {
        Snackbar mSnackBar = Snackbar.make(webView, "No Internet Connection !", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initWebView(url);
                    }
                });
        mSnackBar.setActionTextColor(Color.WHITE);
        mSnackBar.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return false;
    }
}
