package com.easyvolley.dispatcher.adapter;

import android.util.Log;

import com.easyvolley.Callback;
import com.easyvolley.EasyVolleyError;
import com.easyvolley.EasyVolleyResponse;
import com.easyvolley.dispatcher.TypeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JsonObject type response mapping handling.
 *
 * @author rohitsharma
 * @see TypeAdapter
 */
public class JsonObjectTypeAdapter implements TypeAdapter {

    @Override
    public void processResponse(Callback callback, String responseBody, EasyVolleyResponse response) {
        if (callback != null) {
            try {
                callback.onSuccess(new JSONObject(responseBody), response);
            } catch (JSONException e) {
                Log.e("JsonObjectTypeAdapter", e.getMessage(), e);
                callback.onError(EasyVolleyError.from(e.getMessage()));
            }
        }
    }

}
