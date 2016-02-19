package xvideo.ji.com.jiad;

import android.content.Context;

/**
 * Created by Domon on 16-2-19.
 */
public class JiHttpPost extends JiHttpUtils {
    private final String Tag = JiHttpPost.class.getSimpleName();
    private String url;

    public JiHttpPost(Context context, String url, long flag) throws Exception {
        super(context, url);
        this.url = url;
        this.mConnectTimeout = 20000;
        this.mReadTimeout = 50000;
    }

//    public String post(String params) {
//
//    }

    public void close() {
        super.close();
    }
}
