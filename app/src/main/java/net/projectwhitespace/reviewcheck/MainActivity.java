package net.projectwhitespace.reviewcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static int revisitId;
    public static String url = "";
    private EditText txb_url;
    public static List<itemResult> searchHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadSettings();

        // Setting display mode to portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txb_url = findViewById(R.id.txb_url);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        // Check if App got Link via "share" feature
        if(Intent.ACTION_SEND.equals(action) && type != null){
            if(type.equals("text/plain")) {
                Log.d("ReviewMeta", "Recieved Intent was: " + intent.getStringExtra(Intent.EXTRA_TEXT));
                url = intent.getStringExtra(Intent.EXTRA_TEXT);
                txb_url.setText(url);
                Toast.makeText(getApplicationContext(), R.string.url_auto_entered, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.information:
                // Start informaiton activity
                Intent informationIntent = new Intent(this, information.class);
                startActivity(informationIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( searchHistory.size() > 0) {
            LinearLayout linearLayout = findViewById(R.id.lst_searchHistory);

            linearLayout.removeAllViews();

            for (int i = searchHistory.size() - 1; i >= 0; i--) {
                LinearLayout verticalLayout = new LinearLayout(this);
                verticalLayout.setOrientation(LinearLayout.VERTICAL);

                Button button = new Button(this);
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setId(i);
                // Set Text
                itemResult result = searchHistory.get(i);

                String name = result.getName();
                if(result.getName().length() > 50)
                    name = result.getName().substring(0,50) + "...";
                String text = name + "\n" + "Rating: " + result.getRating() + "\tAmazon Typ: " + result .getAmazonType();
                button.setText(text);

                // OnClickListener
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        MainActivity.revisitId = view.getId();
                        Intent myIntent = new Intent(MainActivity.this, requestrevisit.class);
                        MainActivity.this.startActivity(myIntent);
                    }
                });

                ImageView imageView = new ImageView(this);
                Glide.with(getApplicationContext())
                        .load(result.getPictureURL())
                        .override(300,300)
                        .into(imageView);

                verticalLayout.addView(imageView);
                verticalLayout.addView(button);
                linearLayout.addView(verticalLayout);
            }
        }
    }

    public void CheckItem(View v){
        try {
            // Grab text from textbox
            String textboxText = txb_url.getText().toString();

            // Check if something is in it
            if (textboxText.length() > 0) {
                String[] splittedText = textboxText.split(" ");
                url = splittedText[splittedText.length - 1];

                // Check if string from box is a URL
                if(URLUtil.isValidUrl(MainActivity.url)) {
                    // Filter ASIN from URL with RegEx
                    Pattern pattern = Pattern.compile(".*/([a-zA-Z0-9]{10})(?:[/?]|$).*");
                    Matcher matcher = pattern.matcher(url);
                    if(matcher.matches()) {
                        // ASIN was found in URL
                        requested.ASIN = matcher.group(1);
                        Intent requestedIntent = new Intent(this, requested.class);
                        startActivity(requestedIntent);
                    }
                    else {
                        // No ASIN was found in URL
                        Toast.makeText(getApplicationContext(), R.string.no_ASIN_found, Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    // Text was not a URL
                    Toast.makeText(getApplicationContext(),R.string.not_a_url,Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),R.string.no_URL_Entered,Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            Log.d("ReviewMeta", Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSettings();
    }

    // Save/Load
    public void saveSettings(){
        SharedPreferences preferences = getSharedPreferences("ReviewCheckData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(searchHistory);

        editor.putString("historyList", json);
        editor.apply();
    }

    public void loadSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("ReviewCheckData", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("historyList", "");
        if(!json.equals("")) {
            Type type = new TypeToken<List<itemResult>>() {}.getType();
            MainActivity.searchHistory = gson.fromJson(json, type);
        }
    }

    public void clearHistory(View v){
        searchHistory.clear();
        LinearLayout horizontalLayout = findViewById(R.id.lst_searchHistory);
        horizontalLayout.removeAllViews();
        Toast.makeText(getApplicationContext(), R.string.history_cleared, Toast.LENGTH_SHORT).show();
    }
}
