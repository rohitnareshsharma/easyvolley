package com.easyvolley.interceptors;

import com.android.volley.NetworkResponse;

/**
 * Interface for classes who want to intercept the network response before delivering
 * it to the callback. It works on {@link NetworkResponse}. It receives it as a param
 * and its implementation responsibility to return a proper NetworkResponse
 * after intercepting it.
 *
 * @author rohitsharma
 */
public interface ResponseInterceptor {
    NetworkResponse intercept(NetworkResponse response);
}
