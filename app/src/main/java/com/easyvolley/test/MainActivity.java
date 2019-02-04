package com.easyvolley.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.easyvolley.Callback;
import com.easyvolley.EasyVolleyError;
import com.easyvolley.EasyVolleyResponse;
import com.easyvolley.NetworkClient;
import com.easyvolley.NetworkPolicy;
import com.easyvolley.NetworkRequest;
import com.easyvolley.NetworkRequestBuilder;
import com.easyvolley.interceptors.RequestInterceptor;
import com.easyvolley.interceptors.ResponseInterceptor;

import java.util.List;
import java.util.Map;

/**
 * Sample Activity
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkClient.init(getApplication());

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);

        NetworkClient.get("http://demo0736492.mockable.io/test")
                .setCallback(new Callback<String>() {
                    @Override
                    public void onSuccess(String o, EasyVolleyResponse response) {
                        text1.setText(o);
                        Log.d(TAG, response+ "");

                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        text1.setText(error.mMessage + " Error Occured");
                    }
                })
                .execute();

        NetworkClient.get("http://demo0736492.mockable.io/test2")
                .setPriority(NetworkRequestBuilder.Priority.HIGH)
                .setCallback(new Callback<List<Test>>() {
                    @Override
                    public void onSuccess(List<Test> o, EasyVolleyResponse response) {
                        text2.setText(o.size() + "");
                        Log.d(TAG, response+ "");
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        text2.setText(error.mMessage + " Error Occured");
                    }
                }).execute();


        NetworkClient.post("http://demo0736492.mockable.io/postTest")
                .setPriority(NetworkRequestBuilder.Priority.LOW)
                .setCallback(new Callback<Test>() {
                    @Override
                    public void onSuccess(Test o, EasyVolleyResponse response) {
                        text3.setText("Post success " + o.msg);
                        Log.d(TAG, response+ "");
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        text3.setText(error.mMessage + " Error Occured");
                    }
                }).execute();


        findViewById(R.id.btn_make_offline_request).setOnClickListener(v -> {
            NetworkClient.get("http://demo0736492.mockable.io/test")
                    .setNetworkPolicy(NetworkPolicy.OFFLINE)
                    .setCallback(new Callback<Test>() {
                        @Override
                        public void onSuccess(Test o, EasyVolleyResponse response) {
                            text4.setText(o.msg + " Offline Test");
                            Log.d(TAG, response + "");
                        }

                        @Override
                        public void onError(EasyVolleyError error) {
                            text4.setText(error.mMessage + " Error Occured for offline request");
                        }
                    }).execute();

        });

        findViewById(R.id.btn_drop_cache).setOnClickListener(v -> {
             NetworkClient.dropAllCache();
        });

    }
}
