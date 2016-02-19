package xvideo.ji.com.jiad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Domon on 16-2-19.
 */
public class JiHttpPostUtil {
    private static String[] sessionId = null;

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
            }else {
                Log.e(JiHttpPostUtil.class.getSimpleName(),responseCode + "=responseCode");
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
//获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
//设置超时时间为6000毫秒，conn.setConnectionTimeout(0);表示没有时间限制
            conn.setConnectTimeout(6000);
//连接设置获得数据流
            conn.setDoInput(true);
//记住session
            String session_value = conn.getHeaderField("Set-Cookie");
            sessionId = session_value.split(";");
            conn.connect();
//得到数据流
            InputStream is = conn.getInputStream();
//解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
//关闭数据流
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
// TODO 自动生成的 catch 块
        }
        return bitmap;
    }
}
