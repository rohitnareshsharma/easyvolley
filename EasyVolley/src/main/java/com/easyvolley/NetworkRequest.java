package com.easyvolley;

import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.easyvolley.interceptors.Interceptor;
import com.easyvolley.interceptors.impl.GzipInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Volley StringRequest extension to support features like {@link Interceptor}.
 * Default header set and customised etag support. This same request is used
 * for all type Http requests for now (GET|POST)
 *
 * Client app should not ideally use this class and should stick to only
 * using {@link NetworkClient} for making network request.
 *
 * For example {@link NetworkClient#get(String)} for get type network request.
 *
 * @author rohitsharma
 */
public class NetworkRequest extends StringRequest {

    // Form params fields. This will be used for Network request type POST
    private Map<String, String> mParams = new HashMap<>();

    // Raw request body. This will be used for Network request type POST
    private byte[] mRequestBody;

    /**
     * Request headers. There is default set of headers which is pre added
     * to the headers field.
     */
    private HashMap<String, String> mHeaders = new HashMap<String, String>(){{
        // Default headers of network request will go here.
        put("Accept-Encoding", "gzip");
    }};

    // List of interceptors for modification of the original response
    private ArrayList<Interceptor> interceptors = new ArrayList<Interceptor>() {{
        // Default interceptors of NetworkRequest will go hear
        add(new GzipInterceptor());
    }};

    /**
     * NetworkRequest constructor with all method type supported.
     * See {@link Method}
     *
     * @param method Type of request (GET|POST) etc
     * @param url Target url of this request
     * @param headers Request headers
     * @param params  Request params. These are form fields. Primarily for post request.
     * @param requestBody Raw request body
     * @param listener request success event listener
     * @param errorListener request failure event listener
     */
    public NetworkRequest(int method, String url,
                          Map<String, String> headers,
                          Map<String, String> params,
                          byte[] requestBody,
                          Response.Listener<String> listener,
                          Response.ErrorListener errorListener) {

        super(method, url, listener, errorListener);

        // Add the supplied headers to the request header map
        if(headers != null) {
            mHeaders.putAll(headers);
        }

        // Add the supplied form params. This will be primarily used for POST requests
        if(params != null) {
            mParams.putAll(params);
        }

        mRequestBody = requestBody;
    }

    //Network Request constructor for GET type of request.
    public NetworkRequest(String url,
                          Map<String, String> headers,
                          Response.Listener<String> listener,
                          Response.ErrorListener errorListener) {
        this(Method.GET, url, headers, null, null, listener, errorListener);
    }

    /**
     * Add interceptor for the network response. See {@link Interceptor}
     * Gzip is natively supported by the framework. See {@link GzipInterceptor}
     * for example implementation.
     *
     * Each interceptor will receive {@link NetworkResponse} object.
     *
     * @param interceptor Custom interceptor to be registered in the Network request.
     */
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    // Return the headers for this request. This will be used by Volley network engine
    @Override
    public Map<String, String> getHeaders() {

        // Etag Support
        Cache.Entry entry= NetworkClient.getRequestQueue().getCache().get(getCacheKey());
        if (entry != null && !TextUtils.isEmpty(entry.etag)) {
            mHeaders.put("If-None-Match", entry.etag);
        }

        return mHeaders;
    }

    // Return the raw body for this request. This will be used by Volley network engine
    @Override
    public byte[] getBody() {
        return mRequestBody;
    }

    // Return the params for request body. This will be used by Volley network engine
    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    // Handle the network response here. All interceptors will get the opportunity here.
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        // Check for interceptors if any
        for(Interceptor i : interceptors) {
            response = i.intercept(response);
        }

        return super.parseNetworkResponse(response);
    }

}
