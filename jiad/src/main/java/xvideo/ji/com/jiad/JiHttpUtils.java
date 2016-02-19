package xvideo.ji.com.jiad;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by Domon on 16-2-19.
 */
public class JiHttpUtils {
    public static final int TYPE_NET_WORK_DISABLED = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_CM_CU_WAP = 4;
    public static final int TYPE_CT_WAP = 5;
    public static final int TYPE_OTHER_NET = 6;

    protected int mConnectTimeout = 10000;
    protected int mReadTimeout = 300000;
    protected HttpURLConnection mConnection = null;
    public static final String CTWAP = "ctwap";
    public static final String CMWAP = "cmwap";
    public static final String WAP_3G = "3gwap";
    public static final String UNIWAP = "uniwap";
    public static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    protected JiHttpUtils(Context context, String url) throws JiConnectionException {
        try {
            URL uri = null;
            uri = new URL(url);
            int type = checkNetworkType(context);
            try {
                this.mConnection = ((HttpURLConnection) uri.openConnection());
            } catch (Exception e) {
                if (type == 4) {
                    this.mConnection = ((HttpURLConnection) uri.openConnection());
                    try {
                        Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
                        this.mConnection = ((HttpURLConnection) uri.openConnection(p));
                    } catch (Exception e1) {
                        this.mConnection = ((HttpURLConnection) uri.openConnection());
                    }
                }
                if (type != 5) {
                    return;
                }
            }
            try {
                Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80));
                this.mConnection = ((HttpURLConnection) uri.openConnection(p));
            } catch (Exception e2) {
                this.mConnection = ((HttpURLConnection) uri.openConnection());
            }
            return;
        } catch (Exception e) {
            throw new JiConnectionException(100, e);
        }
    }

    public static int checkNetworkType(Context mContext) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if ((networkInfo == null) || (!networkInfo.isAvailable())) {
                return 0;
            }
            int netType = networkInfo.getType();
            if (netType == 1) {
                return 1;
            }
            if (netType == 0) {
                Cursor c = mContext.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
                try {
                    if (c != null) {
                        c.moveToFirst();
                        String user = c.getString(c.getColumnIndex("user"));
                        if ((!TextUtils.isEmpty(user)) &&
                                (user.startsWith("ctwap"))) {
                            return 5;
                        }
                    }
                    String netMode = networkInfo.getExtraInfo();
                    if (netMode != null) {
                        netMode = netMode.toLowerCase();
                        if ((netMode.equals("cmwap")) || (netMode.equals("3gwap")) || (netMode.equals("uniwap"))) {
                            return 4;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
                if (c != null) {
                    c.close();
                }
            }
        } catch (Exception ex) {
            return 6;
        }
        return 6;
    }

    protected void setConnectTimeOut(int timeout) {
        this.mConnectTimeout = timeout;
    }

    protected void setReadTimeOut(int timeout) {
        this.mReadTimeout = timeout;
    }

    protected HttpURLConnection getConnection() {
        return this.mConnection;
    }

    protected void close() {
        if (this.mConnection != null) {
            try {
                this.mConnection.disconnect();
            } catch (Exception localException) {
            }
        }
    }

    public static String getWebContent(String url) {
        HttpGet request = new HttpGet(url);

        HttpParams params = new BasicHttpParams();

        HttpClient httpClient = new DefaultHttpClient(params);
        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity());
                return content;
            }
        } catch (Exception localException) {
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        httpClient.getConnectionManager().shutdown();

        return null;
    }

    public static String requestString(String url) {
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpUriRequest request = new HttpGet(url);

            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception localException) {
        }
        return null;
    }

    public static void test(Context context) {
        RequestQueue mQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest("http://www.baidu.com",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(stringRequest);
    }
}
