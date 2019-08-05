package net.projectwhitespace.reviewcheck;


import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class requested extends AppCompatActivity {

    WebView webview;
    public static String ASIN;
    public static String amazonType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested);

        // Setting default things for webview
        setUpWebView();

        String baseURL = "https://reviewmeta.com/";
        amazonType = getAmazonType();
        webview.loadUrl(baseURL + '/' + amazonType + '/' + ASIN);
        String requestApiURL = baseURL + "api/" + amazonType + '/' + ASIN;
        new getApiDataAsync().execute(requestApiURL);
    }

    private String getAmazonType(){
        try {
            URI uri = new URI(MainActivity.url);
            String hostname = uri.getHost();
            if(hostname.endsWith("de"))
                return "amazon-de";
            if(hostname.endsWith("com"))
                return "amazon";
            if(hostname.endsWith("ca"))
                return "amazon-ca";
            if(hostname.endsWith("it"))
                return "amazon-it";
            if(hostname.endsWith("in"))
                return "amazon-in";
            if(hostname.endsWith("cn"))
                return "amazon-cn";
            if(hostname.endsWith("com.br"))
                return "amazon-br";
            if(hostname.endsWith("co.uk"))
                return "amazon-uk";
            if(hostname.endsWith("com.au"))
                return "amazon-au";
            if(hostname.endsWith("fr"))
                return "amazon-fr";
            if(hostname.endsWith("co.jp"))
                return "amazon-jp";
            if(hostname.endsWith("es"))
                return "amazon-es";
            if(hostname.endsWith("com.mx"))
                return "amazon-mx";
            if(hostname.endsWith("nl"))
                return "amazon-nl";
        }
        catch(URISyntaxException e){
            Log.d("Bamboozle", Objects.requireNonNull(e.getMessage()));
        }
        return "";
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

// Function for requesting the data from API asynchronously
// AsyncTask<Parameters, Progress Value Type, Return Value Type>
class getApiDataAsync extends AsyncTask<String, Integer,itemResult>{

    @Override
    protected itemResult doInBackground(String... strings) {
        String link = strings[strings.length - 1];

        HttpURLConnection huc = null;
        BufferedReader br = null;
        itemResult itemresult = null;

        try{
            URL url = new URL(link);
            huc = (HttpURLConnection)url.openConnection();

            int responseCode = huc.getResponseCode();
            // Check if Response code is HTTP OK
            if(responseCode == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(huc.getInputStream()));

                String response = br.readLine();
                JSONObject result = new JSONObject(response);

                double rating;
                String refLink;
                Byte overall;
                String name;
                // Get ReviewMeta API information
                rating = Double.parseDouble(result.getString("rating"));
                refLink = result.getString("href");
                overall = Byte.parseByte(result.getString("s_overall"));

                // Get Product Name
                Document document = Jsoup.connect(MainActivity.url).followRedirects(false).timeout(10000).get();
                name = document.body().select("#productTitle").get(0).text();

                itemresult = new itemResult(name,rating,overall,refLink, requested.ASIN, requested.amazonType);
            }
        }catch (Exception e){
            Log.e("ReviewCheck", Objects.requireNonNull(e.getMessage()));
        }
        finally{
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e("ReviewCheck", Objects.requireNonNull(e.getMessage()));
                }
            }
            if(huc != null) {
                huc.disconnect();
            }
        }
        return itemresult;
    }

    // Will run in UI Thread when finished
    protected void onPostExecute(itemResult result){
        MainActivity.searchHistory.add(result);
    }
}