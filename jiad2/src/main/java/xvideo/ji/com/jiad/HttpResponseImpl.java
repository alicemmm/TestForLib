package xvideo.ji.com.jiad;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

/**
 * Created by Domon on 16-2-19.
 */
public class HttpResponseImpl implements HttpResponseInterface {
    private HttpResponse response;
    private HttpEntity entity;

    public HttpResponseImpl(HttpResponse response) throws IOException {
        this.response = response;

        HttpEntity tempEntity = response.getEntity();
        if (tempEntity != null) {
            entity = new BufferedHttpEntity(tempEntity);
        }
    }

    public int statusCode() {
        return response.getStatusLine().getStatusCode();
    }

    public InputStream getResponseStream() throws IllegalStateException, IOException {
        InputStream inputStream = entity.getContent();
        return inputStream;
    }

    public String getResponseStreamAsString() throws ParseException,IOException{
        return EntityUtils.toString(entity);
    }

    public byte[] getResponseStreamAsByte() throws IOException{
        return EntityUtils.toByteArray(entity);
    }
}
