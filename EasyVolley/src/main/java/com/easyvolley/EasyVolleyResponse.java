package com.easyvolley;

import com.android.volley.NetworkResponse;

import java.util.Map;

public class EasyVolleyResponse {

    /**
     * The HTTP status code.
     */
    public final int mStatusCode;

    /**
     * Raw data from this response.
     */
    public final byte[] mData;

    /**
     * Raw headers from the response.
     */
    public final Map<String, String> mHeaders;

    /**
     * True if the server returned a 304 (Not Modified).
     */
    public final boolean mNotModified;

    /**
     * Network round trip time in milliseconds.
     */
    public final long mNetworkTimeMs;

    /**
     * Creates EasyVolleyResponse instance based on raw data provided.
     *
     * @param statusCode    http status code of network request
     * @param data          raw body coming in the network response.
     * @param headers       raw headers cominf from the error response.
     * @param notModified   flag indicating content not modified over server.
     * @param networkTimeMs Round trip time in millisecond
     */
    public EasyVolleyResponse(int statusCode,
                              byte[] data,
                              Map<String, String> headers,
                              boolean notModified,
                              long networkTimeMs) {

        mStatusCode = statusCode;
        mData = data;
        mHeaders = headers;
        mNotModified = notModified;
        mNetworkTimeMs = networkTimeMs;
    }

    /**
     * EasyVolleyResponse creation based on Volley NetworkResponse
     *
     * @param response Volley NetworkResponse received for the request
     */
    public EasyVolleyResponse(NetworkResponse response) {
        this(response.statusCode,
                response.data,
                response.headers,
                response.notModified,
                response.networkTimeMs);
    }

    @Override
    public String toString() {
        return "{ StatusCode : " + mStatusCode +
               ", Data : " + mData +
               ", mHeaders : " + mHeaders +
               ", mNetworkTimeMs : " + mNetworkTimeMs +
               " }";
    }
}
