package com.easyvolley.dispatcher;

import com.easyvolley.Callback;
import com.easyvolley.EasyVolleyResponse;

/**
 * Interface to allow {@link ResponseDispatcher} to
 * parse the network response as per custom requirements.
 *
 * If a type match is found. Then default GSON parsing is skipped.
 * See {@link ResponseDispatcher#resgisterTypeAdapter(Class, TypeAdapter)}
 *
 * @author rohitsharma
 */
public interface TypeAdapter {
    void processResponse(Callback callback, String responseBody, EasyVolleyResponse easyVolleyResponse);
}
