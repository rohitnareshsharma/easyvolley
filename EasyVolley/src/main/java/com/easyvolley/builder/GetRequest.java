package com.easyvolley.builder;

import com.easyvolley.NetworkClient;
import com.easyvolley.NetworkRequest;

/**
 * Get type network request builder.
 * @see BaseRequest
 *
 * @author rohitsharma
 */
public class GetRequest extends BaseRequest<GetRequest> {

    /**
     * GetRequest constructor for GET type network request building.
     *
     * @param url Network request target url.
     */
    public GetRequest(String url) {
        super(url);
    }

    /**
     * Create the GET type {@link NetworkRequest} from the builder data
     * and enqueue it to the Volley request queue.
     */
    @Override
    public void execute() {
        if(getUrl() == null) {
            throw new IllegalArgumentException("Empty URL for network request");
        }

        // Create the request
        NetworkRequest request = new NetworkRequest(getUrl(), getHeaders(), this::onResponse, this::onError);

        // Enqueue it
        NetworkClient.getRequestQueue().add(request);
    }

}
