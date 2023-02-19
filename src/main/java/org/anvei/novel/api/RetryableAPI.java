package org.anvei.novel.api;

import org.anvei.novel.api.exceptions.ConnectionException;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * 在API接口基础上添加了HTTP尝试重连功能，默认尝试重连次数为3次
 */
public abstract class RetryableAPI implements CrawlerAPI {

    private int retry = 3;          // 尝试重连次数

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    protected Document connect(Connection connection, Connection.Method method) throws ConnectionException {
        int counter = 0;            // 表示当前是第几次尝试重新连接
        while (counter <= getRetry()) {
            if (counter > 0) {
                System.out.println("[INFO] retry: " + counter);
            }
            try {
                if (method == Connection.Method.GET) {
                    return  connection.get();
                } else if (method == Connection.Method.POST) {
                    return connection.post();
                }
            } catch (IOException e) {
                counter++;
            }
        }
        throw new ConnectionException();
    }

}
