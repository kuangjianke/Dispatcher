package com.renren.test.basic;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.test.exception.ConfigException;

public class TestUtil {

    private static Logger logger = LoggerFactory.getLogger("testutil");

    public static String formatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 将信息打印到终端，此方法在高并发的情况下调用很危险！
     * @param msg
     */
    public static void printToTermin(String msg) {
        System.out.println(msg);
    }

    /**
     * 处理终端异常
     * @param e
     */
    public static void handleInterrupt(InterruptedException e) {
        Thread.interrupted();//此处可以不理，中断一般不会发生
        logError("an interruption occured", e);
    }

    public static void logError(String error, Throwable e) {
        logger.error("an interruption occured", e);
    }

    /**
     * 从固定位置(必须在classpath跟目录下)去获取配置参数.
     * @return
     * @throws ConfigException
     */
    public static Properties getTestProperties() throws ConfigException {
        Properties p = new Properties();
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(
            "concurrenttest.properties");
        try {
            p.load(in);
        } catch (IOException e) {
            throw new ConfigException("配置文件异常", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {}
        }
        return p;
    }

    /**
     * 动态创建一个对象.
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadInstance(String clazz) {
        try {
            return (T) Class.forName(clazz).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
