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
 * @author Ilya Trofimov
 */
public final class DataLoader {
    private static final String URL
            = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

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

    private DataLoader() {
    }

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
}
