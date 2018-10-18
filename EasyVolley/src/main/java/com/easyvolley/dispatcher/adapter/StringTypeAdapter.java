package com.easyvolley.dispatcher.adapter;

import com.easyvolley.Callback;
import com.easyvolley.EasyVolleyResponse;
import com.easyvolley.dispatcher.TypeAdapter;

/**
 * String type response mapping handling.
 * @see TypeAdapter
 *
 * @author rohitsharma
 */
public class StringTypeAdapter implements TypeAdapter {

    @Override
    public void processResponse(Callback callback, String responseBody, EasyVolleyResponse response) {
        if(callback != null) {
            callback.onSuccess(responseBody, response);
        }
    }

}
