package com.easyvolley.dispatcher.adapter;

import com.easyvolley.Callback;
import com.easyvolley.dispatcher.TypeAdapter;

/**
 * String type response mapping handling.
 * @see TypeAdapter
 *
 * @author rohitsharma
 */
public class StringTypeAdapter implements TypeAdapter {

    @Override
    public void processResponse(Callback callback, String response) {
        if(callback != null) {
            callback.onSuccess(response);
        }
    }

}
