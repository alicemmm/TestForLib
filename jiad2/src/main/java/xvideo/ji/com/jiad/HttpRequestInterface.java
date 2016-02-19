package xvideo.ji.com.jiad;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Created by Domon on 16-2-19.
 */

public interface HttpRequestInterface {

    //获得HttpRequest
    public HttpUriRequest getHttpRequest();

    //获得Http请求Url地址
    public String getRequestURL();

    //请求服务器：然会一个Response对象
    public HttpResponseInterface request() throws Exception;
}
