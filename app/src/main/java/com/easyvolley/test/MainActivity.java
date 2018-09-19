package com.easyvolley.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easyvolley.Callback;
import com.easyvolley.EasyVolleyError;
import com.easyvolley.NetworkClient;
import com.easyvolley.NetworkPolicy;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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
                    public void onSuccess(String o) {
                        text1.setText(o);
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        text1.setText(error.mMessage + " Error Occured");
                    }
                })
                .execute();


        NetworkClient.get("http://demo0736492.mockable.io/test2")
                .setCallback(new Callback<List<Test>>() {
                    @Override
                    public void onSuccess(List<Test> o) {
                        text2.setText(o.size() + "");
                    }

                    @Override
                    public void onError(EasyVolleyError error) {
                        text2.setText(error.mMessage + " Error Occured");
                    }
                }).execute();

        NetworkClient.post("http://demo0736492.mockable.io/postTest")
                .setCallback(new Callback<Test>() {
                    @Override
                    public void onSuccess(Test o) {
                        text3.setText("Post success " + o.msg);
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
                        public void onSuccess(Test o) {
                            text4.setText(o.msg + " Offline Test");
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
