package xvideo.ji.com.testforlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import xvideo.ji.com.jiad.JiHttpPostUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        JiHttpUtils.test(MainActivity.this);

        final Map<String, String> map = new HashMap<>();
////        final String url = "http://www.baidu.com";
        final String url = "http://192.168.5.110:88/";
//
        new Thread(new Runnable() {
            @Override
            public void run() {
                JiHttpPostUtil.sendPostMessage(map, "UTF-8", url);
            }
        }).start();

//        final Map<String, Object> map2 = new HashMap<>();
//        Task task = new Task(Task.USER_LOGIN, map2);
//        MainService.newTask(task);
//
//        MainService.addActivity(this);


    }
}
