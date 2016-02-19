package xvideo.ji.com.jiad;

/**
 * Created by Domon on 16-2-19.
 */
public class JiConnectionException extends Exception{
    public JiConnectionException(String message)
    {
        super(message);
    }

    public JiConnectionException(int message)
    {
        super(String.valueOf(message));
    }

    public JiConnectionException(Exception e)
    {
        super(e);
    }

    public JiConnectionException(String message, Exception e)
    {
        super(message, e);
    }

    public JiConnectionException(int message, Throwable e)
    {
        super(String.valueOf(message), e);
    }
}
