package ch.zkb.recipeverse.control;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Fetch extends Service {
    private final IBinder binder = new FetchBinder();
    private final String apiKey = "55d4ab2d730d4f278cc4e533c8ae8517";
    private RequestQueue queue;

    public Fetch() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
    }

    public class FetchBinder extends Binder {
        public Fetch getService() {
            // Return this instance of LocalService so clients can call public methods.
            return Fetch.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void get(String url, String[][] queries, Response.Listener<JSONObject> cb) throws Exception {
        String finalUrl = url;


        finalUrl += "?apiKey="+ apiKey + (queries.length > 0 ? "&": "");


        for (int i = 0; i < queries.length; i++) {
            finalUrl += queries[i][0] + "=" + queries[i][1] + (i != queries.length -1 ? "&": "");
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                finalUrl,
                null,
                cb,
                (VolleyError error)->{

                        throw new RuntimeException(error);

                }
        );

        queue.add(jsonObjectRequest);
    }

}