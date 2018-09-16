package com.easyvolley.dispatcher.adapter;

import android.util.Log;

import com.easyvolley.Callback;
import com.easyvolley.dispatcher.TypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * JsonArray type response mapping handling.
 * @see TypeAdapter
 *
 * @author rohitsharma
 */
public class JsonArrayTypeAdapter implements TypeAdapter {

    @Override
    public void processResponse(Callback callback, String response) {
        if(callback != null) {
            try {
                callback.onSuccess(new JSONArray(response));
            } catch (JSONException e) {
                Log.e("JsonArrayTypeAdapter", e.getMessage(), e);
                callback.onError(e.getMessage());
            }
        }
    }

}
