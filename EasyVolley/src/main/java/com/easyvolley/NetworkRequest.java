package com.easyvolley;

import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.easyvolley.interceptors.ResponseInterceptor;
import com.easyvolley.interceptors.impl.GzipInterceptor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Volley StringRequest extension to support features like {@link ResponseInterceptor}.
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

    // Response Listener
    private Response.Listener<String> mResponseListener;

    /**
     * Request headers. There is default set of headers which is pre added
     * to the headers field.
     */
    private HashMap<String, String> mHeaders = new HashMap<String, String>(){{
        // Default headers of network request will go here.
        put("Accept-Encoding", "gzip");
    }};

    // Network response object. It will be null in case request fails.
    private NetworkResponse networkResponse;

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

        mResponseListener = listener;

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
    public Map<String, String> getParams() {
        return mParams;
    }


    public Response.Listener<String> getResponseListener() {
        return mResponseListener;
    }

    // Handle the network response here. All responseInterceptors will get the opportunity here.
    @Override
    public Response<String> parseNetworkResponse(NetworkResponse response) {

        // Set the response reference to send it client in the end.
        this.networkResponse = response;

        List<ResponseInterceptor> responseInterceptors = NetworkClient.getResponseInterceptor();

        // Check for responseInterceptors if any
        for(ResponseInterceptor i : responseInterceptors) {
            response = i.intercept(response);
        }

        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }

        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    /**
     * @return NetworkResponse It will be null in case request fails.
     */
    public NetworkResponse getNetworkResponse() {
        return networkResponse;
    }
}
