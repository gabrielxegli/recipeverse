package ch.zkb.recipeverse.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.zkb.recipeverse.AppDatabase;
import ch.zkb.recipeverse.R;
import ch.zkb.recipeverse.control.Fetch;
import ch.zkb.recipeverse.model.Recipe;
import ch.zkb.recipeverse.model.RecipeDao;

public class RecipeActivity extends AppCompatActivity {
    Fetch fetch;

    ImageView imageView;
    TextView recipeTitle;
    RatingBar healthBar;
    TextView summary;

    FloatingActionButton save;
    int id;
    Recipe recipe;

    RecipeActivity ctx;

    boolean saved =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        imageView = findViewById(R.id.recipe_image);
        recipeTitle = findViewById(R.id.recipe_title);
        healthBar = findViewById(R.id.health_bar);
        summary = findViewById(R.id.summary);
        save = findViewById(R.id.save);

        ctx = this;

        Intent intent = getIntent();

        id = intent.getIntExtra("id", -1);

        if(id == -1){
            Toast.makeText(this, "Not a valid Id", Toast.LENGTH_SHORT).show();

            finish();
        }

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "recipeverse").build();

        AsyncTask.execute(()->{
            RecipeDao recipeDao = db.recipeDao();

            Recipe recipe = recipeDao.getByID(id);

            if(recipe != null) {


                runOnUiThread(() -> {
                    save.setImageResource(R.drawable.baseline_delete_24);

             });
            }
        });

        save.setOnClickListener((view -> {
            AsyncTask.execute(()->{
                RecipeDao recipeDao = db.recipeDao();

                if(saved){
                    recipeDao.delete(recipe);
                    runOnUiThread(() -> {
                        save.setImageResource(R.drawable.baseline_save_24);

                    });
                }else{
                    recipeDao.insertAll(recipe);
                    runOnUiThread(() -> {
                        save.setImageResource(R.drawable.baseline_delete_24);

                    });
                }

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                Toast.makeText(ctx, "**Vibrating**", Toast.LENGTH_LONG);

            });

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

            String[][] queries = {};

            try {
                fetch.get("https://api.spoonacular.com/recipes/"+id+"/information", queries, (JSONObject json)->{
                    try {
                        int id = json.getInt("id");

                        Log.i("JSON", json.toString());

                        String image = json.getString("image");
                        Glide.with(ctx).load(image).centerCrop().into(imageView);

                        String title = json.getString("title");
                        recipeTitle.setText(title);

                        double hScore = json.getDouble("healthScore");
                        healthBar.setRating((float) hScore);
                        healthBar.setStepSize(0.5f);

                        String summaryHtml = json.getString("summary");
                        summary.setText(Html.fromHtml(summaryHtml, Html.FROM_HTML_MODE_COMPACT));

                        recipe = new Recipe(id, title, image, hScore, summaryHtml);

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