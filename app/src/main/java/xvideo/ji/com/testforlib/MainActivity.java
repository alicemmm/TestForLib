package xvideo.ji.com.testforlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import xvideo.ji.com.jiad.JiHttpPostUtil;
import xvideo.ji.com.jiad.ObserverCallBack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        JiHttpUtils.test(MainActivity.this);

        final Map<String, String> map = new HashMap<>();
        final String url = "http://192.168.5.110:88/";

        JiHttpPostUtil.requestByPost(MainActivity.this, new ObserverCallBack() {
            @Override
            public void callback(String data) {
                Log.e("TAG", "UI callback data=" + data);
            }
        }, url, map);
    }
}
