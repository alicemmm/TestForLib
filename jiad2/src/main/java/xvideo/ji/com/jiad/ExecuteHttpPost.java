package xvideo.ji.com.jiad;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.impl.client.AbstractHttpClient;

/**
 * Created by Domon on 16-2-19.
 */
public class ExecuteHttpPost extends HttpRequestImpl {

    public ExecuteHttpPost(AbstractHttpClient abstractHttpClient, String url) {
        this(abstractHttpClient, url, null);
    }

    public ExecuteHttpPost(AbstractHttpClient abstractHttpClient, String url, HttpEntity entity) {
        super(abstractHttpClient);

        this.httpUriRequest = new org.apache.http.client.methods.HttpPost(url);

        if (entity != null) {
            ((HttpEntityEnclosingRequestBase) httpUriRequest).setEntity(entity);
        }
    }
}
