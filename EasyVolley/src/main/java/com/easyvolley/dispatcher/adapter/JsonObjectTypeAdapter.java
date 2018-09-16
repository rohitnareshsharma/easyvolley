package com.easyvolley.dispatcher.adapter;

import android.util.Log;

import com.easyvolley.Callback;
import com.easyvolley.dispatcher.TypeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JsonObject type response mapping handling.
 * @see TypeAdapter
 *
 * @author rohitsharma
 */
public class JsonObjectTypeAdapter implements TypeAdapter {

    @Override
    public void processResponse(Callback callback, String response) {
        if(callback != null) {
            try {
                callback.onSuccess(new JSONObject(response));
            } catch (JSONException e) {
                Log.e("JsonObjectTypeAdapter", e.getMessage(), e);
                callback.onError(e.getMessage());
            }
        }
    }

}
