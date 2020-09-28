package com.junjun.lei.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author junjun.lei
 * @create 2020-09-22 9:57
 */
public class MyHttpSessionListener implements HttpSessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpSessionListener.class);

    public static AtomicInteger userCount = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        userCount.getAndIncrement();
        ServletContext servletContext = se.getSession().getServletContext();
        servletContext.setAttribute("sessionCount", userCount.get());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        userCount.getAndDecrement();
        ServletContext servletContext = se.getSession().getServletContext();
        servletContext.setAttribute("sessionCount", userCount.get());
        LOGGER.info("在线人数减少为：{}", userCount.get());
    }
}
