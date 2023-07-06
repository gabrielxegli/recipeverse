package ch.zkb.recipeverse.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;

import ch.zkb.recipeverse.R;
import ch.zkb.recipeverse.control.Fetch;


public class MainActivity extends AppCompatActivity {
    Fetch fetch;
    MainActivity ctx;
    boolean succesfull = false;

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.main_text);

        RequestQueue queue = Volley.newRequestQueue(this);




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
                            "query", "pasta"
                    }
            };

            try {
                fetch.get("https://api.spoonacular.com/recipes/complexSearch", queries, (JSONObject json)->{
                    text.setText(json.toString());
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