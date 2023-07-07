package ch.zkb.recipeverse.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import ch.zkb.recipeverse.AppDatabase;
import ch.zkb.recipeverse.R;
import ch.zkb.recipeverse.control.Fetch;
import ch.zkb.recipeverse.model.Recipe;
import ch.zkb.recipeverse.model.RecipeDao;

import com.bumptech.glide.Glide;


public class MainActivity extends AppCompatActivity {
    Fetch fetch;
    MainActivity ctx;

    TextView text;
    LinearLayout saved_layout;
    LinearLayout vegi_layout;
    LinearLayout italian_layout;
    LinearLayout noodlesLayout;


    Button searchButton;
    EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;

        searchButton = findViewById(R.id.search_button);
        searchInput = findViewById(R.id.search_input);

        searchButton.setOnClickListener((view)->  {
            String value = searchInput.getText().toString();

            if(value.length() < 1){
                Toast.makeText(MainActivity.this, "Please enter valid text to search", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, SearchActivity.class);

            intent.putExtra("searchValue",value );

            startActivity(intent);
        });

        saved_layout = findViewById(R.id.saved_layout);
        noodlesLayout = findViewById(R.id.noodles_layout);
        vegi_layout = findViewById(R.id.vegi_layout);
        italian_layout = findViewById(R.id.italian_layout);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "recipeverse").build();

        AsyncTask.execute(()->{

            RecipeDao recipeDao = db.recipeDao();

            List<Recipe> recipes = recipeDao.getAll();
            Log.i("DB", Integer.toString(recipes.size()));
            runOnUiThread(()->{
                for (Recipe recipe : recipes) {
                    // ImageView
                    ImageView imageView = new ImageView(ctx);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 300);

                    params.setMargins(0,0,0,5);

                    imageView.setLayoutParams(params);

                    // TextView
                    TextView text = new TextView(ctx);
                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(500, 100);

                    text.setText(recipe.title);
                    text.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    text.setLayoutParams(textParams);


                    // LinearLayout
                    LinearLayout container = new LinearLayout(ctx);

                    container.setOnClickListener((view)->{
                        Intent intent = new Intent(MainActivity.this, RecipeActivity.class);

                        intent.putExtra("id", recipe.id);

                        startActivity(intent);
                    });

                    container.setOrientation(LinearLayout.VERTICAL);

                    container.addView(imageView);
                    container.addView(text);

                    saved_layout.addView(container);

                    Glide.with(ctx).load(recipe.image).centerCrop().into(imageView);
                }


            });



            
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "recipeverse").build();

        AsyncTask.execute(()->{
            saved_layout.removeAllViewsInLayout();

            RecipeDao recipeDao = db.recipeDao();

            List<Recipe> recipes = recipeDao.getAll();
            Log.i("DB", Integer.toString(recipes.size()));
            runOnUiThread(()->{
                for (Recipe recipe : recipes) {
                    // ImageView
                    ImageView imageView = new ImageView(ctx);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 300);

                    params.setMargins(0,0,0,5);

                    imageView.setLayoutParams(params);

                    // TextView
                    TextView text = new TextView(ctx);
                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(500, 100);

                    text.setText(recipe.title);
                    text.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    text.setLayoutParams(textParams);


                    // LinearLayout
                    LinearLayout container = new LinearLayout(ctx);

                    container.setOnClickListener((view)->{
                        Intent intent = new Intent(MainActivity.this, RecipeActivity.class);

                        intent.putExtra("id", recipe.id);

                        startActivity(intent);
                    });

                    container.setOrientation(LinearLayout.VERTICAL);

                    container.addView(imageView);
                    container.addView(text);

                    saved_layout.addView(container);

                    Glide.with(ctx).load(recipe.image).centerCrop().into(imageView);
                }


            });

        });
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

            createRow("noodles,main course", 2, noodlesLayout);

            createRow("vegetarian,main course", 2, vegi_layout);

            createRow("italian,main course", 2, italian_layout);

        }

        public void createRow(String tag, int amount, LinearLayout layout){

            String[][] queries = {
                    {
                            "tags", tag
                    },
                    {
                            "number", Integer.toString(amount)
                    }
            };

            try {
                fetch.get("https://api.spoonacular.com/recipes/random", queries, (JSONObject json)->{
                    try {
                        JSONArray jsonArray = json.getJSONArray("recipes");

                        Log.i("JSON", jsonArray.toString());

                        int len = jsonArray.length();

                        for (int i = 0; i < len; i++) {
                            JSONObject entry = jsonArray.getJSONObject(i);

                            int id = entry.getInt("id");
                            String title = entry.getString("title");
                            String image = entry.getString("image");

                            // ImageView
                            ImageView imageView = new ImageView(ctx);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 300);

                            params.setMargins(0,0,0,5);

                            imageView.setLayoutParams(params);

                            // TextView
                            TextView text = new TextView(ctx);
                            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(500, 100);

                            text.setText(title);
                            text.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                            text.setLayoutParams(textParams);


                            // LinearLayout
                            LinearLayout container = new LinearLayout(ctx);

                            container.setOnClickListener((view)->{
                                Intent intent = new Intent(MainActivity.this, RecipeActivity.class);

                                intent.putExtra("id", id);

                                startActivity(intent);
                            });

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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}