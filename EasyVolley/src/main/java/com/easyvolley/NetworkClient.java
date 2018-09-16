package com.easyvolley;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.easyvolley.builder.GetRequest;
import com.easyvolley.builder.PostRequest;

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

    // App context
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    // SingleTon
    @SuppressLint("StaticFieldLeak")
    private static NetworkClient instance;

    // Volley request queue
    private RequestQueue mRequestQueue;

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
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);

            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
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
     * Core NetworkClient method to create Get type request.
     * @param url Target network URL
     * @return {@link GetRequest} builder object with various network related methods available.
     */
    public static GetRequest get(String url) {
        return new GetRequest(url);
    }

    /**
     * Core NetworkClient method to create Post type request.
     * @see PostRequest#setRequestBody(String) for setting the raw body
     * with the Post request.
     *
     * @param url Target network URL
     * @return {@link PostRequest} builder object with various network related methods available.
     */
    public static PostRequest post(String url) {
        return new PostRequest(url);
    }

}
