package xvideo.ji.com.jiad;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

/**
 * Created by Domon on 16-2-19.
 */
public class BaseHttpClient {
    private AbstractHttpClient httpClient;
    public static final int DEFAULT_RETIES_COUNT = 5;

    protected int retriesCount = DEFAULT_RETIES_COUNT;

    //最大连接数
    public final static int MAX_TOTAL_CONNECTIONS = 100;

    //最大等待时间
    public final static int WAIT_TIMEOUT = 30000;

    public final static int MAX_ROUTE_CONNECTION = 100;

    public final static int CONNECT_TIMEOUT = 10000;

    public final static int READ_TIMEOUT = 10000;


    public BaseHttpClient() {
        initHttpClient();
    }

    private void initHttpClient() {
        HttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setMaxTotalConnections(httpParams, MAX_TOTAL_CONNECTIONS);

        ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);

        ConnPerRouteBean connPerRouteBean = new ConnPerRouteBean(MAX_ROUTE_CONNECTION);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRouteBean);

        HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        httpClient = new DefaultHttpClient(clientConnectionManager, httpParams);

        httpClient.setHttpRequestRetryHandler(new BaseHttpRequestRetryHandler(retriesCount));
    }

    private List<BasicNameValuePair> parseParams(HashMap<String, Object> params) {
        if (params == null || params.size() == 0) {
            return null;
        }

        List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>(params.size());

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue() + ""));
        }
        return pairList;
    }

    /**
     * 向服务器端请求：当请求只有url 没有参数的时候
     */
    public String post(String url) throws Exception {
        return post(url, null); //调用有参数的时候执行的post并将参数设置为null
    }

    /**
     * post请求之后返回T类型的结果
     */
    public <T> T post(String url, HashMap<String, Object> params, Class<T> clz) throws Exception {
        String json = post(url, params);
        return JSONUtil.fromJson(json, clz); //转化为具体的类型返回
    }

    /**
     * 当请求有参数的时候，其他函数间接调用该方法
     */
    public String post(String url, HashMap<String, Object> params) throws Exception {

        //将传入的参数转化为参数实体：将params转化为enrity的对象：表单entity
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parseParams(params));

        return request(url, entity).getResponseStreamAsString();

    }

    /**
     * 将post执行的结果直接返回
     */
    public Result postAsResult(String url, HashMap<String, Object> params) throws Exception {
        return post(url, params, Result.class);
    }

    public HttpResponseInterface request(String url, HttpEntity entity) throws Exception {

        HttpRequestImpl httpRequestImpl = new ExecuteHttpPost(httpClient, url, entity);

        return httpRequestImpl.request();
    }

}
