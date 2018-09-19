package com.easyvolley.builder;

import com.android.volley.Request;
import com.easyvolley.NetworkRequest;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Post type network request builder.
 * @see BaseRequest
 *
 * @author rohitsharma
 */
public class PostRequest extends BaseRequest<PostRequest> {

    // Form field data
    private Map<String, String> mParams;

    // Raw request body
    private byte[] mRequestBody;

    public PostRequest(String url) {
        super(url);
    }

    /**
     * Sets the raw request body for the network request.
     * @param requestBody String form of request body
     * @return current builder object
     */
    public PostRequest setRequestBody(String requestBody) {
        if(requestBody != null) mRequestBody = requestBody.getBytes(Charset.forName("UTF-8"));
        return this;
    }

    /**
     * Sets the raw request body for the network request.
     * @param requestBody byte[] form of request body
     * @return current builder object
     */
    public PostRequest setRequestBody(byte[] requestBody) {
        if(requestBody != null) mRequestBody = requestBody;

        return this;
    }

    /**
     * Add params to the network request body.
     * It is form field data pairs.
     *
     * @param params Map with multiple param entries. All will be added
     * @return current builder object
     */
    public PostRequest addParams(Map<String, String> params) {
        if(mParams == null) mParams = new HashMap<>();
        mParams.putAll(params);
        return this;
    }

    /**
     * Add params to the network request body.
     * Single pair of form field data pair is added.
     *
     * @param param key of form field
     * @param value value of form field
     * @return current builder object
     */
    public PostRequest addParams(String param, String value) {
        if(mParams == null) mParams = new HashMap<>();
        mParams.put(param, value);
        return this;
    }

    /**
     * Create the POST type {@link NetworkRequest} from the builder data
     * and enqueue it to the Volley request queue.
     */

    public void execute() {
        if(getUrl() == null) throw new IllegalArgumentException("Empty URL for network request");

        // Create the request
        NetworkRequest request = new NetworkRequest(Request.Method.POST,
                getUrl(), getHeaders(), mParams, mRequestBody, this::onResponse, this::onError);

        // Enqueue it
        add(request);
    }

}
