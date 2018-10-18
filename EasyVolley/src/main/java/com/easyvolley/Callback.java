package com.easyvolley;

import android.util.Log;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Network Request response callback. It will throw {@link RuntimeException}
 * in case non generic Callback is passed to the {@link NetworkClient}.
 * @param <T>
 *
 * @author rohitsharma
 */
public interface Callback<T> {

    // Success event delegate. It will received GSON deserialized object response
    void onSuccess(T t, EasyVolleyResponse response);

    // Failure event delegate. We need to pass more details here.
    void onError(EasyVolleyError error);

    /**
     * This is sorcery. This is done to know the Type passed in Callback
     * so that gson can work on object mapping without client passing the
     * Type class additionaly.
     */
    default Type getGenericType() {
        Type type;

        try {
            type = ((ParameterizedType) getClass()
                    .getGenericInterfaces()[0]).getActualTypeArguments()[0];
        } catch (Exception e) {
            Log.e("Callback", e.getMessage(), e);
            throw new RuntimeException("You must use a proper Generics based Callback");
        }

        return type;
    }
}
