package com.easyvolley;

import com.android.volley.VolleyError;

import java.util.Map;

/**
 * Error detail wrapper. We intentionally gave this extra wrapper
 * above {@link VolleyError} because we do not want to expose
 * volley lib dependency to consumer. That will help in a faster
 * build times. implementaion is used and not api in gradle.
 *
 * NOTE: Most of its field can be null. Make sure to check before use.
 * @author rohitsharma
 */
public class EasyVolleyError extends Exception {

    /** General message about the error */
    public final String mMessage;

    /** Round trip time in milliseconds */
    public final long mNetworkTimeMs;

    /** The HTTP status code. */
    public final int mStatusCode;

    /** Raw data from this response. */
    public final byte[] mData;

    /** Raw headers from the response. */
    public final Map<String, String> mHeaders;

    /**
     * Creates EasyVolleyError instance based on raw data provided.
     *
     * @param message Error message
     * @param networkTimeMs Round trip time in millisecond
     * @param statusCode http status code of network request
     * @param data raw body coming in the error response.
     * @param headers raw headers cominf from the error response.
     */
    public EasyVolleyError(String message, long networkTimeMs, int statusCode, byte[] data, Map<String, String> headers) {
        super(message);
        mMessage = message;
        mNetworkTimeMs = networkTimeMs;
        mStatusCode = statusCode;
        mData = data;
        mHeaders = headers;
    }

    /**
     * EasyVolley Error creation based on VolleyError
     * @param error Volley Error received from Volley Request
     */
    private EasyVolleyError(VolleyError error) {
        this(error.getMessage(),
                error.getNetworkTimeMs(),
                error.networkResponse.statusCode,
                error.networkResponse.data,
                error.networkResponse.headers);
    }

    /**
     * EasyVolleyError instance creation based on String message only.
     * @param message error message
     */
    private EasyVolleyError(String message) {
        this(message, 0, -1, null, null);
    }

    /**
     * Utility method to create EasyVolleyError based on volley error
     * @param error VolleyError instance
     *
     * @return EasyVolleyError constructed EasyVolleyError instance.
     */
    public static EasyVolleyError from(VolleyError error) {
        EasyVolleyError easyVolleyError;
        if(error == null) {
            easyVolleyError = new EasyVolleyError("Something went wrong");
        } else if(error.networkResponse == null) {
            easyVolleyError = new EasyVolleyError(error.getMessage(), error.getNetworkTimeMs(),
                                                  -1, null, null);
        } else {
            easyVolleyError = new EasyVolleyError(error);
        }
        return easyVolleyError;
    }

    /**
     * Utility method to create EasyVolleyError based on volley error
     * @param errorMessage String error message
     *
     * @return EasyVolleyError constructed EasyVolleyError instance.
     */
    public static EasyVolleyError from(String errorMessage) {
        return new EasyVolleyError(errorMessage);
    }
}
