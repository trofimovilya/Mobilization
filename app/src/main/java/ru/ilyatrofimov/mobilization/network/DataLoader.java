package ru.ilyatrofimov.mobilization.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import ru.ilyatrofimov.mobilization.MobilizationApp;


/**
 * Util class to async fetch data from net
 *
 * @author Ilya Trofimov
 */
public final class DataLoader {
    // JSON Data URL
    private static final String URL
            = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

    /**
     * Gets Volley instance and requests for json
     * Posts EventBus events on successful or error response
     */
    public static void requestJSON() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                EventBus.getDefault().post(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EventBus.getDefault().post(error);
            }
        });

        VolleySingleton.getInstance().getRequestQueue().add(request);
    }

    /**
     * Singleton according Google's guides to have one request queue on whole app
     */
    public static final class VolleySingleton {
        private static VolleySingleton sInstance = null;
        private RequestQueue mRequestQueue;

        private VolleySingleton() {
            mRequestQueue = Volley.newRequestQueue(MobilizationApp.getAppContext());
        }

        public static VolleySingleton getInstance() {
            if (sInstance == null) {
                sInstance = new VolleySingleton();
            }

            return sInstance;
        }

        public RequestQueue getRequestQueue() {
            return mRequestQueue;
        }
    }

    private DataLoader() {
        // To prevent creating instances
    }
}
