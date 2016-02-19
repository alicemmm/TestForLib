package xvideo.ji.com.jiad;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

/**
 * Created by Domon on 16-2-19.
 */
public interface HttpResponseInterface {

    //返回状态码
    public int statusCode();

    //向客户端返回流
    public InputStream getResponseStream() throws IllegalStateException, IOException;

    //向客户端返回字节
    public byte[] getResponseStreamAsByte() throws IOException;

    //向客户端返回Json
    public String getResponseStreamAsString() throws ParseException, IOException;
}
