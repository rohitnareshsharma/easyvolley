package com.easyvolley.interceptors;

import com.android.volley.NetworkResponse;
import com.easyvolley.NetworkRequest;

/**
 * Interface for classes who want to intercept the network request before executing it
 * It works on {@link NetworkRequest}. It receives it as a param
 * and its implementation responsibility to return a proper NetworkRequest
 * after intercepting it.
 *
 * @author rohitsharma
 */
public interface RequestInterceptor {
    NetworkRequest intercept(NetworkRequest response);
}
