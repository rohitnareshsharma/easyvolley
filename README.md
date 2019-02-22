<img src=https://raw.githubusercontent.com/rohitnareshsharma/easyvolley/master/assets/readme_header.jpg >

# Easyvolley
Volley + GSON + Easy Interface

Networking is a part of every application we build these days. Not only just getting the data 
from the servers is important, But also to parse it in usable form is equally important.

Both of these operations needs to be fast and light on memory, thread and cpu usage.
Thanks to open source communities. We have networking libraries like 
<a href="https://github.com/google/volley">Volley</a> and JSON mappers like 
<a href="https://github.com/google/gson">GSON</a> that do these jobs beautifully.

EasyVolley is a wrapper that has combined both of these great utilities and also has provided
a super easy wrapper classes for easy integration with apps. 
Wrapper code is super light. Hardly 500 lines of code. 

Why Volley and not the Retrofit? I personally like Volley because of its simplicity and so easy 
customization. Also i feel it pain declaring interfaces just for network calls. I like bare naked
url endpoint. Smaller the code better it is.

It is using okhttp3-urlconnection at the core of it so it is HTTP 2.0 compliant. 

# Caching

Caching is super important to ensure our responses are fast and bandwidth usage is minimal.
Volley is configured to use 20MB disk cache. Make sure to use cache-control and expiry headers
properly in the network response header coming from servers. 
You can change this disk cache size by using 

```java
NetworkClient.setDiskCacheSizeBytes(diskCacheSizeBytesMs);
```

ETAG support auto works in requests. You will get cached copy if server is returning 304. 


# Gradle
```groovy
implementation 'com.spandexship:easyvolley:0.3.0'
```

# How to use it

Add this line in your application onCreate().
```java
NetworkClient.init(this);
```

You are done. 

<b>Whatever Type you will pass in the Callback generics tag. 
   Your response will be parsed accodringly</b>

Now make GET request like below to get raw response in String.
```java
        NetworkClient.get("http://demo0736492.mockable.io/test")
                .setCallback(new Callback<String>() {
                    @Override
                    public void onSuccess(String o, EasyVolleyResponse response) {
                        Log.d(TAG, "Response is + o");
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        Log.e(TAG, "Something Went Wrong " + error.mMessage);
                    }
                }).execute();
```

Make GET request auto mapped to a POJO.
```java
        NetworkClient.get("http://demo0736492.mockable.io/test")
                .setCallback(new Callback<Test>() {
                    @Override
                    public void onSuccess(Test o, EasyVolleyResponse response) {
                        Log.d(TAG, "Response Recieved" + o.msg);
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        Log.e(TAG, "Something Went Wrong " + error.mMessage);
                    }
                }).execute();
                    
        // Test class 
        public class Test {
            String msg;
            int id;
        }              
```

Make GET request auto mapped to a JSONObject.
```java
        NetworkClient.get("http://demo0736492.mockable.io/test")
                .setCallback(new Callback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject o, EasyVolleyResponse response) {
                        Log.d(TAG, "Response Recieved" + o);
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        Log.e(TAG, "Something Went Wrong " + error.mMessage);
                    }
                }).execute();
```

Make POST request and response auto mapped to a POJO.
```java
        NetworkClient.post("http://demo0736492.mockable.io/postTest")
                .setRequestBody("Test Post Body")
                .setCallback(new Callback<Test>() {
                    @Override
                    public void onSuccess(Test o, EasyVolleyResponse response) {
                        Log.d(TAG, "Response Recieved" + o.msg);
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        Log.e(TAG, "Something Went Wrong " + error.mMessage);
                    }
                }).execute();
```

# NetworkPolicy

Framework support 4 network policies for request.

1. NetworkPolicy.NO_CACHE : Ignore disk cache and force network request. Response will not be cached also
2. NetworkPolicy.OFFLINE : Check through disk cache only. No network.
3. NetworkPolicy.IGNORE_READ_BUT_WRITE_CACHE : Ignore disk cache and force network request. Response will be cached.
3. NetworkPolicy.DEFAULT : Check with cache if valid return from it else make network call.

```java
        NetworkClient.get("http://demo0736492.mockable.io/test")
                .setNetworkPolicy(NetworkPolicy.OFFLINE)
                .setCallback(new Callback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject o) {
                        Log.d(TAG, "Response Recieved" + o);
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        Log.e(TAG, "Something Went Wrong " + error.mMessage);
                    }
                }).execute();
```

# Request and Response Interceptors

Framework support integrating apps to provide HTTP request interceptor and Response interceptors.
This is helpful for cases like you would want to log perticular request data, or change anything
before executing, or like adding some thing common to each requests.

```java
        NetworkClient.addRequestInterceptor(new RequestInterceptor() {
            @Override
            public NetworkRequest intercept(NetworkRequest request) {
                return request;
            }
        });
        
        NetworkClient.addResponseInterceptor(new ResponseInterceptor() {
            @Override
            public NetworkResponse intercept(NetworkResponse response) {
                return response;
            }
        });
```

