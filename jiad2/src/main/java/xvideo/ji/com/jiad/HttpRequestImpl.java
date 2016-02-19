package xvideo.ji.com.jiad;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;

import java.io.IOException;

/**
 * Created by Domon on 16-2-19.
 */
public class HttpRequestImpl implements HttpRequestInterface, ResponseHandler<HttpResponseInterface> {
    protected HttpUriRequest httpUriRequest;//获取request的url地址
    private AbstractHttpClient abstractHttpClient;//client 对象

    public HttpRequestImpl(AbstractHttpClient abstractHttpClient) {
        this.abstractHttpClient = abstractHttpClient;
    }

    @Override
    public HttpUriRequest getHttpRequest() {
        return httpUriRequest;
    }

    public String getRequestURL() {
        return httpUriRequest.getURI().toString();
    }

    public HttpResponseInterface request() throws Exception {
        return abstractHttpClient.execute(httpUriRequest, this);//传入ResponseHandler对象
    }

    public HttpResponseInterface handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        HttpResponseInterface httpResponseInterface = new HttpResponseImpl(response);

        return httpResponseInterface;
    }
}
