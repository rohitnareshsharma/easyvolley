package com.easyvolley.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.easyvolley.Callback;
import com.easyvolley.EasyVolleyError;
import com.easyvolley.EasyVolleyResponse;
import com.easyvolley.NetworkClient;
import com.easyvolley.NetworkPolicy;

/**
 * Sample Activity
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkClient.init(getApplication());
        text1 = findViewById(R.id.text1);

        findViewById(R.id.btn_make_normal_request).setOnClickListener(v -> {
            NetworkClient.get("http://demo0736492.mockable.io/test2")
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
        });

        findViewById(R.id.btn_make_offline_request).setOnClickListener(v -> {
            NetworkClient.get("http://demo0736492.mockable.io/test2")
                    .setNetworkPolicy(NetworkPolicy.OFFLINE)
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
        });

        findViewById(R.id.btn_make_ignore_read_update_cache_request).setOnClickListener(v -> {
            NetworkClient.get("http://demo0736492.mockable.io/test2")
                    .setNetworkPolicy(NetworkPolicy.IGNORE_READ_BUT_WRITE_CACHE)
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
        });

    }
}
