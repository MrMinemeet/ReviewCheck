package net.projectwhitespace.reviewcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class requestrevisit extends AppCompatActivity {

    public static itemResult result;
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested);

        result = MainActivity.searchHistory.get(MainActivity.revisitId);

        // Setting default things for webview
        setUpWebView();

        String baseURL = "https://reviewmeta.com/";
        webview.loadUrl(baseURL + '/' + result.getAmazonType() + '/' + result.getASIN());
    }

    public void setUpWebView(){
        webview = findViewById(R.id.browser);
        WebSettings webSettings = webview.getSettings();
        // Enable Javascript
        webSettings.setJavaScriptEnabled(false);

        webview.setWebViewClient(new WebViewClient());
        CookieManager.getInstance().setAcceptCookie(true);
    }
}
