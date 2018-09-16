package com.easyvolley.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.easyvolley.Callback;
import com.easyvolley.NetworkClient;

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
                    public void onError(String errorMessage) {

                    }
                }).execute();

        NetworkClient.get("http://demo0736492.mockable.io/test")
                .setCallback(new Callback<Test>() {
                    @Override
                    public void onSuccess(Test o) {
                        text2.setText(o.msg + " Response Recieved");
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                }).execute();

        NetworkClient.get("http://demo0736492.mockable.io/test2")
                .setCallback(new Callback<List<Test>>() {
                    @Override
                    public void onSuccess(List<Test> o) {
                        text3.setText(o.size() + "");
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                }).execute();

        NetworkClient.post("http://demo0736492.mockable.io/postTest")
                .setCallback(new Callback<Test>() {
                    @Override
                    public void onSuccess(Test o) {
                        text4.setText("Post success " + o.msg);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        text4.setText(errorMessage);
                    }
                }).execute();

    }
}
