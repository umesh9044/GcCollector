package com.daily.gccollector.Volley;


import android.content.Context;
//import android.net.http.AndroidHttpClient;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyClient {

    private static VolleyClient requestQueueSingleton;
    private RequestQueue requestQueue;
    private static Context context;

    private VolleyClient(Context ctx){
        context = ctx;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyClient getInstance(Context context){
        if (requestQueueSingleton == null){
            requestQueueSingleton = new VolleyClient(context);
        }
        return requestQueueSingleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
}

