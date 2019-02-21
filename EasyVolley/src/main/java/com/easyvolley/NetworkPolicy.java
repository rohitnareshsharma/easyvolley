package com.easyvolley;

public enum NetworkPolicy {

    /** Ignore disk cache and force network request. Response will not be cached also */
    NO_CACHE,

    /** Check through disk cache only. No network */
    OFFLINE,

    /** Ignore disk cache and force network request. Response will be cached */
    IGNORE_READ_BUT_WRITE_CACHE,

    /** Check with cache if valid return from it else make network call */
    DEFAULT

}
