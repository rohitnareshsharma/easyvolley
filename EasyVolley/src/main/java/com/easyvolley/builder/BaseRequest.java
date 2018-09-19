package com.easyvolley.builder;

import android.net.Uri;

import com.android.volley.VolleyError;
import com.easyvolley.Callback;
import com.easyvolley.EasyVolleyError;
import com.easyvolley.NetworkClient;
import com.easyvolley.NetworkPolicy;
import com.easyvolley.NetworkRequest;
import com.easyvolley.dispatcher.ResponseDispatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for network request builder.
 * It is generics enabled for supporting builder pattern
 * of its subclass while preserving common methods in
 * its class declaration itself.
 *
 * T will be subclass type BaseRequest always.
 *
 * @param <T>
 *
 * @author rohitsharma
 */
public abstract class BaseRequest<T extends BaseRequest> {

    // Target url of the network request
    private String mUrl;

    // Callback from the client implementation for receiving network request success/fail events.
    private Callback mCallback;

    // Request header set from the client implementation to be added in NetworkRequest
    private Map<String, String> mHeaders;

    // Uri builder for url modification. Like adding a query parameter.
    private Uri.Builder builder;

    // Network policy of the request.
    private NetworkPolicy mNetworkPolicy = NetworkPolicy.DEFAULT;

    /*package*/ BaseRequest(String url) {
        mUrl = url;
    }

    /*package*/ String getUrl() {
        // See if builder is used for url modification if not return the url as is.
        if(builder == null) return mUrl;

        return builder.build().toString();
    }

    /*package*/ Map<String, String> getHeaders() {
        return mHeaders;
    }

    /**
     * Sets the {@link Callback} for the network request for
     * success/failure events listening.
     *
     * @param callBack Callback from client implementation.
     * @return current object
     */
    public T setCallback(Callback callBack) {
        if(callBack != null) mCallback = callBack;
        return (T)this;
    }

    /**
     * Adds all the map entries into request header set.
     *
     * @param headers Map of custom headers.
     * @return current object
     */
    public T addHeader(Map<String, String> headers) {
        if(mHeaders == null) mHeaders = new HashMap<>();
        mHeaders.putAll(headers);

        return (T)this;
    }

    /**
     * Add single header entry to the network request
     *
     * @param key String header key
     * @param value String header value
     * @return current builder object
     */
    public T addHeader(String key, String value) {
        if(mHeaders == null) mHeaders = new HashMap<>();
        mHeaders.put(key, value);

        return (T)this;
    }

    /**
     * Add query param to the supplied url.
     *
     * @param param String query param key
     * @param value String query param value
     * @return current builder object
     */
    public T addQueryParam(String param, String value) {

        if(builder == null) builder = Uri.parse(mUrl).buildUpon();
        builder.appendQueryParameter(param, value);

        return (T)this;
    }

    /**
     * Set the network policy of the request. See {@link NetworkPolicy}
     *
     * @param networkPolicy Network policy of the request.
     * @return current builder object
     */
    public T setNetworkPolicy(NetworkPolicy networkPolicy) {
        if(networkPolicy != null) mNetworkPolicy = networkPolicy;
        return (T)this;
    }

    /**
     * Gets the current network policy of the request.
     *
     * @see NetworkPolicy
     * @return
     */
    public NetworkPolicy getNetworkPolicy() {
        return mNetworkPolicy;
    }

    /**
     * Internal Volley response handler. It will dispatch response
     * correctly to the client.
     *
     * @param response String converted network response
     */
    /*package*/ void onResponse(String response) {
        ResponseDispatcher.getInstance().dispatch(mCallback, response);
    }

    /**
     * Internal Volley error handler.
     *
     * @param error {@link VolleyError}
     */
    /*package*/ void onError(VolleyError error) {
        if(mCallback != null) {
            mCallback.onError(EasyVolleyError.from(error));
        }
    }

    /**
     * Add the request to network/cache queue
     * @param request
     */
    protected void add(NetworkRequest request) {
        if(mNetworkPolicy == NetworkPolicy.OFFLINE) {
            NetworkClient.addCacheOnlyRequest(request);
            return;
        }

        // See if no cache mode is requested.
        request.setShouldCache(mNetworkPolicy != NetworkPolicy.NO_CACHE);

        // Enqueue it to network queue
        NetworkClient.addNetworkRequest(request);
    }

    /**
     * Client specific implementation required for different kind of network request. (GET/POST)
     */
    public abstract void execute();

}
