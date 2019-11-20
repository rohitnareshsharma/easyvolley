package com.easyvolley;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.easyvolley.dispatcher.CacheOnlyDispatcher;
import com.easyvolley.interceptors.RequestInterceptor;
import com.easyvolley.interceptors.ResponseInterceptor;
import com.easyvolley.interceptors.impl.GzipInterceptor;
import com.easyvolley.okhttp3.VolleyOkHttp3StackInterceptors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * NetworkClient framework for android app. It has currently wrapped
 * Volley at its core. GSON is used for object serialization/deserialization.
 *
 * @see #get(String) for Get type Network request.
 * @see #post(String) for Post type Network request.
 *
 * Simple Exaample of usage
 *
 * <code>
 *     NetworkClient.get("http://demo0736492.mockable.io/test")
 *         .setCallback(new Callback<String>(){
 *              public void onSuccess(String result) {
 *                 Log.d("NetworkClient", result)
 *              }
 *              public void onError(String errorMessage) {
 *
 *              }
 *           }).execute();
 * </code>
 *
 * @author rohitsharma
 */
public class NetworkClient {

    // Disk cache size in bytes
    private static int defaultDiskCacheSize = 20 * 1024 * 1024;

    /**
     * Default socket of all the requests.
     * @see #setDefaultSocketTimeoutMs(int) to change it for all the requests.
     * @see NetworkRequestBuilder#setSocketTimeoutMS(int) for overriding it for individual request
     */
    static int defaultSocketTimeoutMs = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;

    // App context
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    // SingleTon
    @SuppressLint("StaticFieldLeak")
    private static NetworkClient instance;

    // Volley request queue
    private RequestQueue mRequestQueue;

    /** The cache triage queue. */
    private PriorityBlockingQueue<NetworkRequest> mCacheOnlyQueue;

    private CacheOnlyDispatcher mCacheOnlyDispatcher;

    // List of responseInterceptors for modification of the original response
    private ArrayList<ResponseInterceptor> responseInterceptors = new ArrayList<ResponseInterceptor>() {{
        // Default responseInterceptors of NetworkRequest will go hear
        add(new GzipInterceptor());
    }};

    // List of requestInterceptors for modification of the original response
    private ArrayList<RequestInterceptor> requestInterceptors = new ArrayList<RequestInterceptor>() {{
        // Default requestInterceptors of NetworkRequest will go hear

    }};

    /**
     * Add interceptor for the network response. See {@link ResponseInterceptor}
     * Gzip is natively supported by the framework. See {@link GzipInterceptor}
     * for example implementation.
     *
     * Each interceptor will receive {@link NetworkResponse} object.
     *
     * @param  responseInterceptor Custom interceptor to be registered.
     */
    public static void addResponseInterceptor(ResponseInterceptor responseInterceptor) {
        instance.responseInterceptors.add(responseInterceptor);
    }

    /**
     * Add interceptor for the network request. See {@link ResponseInterceptor}
     * Gzip is natively supported by the framework. See {@link GzipInterceptor}
     * for example implementation.
     *
     * Each interceptor will receive {@link NetworkResponse} object.
     *
     * @param  requestInterceptor Custom interceptor to be registered.
     */
    public static void addRequestInterceptor(RequestInterceptor requestInterceptor) {
        instance.requestInterceptors.add(requestInterceptor);
    }

    /**
     * @return The list of response interceptor
     */
    public static List<ResponseInterceptor> getResponseInterceptor() {
        return instance.responseInterceptors;
    }

    /**
     * @return The list of request interceptor
     */
    public static List<RequestInterceptor> getRequestInterceptor() {
        return instance.requestInterceptors;
    }

    /**
     * Initialise the network client. Call this method preferably
     * in {@link Application#onCreate()}
     * @param app Application context.
     */
    public static void init(Application app) {
        // Lets prepare the client
        if(instance == null) {
            instance = new NetworkClient(app);
        }
    }

    /**
     * Initialise the network client. Call this method preferably
     * in {@link Application#onCreate()}
     *
     * @param app Application context.
     * @param socketTimeoutMs default socket timeout for all the requests.
     */
    public static void init(Application app, int socketTimeoutMs) {
        init(app);
        defaultSocketTimeoutMs = socketTimeoutMs;
    }

    // For single ton implementation
    private NetworkClient(Context context) {
        // Save the app context so that our instance remain throughout the app lifespan.
        mContext = context;

        initRequestQueue();
    }

    //Lets initialise our network client to use 20MB disk cache by default
    private void initRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mContext.getCacheDir(), defaultDiskCacheSize);
            Network network = new BasicNetwork(new VolleyOkHttp3StackInterceptors());
            mRequestQueue = new RequestQueue(cache, network);

            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
    }

    // init app cache only request queue.
    private void initCacheOnlyRequestQueue() {
        // Kill any previous if alive
        if(mCacheOnlyDispatcher != null) {
            mCacheOnlyDispatcher.quit();
        }

        mCacheOnlyQueue = new PriorityBlockingQueue<>();

        mCacheOnlyDispatcher = new CacheOnlyDispatcher(mCacheOnlyQueue, mRequestQueue.getCache(),
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));

        mCacheOnlyDispatcher.start();
    }

    /**
     * Set the disk cache size for network client.
     * Default is 20MB.
     * @param diskCacheSizeBytesMs New disk cache size
     */
    public static void setDiskCacheSizeBytes(int diskCacheSizeBytesMs) {
        defaultDiskCacheSize = diskCacheSizeBytesMs;
    }

    /**
     * Return the network request queue.
     * Activity code should not use this method.
     * @see NetworkClient for available interface for client code
     * for making any type of request.
     * @return {@link RequestQueue} instance, This should be single instance per app session.
     */
    public static RequestQueue getRequestQueue() {
        return instance.mRequestQueue;
    }

    /**
     * Add request to network request queue.
     * Activity code should not use this method.
     * @see NetworkClient for available interface for client code
     * for making any type of request.
     */
    public static void addNetworkRequest(Request<?> request) {
        instance.mRequestQueue.add(request);
    }

    /**
     * Add request to cache only queue.
     * Activity code should not use this method.
     * @see NetworkClient for available interface for client code
     * for making any type of request.
     */
    public static void addCacheOnlyRequest(NetworkRequest networkRequest) {

        if(instance.mCacheOnlyQueue == null) {
            instance.initCacheOnlyRequestQueue();
        }

        networkRequest.setSequence(instance.mRequestQueue.getSequenceNumber());
        instance.mCacheOnlyQueue.add(networkRequest);
    }

    /**
     * Drop all the network cache.
     */
    public static void dropAllCache() {
        instance.mRequestQueue.getCache().clear();
    }

    /**
     * Drop all the network cache.
     */
    public static void dropCache(String key) {
        instance.mRequestQueue.getCache().remove(key);
    }

    /**
     * Set default socket of all the requests.
     *
     * @see NetworkRequestBuilder#setSocketTimeoutMS(int) for overriding it for individual request
     * @param socketTimeoutMs socket timeout in milliseconds.
     */
    public static void setDefaultSocketTimeoutMs(int socketTimeoutMs) {
        defaultSocketTimeoutMs = socketTimeoutMs;
    }

    /**
     * Core NetworkClient method to create Get type request.
     * @param url Target network URL
     * @return {@link NetworkRequestBuilder} builder object with various network related methods available.
     */
    public static NetworkRequestBuilder get(String url) {
        return new NetworkRequestBuilder(url, Request.Method.GET);
    }

    /**
     * Core NetworkClient method to create Post type request.
     * @see NetworkRequestBuilder#setRequestBody(String) for setting the raw body
     * with the Post request.
     *
     * @param url Target network URL
     * @return {@link NetworkRequestBuilder} builder object with various network related methods available.
     */
    public static NetworkRequestBuilder post(String url) {
        return new NetworkRequestBuilder(url, Request.Method.POST);
    }

    /**
     * Core NetworkClient method to create Put type request.
     * @see NetworkRequestBuilder#setRequestBody(String) for setting the raw body
     * with the Put request.
     *
     * @param url Target network URL
     * @return {@link NetworkRequestBuilder} builder object with various network related methods available.
     */
    public static NetworkRequestBuilder put(String url) {
        return new NetworkRequestBuilder(url, Request.Method.PUT);
    }

    /**
     * Core NetworkClient method to create Delete type request.
     *
     * @param url Target network URL
     * @return {@link NetworkRequestBuilder} builder object with various network related methods available.
     */
    public static NetworkRequestBuilder delete(String url) {
        return new NetworkRequestBuilder(url, Request.Method.DELETE);
    }

    /**
     * Core NetworkClient method to create HEAD type request.
     *
     * @param url Target network URL
     * @return {@link NetworkRequestBuilder} builder object with various network related methods available.
     */
    public static NetworkRequestBuilder head(String url) {
        return new NetworkRequestBuilder(url, Request.Method.HEAD);
    }


    /**
     * Core NetworkClient method to create OPTIONS type request.
     *
     * @param url Target network URL
     * @return {@link NetworkRequestBuilder} builder object with various network related methods available.
     */
    public static NetworkRequestBuilder options(String url) {
        return new NetworkRequestBuilder(url, Request.Method.OPTIONS);
    }

    /**
     * Core NetworkClient method to create TRACE type request.
     *
     * @param url Target network URL
     * @return {@link NetworkRequestBuilder} builder object with various network related methods available.
     */
    public static NetworkRequestBuilder trace(String url) {
        return new NetworkRequestBuilder(url, Request.Method.TRACE);
    }

    /**
     * Core NetworkClient method to create Patch type request.
     *
     * @param url Target network URL
     * @return {@link NetworkRequestBuilder} builder object with various network related methods available.
     */
    public static NetworkRequestBuilder patch(String url) {
        return new NetworkRequestBuilder(url, Request.Method.PATCH);
    }

}
