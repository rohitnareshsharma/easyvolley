package com.easyvolley.dispatcher;

import android.util.Log;

import com.easyvolley.Callback;
import com.easyvolley.dispatcher.adapter.JsonArrayTypeAdapter;
import com.easyvolley.dispatcher.adapter.JsonObjectTypeAdapter;
import com.easyvolley.dispatcher.adapter.StringTypeAdapter;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Dispatches the response to the callback. Its core functionality
 * is to map the network response to the callback generic supplied POJO.
 *
 * It is gelling Volley with GSON to achieve this effect.
 * It also allows custom TypeAdapter to intercept GSON parsing.
 *
 * @author rohitsharma.
 */
public class ResponseDispatcher {

    // Tag for logging
    private static final String TAG = ResponseDispatcher.class.getSimpleName();

    // Singleton Instance
    private static ResponseDispatcher instance;

    // Gson Builder. Currently we are using default settings
    private GsonBuilder gson = new GsonBuilder();

    /** Map of custom {@link TypeAdapter} */
    private HashMap<Type, TypeAdapter> typeAdapterHashMap = new HashMap<>();

    // Singleton restriction.
    private ResponseDispatcher() {

        // Add the default supported TypeAdapters
        resgisterTypeAdapter(String.class, new StringTypeAdapter());
        resgisterTypeAdapter(JSONObject.class, new JsonObjectTypeAdapter());
        resgisterTypeAdapter(JSONArray.class, new JsonArrayTypeAdapter());
    }

    /**
     * Register a custom {@link TypeAdapter} for Response dispatcher
     *
     * @param t Type of Callback generic argument. Callback<T>. It is T.class
     * @param adapter Adapter implementation
     */
    public void resgisterTypeAdapter(Type t, TypeAdapter adapter) {
        typeAdapterHashMap.put(t, adapter);
    }

    /** @return Return the instance of the dispatcher. */
    public static ResponseDispatcher getInstance() {
        if(instance == null) {
            instance = new ResponseDispatcher();
        }

        return instance;
    }

    // Core dispatch method. TypeAdaptor intercepting will happen here.
    public void dispatch(Callback callback, String response) {
        if(callback != null) {

            Type type  = callback.getGenericType();
            Log.d(TAG, "Callback Type is " + type);

            // Intercept for registered TypeAdaptors
            TypeAdapter adapter = typeAdapterHashMap.get(type);
            if(adapter != null) {
               adapter.processResponse(callback, response);
               return;
            }

            //Else Parse Using GSON. Must be for Custom POJO
            callback.onSuccess(gson.create().fromJson(response, type));
        }
    }
}
