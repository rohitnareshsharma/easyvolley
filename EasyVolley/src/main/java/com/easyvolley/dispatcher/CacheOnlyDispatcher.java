/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.easyvolley.dispatcher;

import android.os.Process;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ResponseDelivery;
import com.android.volley.VolleyError;
import com.easyvolley.BuildConfig;
import com.easyvolley.NetworkRequest;

import java.util.concurrent.BlockingQueue;


/**
 * Provides a thread for performing cache only triage on a queue of requests.
 *
 * Requests added to the specified cache queue are resolved from cache.
 * Any deliverable response is posted back to the caller via a
 * {@link ResponseDelivery}.  Cache misses and responses that require
 * refresh are treated as error cases.
 */
public class CacheOnlyDispatcher extends Thread {

    // TAG for logging
    private static final String TAG = CacheOnlyDispatcher.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    /** The queue of requests coming in for triage. */
    private final BlockingQueue<NetworkRequest> mCacheOnlyQueue;

    /** The cache to read from. */
    private final Cache mCache;

    /** For posting responses. */
    private final ResponseDelivery mDelivery;

    /** Used for telling us to die. */
    private volatile boolean mQuit = false;

    /**
     * Creates a new cache triage dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param cacheOnlyQueue Queue of incoming requests for triage
     * @param cache Cache interface to use for resolution
     * @param delivery Delivery interface to use for posting responses
     */
    public CacheOnlyDispatcher(
            BlockingQueue<NetworkRequest> cacheOnlyQueue,
            Cache cache, ResponseDelivery delivery) {
        mCacheOnlyQueue = cacheOnlyQueue;
        mCache = cache;
        mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        if (DEBUG) Log.v(TAG, "start new CacheOnlyDispatcher");
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        // Make a blocking call to initialize the cache.
        mCache.initialize();

        while (true) {
            try {
                processRequest();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
            }
        }
    }

    // Extracted to its own method to ensure locals have a constrained liveness scope by the GC.
    // This is needed to avoid keeping previous request references alive for an indeterminate amount
    // of time. Update consumer-proguard-rules.pro when modifying this. See also
    // https://github.com/google/volley/issues/114
    private void processRequest() throws InterruptedException {
        // Get a request from the cache triage queue, blocking until
        // at least one is available.
        final NetworkRequest request = mCacheOnlyQueue.take();
        request.addMarker("cache-queue-take");

        // If the request has been canceled, don't bother dispatching it.
        if (request.isCanceled()) {
            return;
        }

        // Attempt to retrieve this item from cache.
        Cache.Entry entry = mCache.get(request.getCacheKey());
        if (entry == null) {
            request.addMarker("cache-miss");
            // Cache miss; send err.
            mDelivery.postError(request, new VolleyError("No Cache Available"));
            return;
        }

        // If it is completely expired, just send it to the network.
        if (entry.isExpired()) {
            request.addMarker("cache-hit-expired");
            request.setCacheEntry(entry);
            // Cache miss; send err.
            mDelivery.postError(request, new VolleyError("Cache has expired"));
            return;
        }

        // We have a cache hit; parse its data for delivery back to the request.
        request.addMarker("cache-hit");
        Response<?> response = request.parseNetworkResponse(
                new NetworkResponse(entry.data, entry.responseHeaders));
        request.addMarker("cache-hit-parsed");

        mDelivery.postResponse(request, response);
    }
}
