package configuration.helpers;

import okhttp3.OkHttpClient;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by kgr on 11/16/2016.
 */
public class HtppClient {
    protected final Logger log = Logger.getLogger(this.getClass());
    private static HtppClient htppClient;
    private static OkHttpClient client;

    private HtppClient() {
        this(60, 60, 60);
    }

    private HtppClient(long readTimeout, long writeTimeout, long connectTimeout) {
        log.info("Initialize http client");
        client = new OkHttpClient.Builder()
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpClient getHtppClient() {
        if (htppClient == null)
            htppClient = new HtppClient();
        return client;
    }

    public static OkHttpClient getHtppClient(long readTimeout, long writeTimeout, long connectTimeout) {
        if (htppClient == null)
            htppClient = new HtppClient(readTimeout, writeTimeout, connectTimeout);
        return client;
    }
}