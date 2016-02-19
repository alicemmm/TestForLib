package xvideo.ji.com.jiad;

import android.content.Context;

import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Domon on 16-2-19.
 */
public class JiHttpManager {

    protected static final String base_url = "192.168.5.108:88";
    private static JiHttpManager mManager;
    protected long mHttpCount = 0L;
    private final int DEFAULT_THREAD_POOL_SIZE = 3;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    protected static JiHttpManager getInstance(Context context) {
        if (mManager == null) {
            mManager = new JiHttpManager();
        }
        return mManager;
    }

    protected String synRequest(Context context, String url, LinkedHashMap<String, Object> map, long flag) throws Exception {
        String result = "";

        JiHttpPost http = null;

        http = new JiHttpPost(context,base_url,flag);


        return result;
    }

}
