package net.projectwhitespace.reviewcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class requestrevisit extends AppCompatActivity {

    public static itemResult result;
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested);

        String ASIN = null;
        String AmazonType = null;

        // Get extras from intent
        try {
            if (getIntent().hasExtra("ASIN"))
                ASIN = getIntent().getExtras().getString("ASIN");
            if (getIntent().hasExtra("Type"))
                AmazonType = getIntent().getExtras().getString("Type");
        }catch(Exception e){
            Log.e("ReviewCheck", String.valueOf(e.getStackTrace()));
            Toast.makeText(getApplicationContext(),getString(R.string.error_loading),Toast.LENGTH_LONG).show();
        }

        // Setting default things for webview
        if(ASIN != null && AmazonType != null) {
            setUpWebView();
            String baseURL = "https://reviewmeta.com/";
            webview.loadUrl(baseURL + '/' + AmazonType + '/' + ASIN);
        }
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
