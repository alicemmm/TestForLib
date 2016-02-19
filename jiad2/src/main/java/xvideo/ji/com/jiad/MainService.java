package xvideo.ji.com.jiad;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Domon on 16-2-19.
 */
public class MainService extends Service implements Runnable {
    // 任务队列：用于存放任务的队列
    private static Queue<Task> tasks = new LinkedList<Task>();

    // 将需要更新的UI添加到集合中
    private static ArrayList<Activity> appActivities = new ArrayList<Activity>();

    private boolean isRun;// 是否运行线程

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Task.USER_LOGIN: {// 用户登录 :更新UI
                    //根据name找到activity：因为MAinActivity实现了MainActivityInter接口，所以可以强转为MainActivityInter类型
                    MainActivityInterface activity = (MainActivityInterface) getActivityByName("MainActivity");
                    activity.refresh(msg.obj.toString());
                    break;
                }
                default:
                    break;
            }

        }

        ;
    };

    /**
     * 添加任务到任务队列中
     */
    public static void newTask(Task t) {
        tasks.add(t);
    }

    @Override
    public void onCreate() {

        isRun = true;
        Thread thread = new Thread(this);
        thread.start();

        super.onCreate();
    }

    /**
     * 让服务一直遍历执行
     */
    public void run() {

        while (isRun) { // 去监听任务
            Task task = null;
            if (!tasks.isEmpty()) { // 判断队列中是否有值
                task = tasks.poll();// 执行完任务后把改任务从任务队列中移除
                if (null != task) {
                    doTask(task); // TO DO ：执行任务
                }
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    // 处理任务
    private void doTask(Task task) {
        Message msg = handler.obtainMessage();
        msg.what = task.getTaskId();

        switch (task.getTaskId()) {
            case Task.USER_LOGIN: { // 用户登录
                HashMap<String, Object> paramsHashMap = (HashMap<String, Object>) task.getTaskParams();

                //访问网络，进行判断用户是否存在
                String url = "http://172.23.252.89:8080/igouServ/userlogin.action";
                BaseHttpClient httpClient = new BaseHttpClient();
                try {
                    String result = httpClient.post(url, paramsHashMap);
                    msg.obj = result; //返回到handler进行处理
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }

            default:
                break;
        }

        handler.sendMessage(msg);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 添加一个Activity对象到集合中
     */
    public static void addActivity(Activity activity) {

        if (!appActivities.isEmpty()) {

            for (Activity ac : appActivities) {
                if (ac.getClass().getName().equals(ac.getClass().getName())) {
                    appActivities.remove(ac);
                    break;
                }
            }
        }
        appActivities.add(activity);
    }

    /**
     * 根据Activity的Name获取Activity对象
     */
    private Activity getActivityByName(String name) {

        if (!appActivities.isEmpty()) {
            for (Activity activity : appActivities) {
                if (null != activity) {
                    if (activity.getClass().getName().indexOf(name) > 0) {
                        return activity;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 退出系统
     */
    public static void appExit(Context context) {
        // Finish 所有的Activity
        for (Activity activity : appActivities) {
            if (!activity.isFinishing())
                activity.finish();
        }

        // 结束 Service
        Intent service = new Intent("com.xuliugen.frame.task.MainService");
        context.stopService(service);

    }
}
