package net.projectwhitespace.reviewcheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class requested extends AppCompatActivity {
    public static itemResult result = new itemResult();
    public static WebView webview;
    public static ProgressBar progressBar;
    public static String ASIN;
    public static String amazonType = "";


    public static boolean gotProductInformation = false;
    public static boolean gotApiData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested);
        // Setting default things for webview
        setUpWebView();

        progressBar = findViewById(R.id.pgb_loading);

        // Create API and Web URL
        String baseURL = "https://reviewmeta.com/";
        amazonType = getAmazonType();
        result.setAmazonType(amazonType);
        result.setASIN(ASIN);
        String reviewMetaURL = baseURL + '/' + amazonType + '/' + ASIN;
        String reviewMetaAPI = baseURL + "api/" + amazonType + '/' + ASIN;;

        // Load URL into webview
        webview.loadUrl(reviewMetaURL);

        // Call threads to get data in other thread
        new getApiDataAsync().execute(reviewMetaAPI);
        new getProductInformationAsync().execute(reviewMetaURL);
        new CheckForFinishedGathering().execute();
    }

    @NotNull
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

    @Override
    protected void onPause() {
        super.onPause();
        if(result != null)
            MainActivity.searchHistory.add(result);
    }
}

class CheckForFinishedGathering extends AsyncTask<String, Void, Void>{

    @Override
    protected Void doInBackground(String... strings) {
        while(!requested.gotProductInformation && !requested.gotApiData){
            // App is still gathering data
            // wait for 50ms
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        requested.progressBar.setVisibility(View.GONE);
        requested.webview.setVisibility(View.VISIBLE);
    }
}

// Function for grabbing and filtering product name and other specific information
class getProductInformationAsync extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... strings) {
        String url = strings[strings.length - 1];
        Document doc = null;

        // Download html-website with Jsoup
        try {
            doc = Jsoup.connect(url).followRedirects(false).timeout(10000).get();

        } catch (IOException e) {
            Log.e("ReviewCheck", String.valueOf(e.getStackTrace()));
            return null;
        }

        // Fetch productname from html code
        try{
            if(doc != null) {
                String s = doc.body().select("#product_name").get(0).text();
                String[] splitted = s.split("More");
                requested.result.setName(splitted[0]);
            }
        }catch(Exception e){
            Log.e("ReviewCheck", String.valueOf(e.getStackTrace()));
            return null;
        }

        // Fetch product image url from html code
        try{
            if(doc != null) {
                Element image = doc.body().select("#main_product_image").get(0);
                String imageUrl = image.absUrl("src");

                InputStream input = new URL(imageUrl).openStream();
                Bitmap productImage = BitmapFactory.decodeStream(input);

                requested.result.setPictureUrl(imageUrl);
                requested.result.setPicture(productImage);
            }
        }catch(Exception e){
            Log.e("ReviewCheck", String.valueOf(e.getStackTrace()));
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        requested.gotProductInformation = true;
    }
}

// Function for requesting the data from API asynchronously
// AsyncTask<Parameters, Progress Value Type, Return Value Type>
class getApiDataAsync extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        String link = strings[strings.length - 1];

        HttpURLConnection huc = null;
        BufferedReader br = null;

        // Get Information from ReviewMeta API
        try{
            URL url = new URL(link);
            huc = (HttpURLConnection)url.openConnection();

            int responseCode = huc.getResponseCode();
            // Check if Response code is HTTP OK
            if(responseCode == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                String response = br.readLine();
                JSONObject result = new JSONObject(response);
                // Get ReviewMeta API information
                requested.result.setRating(Double.parseDouble(result.getString("rating")));
                requested.result.setLink(result.getString("href"));
                requested.result.setOverall(Byte.parseByte(result.getString("s_overall")));
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
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        requested.gotApiData = true;
    }
}