package com.easyvolley.interceptors.impl;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.easyvolley.interceptors.ResponseInterceptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * GZIP interceptor to convert gzipped bytes to default charset.
 * See {@link ResponseInterceptor}
 *
 * @author rohitsharma
 */
public class GzipInterceptor implements ResponseInterceptor {

    @Override
    public NetworkResponse intercept(NetworkResponse response) {

        String encoding = response.headers.get("Content-Encoding");
        if (response.data != null) {
            if (encoding != null && encoding.equals("gzip")) {
                Log.d("GzipInterceptor", "Content-Encoding : gzip");
                try {
                    GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
                    ByteArrayOutputStream os = new ByteArrayOutputStream();

                    // 4K buffer
                    byte[] buffer = new byte[4 * 1024];
                    int len;

                    // read bytes from the input stream and store them in buffer
                    while ((len = gStream.read(buffer)) != -1) {
                        // write bytes from the buffer into output stream
                        os.write(buffer, 0, len);
                    }

                    byte[] output = os.toByteArray();

                    response = new NetworkResponse(response.statusCode,
                                                   output,
                                                   response.notModified,
                                                   response.networkTimeMs,
                                                   response.allHeaders);

                } catch (IOException e) {
                    // We can safely ignore this
                }
            }
        }
        return response;
    }
}
