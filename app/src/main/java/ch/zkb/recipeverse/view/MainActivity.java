package ch.zkb.recipeverse.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ch.zkb.recipeverse.R;
import ch.zkb.recipeverse.control.Fetch;

import com.bumptech.glide.Glide;


public class MainActivity extends AppCompatActivity {
    Fetch fetch;
    MainActivity ctx;
    boolean succesfull = false;

    TextView text;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;

        layout = findViewById(R.id.saved_layout);





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

            String[][] queries = {
                    {
                        "apiKey", "0b3d242f0f2c41e69d12fbfb2b866864"
                    },
                    {
                        "query", "noodles"
                    },
                    {
                        "number", "25"
                    }
            };

            try {
                fetch.get("https://api.spoonacular.com/recipes/complexSearch", queries, (JSONObject json)->{
                    try {
                        JSONArray jsonArray = json.getJSONArray("results");

                        int len = jsonArray.length();

                        for (int i = 0; i < len; i++) {
                            JSONObject entry = jsonArray.getJSONObject(i);

                            String title = entry.getString("title");
                            String image = entry.getString("image");

                            ImageView imageView = new ImageView(ctx);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 500);

                            params.setMargins(5,5,5,5);

                            imageView.setLayoutParams(params);

                            TextView text = new TextView(ctx);

                            text.setText(title);

                            LinearLayout container = new LinearLayout(ctx);

                            container.setOrientation(LinearLayout.VERTICAL);

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

            succesfull = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}