package xvideo.ji.com.jiad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Domon on 16-2-19.
 */
public class JiHttpPostUtil {
    private static String[] sessionId = null;

    public static final int REQUEST_TIMEOUT = 10 * 1000;//设置请求超时10秒钟
    public static final int SO_TIMEOUT = 10 * 1000;  //设置等待数据超时时间10秒钟
    static final int POST = 1; // post 提交
    static final int GET = 2; // get 提交

    static String tag = JiHttpPostUtil.class.getSimpleName();

    public static DefaultHttpClient mHttpClient;

    public static String sendPostMessage(Map<String, String> params, String encode, String url_path) {
        URL url;
        StringBuffer buffer = new StringBuffer();
        try {
            url = new URL(url_path);
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buffer.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
                }
                buffer.deleteCharAt(buffer.length() - 1);
            }
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestMethod("POST");

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            if (sessionId != null) {
                if (sessionId[0] != null) {
                    urlConnection.setRequestProperty("cookie", sessionId[0]);
                }
            }

            byte[] mydata = buffer.toString().getBytes();

            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(mydata.length));

            //获得输出流，向服务器输出数据
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(mydata, 0, mydata.length);
            outputStream.close();

            //获得服务器响应的结果和状态码
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == 200) {
                String ret = changeInputStream(urlConnection.getInputStream(), encode);
                Log.e(JiHttpPostUtil.class.getSimpleName(), ret);
                return ret;
            } else {
                Log.e(JiHttpPostUtil.class.getSimpleName(), responseCode + "=responseCode");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将一个输入流转换成指定编码的字符串
     *
     * @param inputStream
     * @param encode
     * @return
     */
    private static String changeInputStream(InputStream inputStream, String encode) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                result = new String(outputStream.toByteArray(), encode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取网络图片资源
     *
     * @param url
     * @return Bitmap
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);

            String session_value = conn.getHeaderField("Set-Cookie");
            sessionId = session_value.split(";");
            conn.connect();

            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 异步请求网络数据
     *
     * @param sendType 请求类型
     * @param map      参数
     * @param context
     * @param callBack 回调对象
     * @param url
     */
    private static void doAsynRequest(final int sendType,
                                      final Map<String, String> map, final Context context,
                                      final ObserverCallBack callBack, final String url
    ) {

        // 请求
        ThreadPoolUtils.execute(new MyRunnable(sendType, map, context, callBack, url));
    }

    /**
     * 访问网络初始化函数 支持Post请求方式
     *
     * @param context
     * @param callBack 回调执行函数 支持线程
     * @param url      每个执行url
     * @param map      参数
     */
    public static void requestByPost(Context context,
                                     final ObserverCallBack callBack, String url, Map<String, String> map) {

        //组织URL
        StringBuffer buffer = new StringBuffer();
        buffer.append(url);

        String requestUrl = buffer.toString();
        Log.i("httpurl", requestUrl);
        // 异步请求数据
        doAsynRequest(POST, map, context, callBack, requestUrl);
    }

    static class MyRunnable implements Runnable {
        final int sendType;
        final Map<String, String> map;
        final Context context;
        final ObserverCallBack callBack;
        final String url;

        public MyRunnable(final int sendType,
                          final Map<String, String> map, final Context context,
                          final ObserverCallBack callBack, final String url) {
            this.sendType = sendType;
            this.map = map;
            this.context = context;
            this.callBack = callBack;
            this.url = url;
        }

        @Override
        public void run() {
            String data = null;
            try {
                // 设置请求头超时请求参数
                BasicHttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, JiHttpPostUtil.REQUEST_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParams, JiHttpPostUtil.SO_TIMEOUT);
                if (JiHttpPostUtil.mHttpClient == null) {
//				AnsynHttpRequest.mHttpClient = new DefaultHttpClient(httpParams);
                    DefaultHttpClient client = new DefaultHttpClient(httpParams);
                    ClientConnectionManager mgr = client.getConnectionManager();
                    HttpParams params = client.getParams();
                    client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
                    JiHttpPostUtil.mHttpClient = client;
                }
                HttpResponse response = null;
                switch (sendType) {
                    case JiHttpPostUtil.GET: // get 方式提交
                        HttpGet get = new HttpGet(url);
                        response = JiHttpPostUtil.mHttpClient.execute(get);
                        break;
                    case JiHttpPostUtil.POST: // post 方式提交
                        HttpPost post = new HttpPost(url);
                        Log.i("httpurl", url + map.toString());
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        if (map != null && map.size() > 0)
                            for (String key : map.keySet()) {
                                params.add(new BasicNameValuePair(key, map.get(key)));
                            }
                        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                        post.setEntity(entity);
                        response = JiHttpPostUtil.mHttpClient.execute(post);
                        break;
                    default:
                        break;
                }

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    data = EntityUtils.toString(response.getEntity());
                    Log.e(tag, "respone data= " + data);
                } else {
                    data = null;
                    Log.e(tag, "respone data= " + null);
                    Log.e(tag, response.getStatusLine().getStatusCode() + "");
                }
            } catch (Exception e) {
                Log.e(JiHttpPostUtil.tag, e.getMessage());
                data = null;
            }

            try { // 回调数据
                if (callBack != null) {
                    callBack.callback(data);
                    if (data != null) Log.i("httpurl", data);
                }
            } catch (Exception e) {
                Log.e(JiHttpPostUtil.tag, e.getMessage());
            }
        }
    }
}
