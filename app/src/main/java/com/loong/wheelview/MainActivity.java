package com.loong.wheelview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WheelView<String> wheelView = (WheelView<String>) findViewById(R.id.wheel);
        List<String> ss = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            ss.add("Item"+i);
        }
        wheelView.setAdapter(new WheelView.AdapterImpl<String>() {
            @Override
            public void onBindItem(TextView textView, String o) {
                textView.setText(o);
            }
        });
        wheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(String s) {
                Log.e("---","item:" + s);
            }
        });
        wheelView.setData(ss);
    }
}
