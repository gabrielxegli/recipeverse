package ch.zkb.recipeverse.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.zkb.recipeverse.R;
import ch.zkb.recipeverse.control.Fetch;

public class SearchActivity extends AppCompatActivity {
    SearchActivity ctx;
    Fetch fetch;
    EditText input;
    LinearLayout layout;
    Button button;

    String query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ctx = this;

        input = findViewById(R.id.search_input);
        button =findViewById(R.id.search_button);
        layout = findViewById(R.id.result_container);

        Intent intent = getIntent();

        query = intent.getStringExtra("searchValue");

        input.setText(query);

        button.setOnClickListener((view -> {
            layout.removeAllViewsInLayout();

            query = input.getText().toString();

            search(query, 20, layout);
        }));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService.
        Intent intent = new Intent(this, Fetch.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            Fetch.FetchBinder binder = (Fetch.FetchBinder) service;
            fetch = binder.getService();

            search(query, 20 ,layout);
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void search(String query, int amount, LinearLayout layout){
        String[][] queries = {
                {
                        "query", query
                },
                {
                        "number", Integer.toString(amount)
                }
        };


        try {
            fetch.get("https://api.spoonacular.com/recipes/complexSearch", queries, (JSONObject json)->{
                try {
                    JSONArray jsonArray = json.getJSONArray("results");

                    int len = jsonArray.length();

                    for (int i = 0; i < len; i++) {
                        JSONObject entry = jsonArray.getJSONObject(i);

                        int id = entry.getInt("id");
                        String title = entry.getString("title");
                        String image = entry.getString("image");

                        // ImageView
                        ImageView imageView = new ImageView(ctx);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(350, 350);

                        params.setMargins(0,0,10,0);

                        imageView.setLayoutParams(params);

                        // TextView
                        TextView text = new TextView(ctx);

                        text.setText(title);
                        text.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);


                        // LinearLayout
                        LinearLayout container = new LinearLayout(ctx);

                        container.setOnClickListener((view)->{
                            Intent intent = new Intent(SearchActivity.this, RecipeActivity.class);

                            intent.putExtra("id", id);

                            startActivity(intent);
                        });

                        container.setOrientation(LinearLayout.HORIZONTAL);

                        container.addView(imageView);
                        container.addView(text);

                        layout.addView(container);

                        Glide.with(ctx).load(image).centerCrop().into(imageView);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}