package com.junjun.lei.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * @author junjun.lei
 * @create 2020-09-22 10:15
 */
public class MyHttpRequestListener implements ServletRequestListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpRequestListener.class);

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
        String requestURI = req.getRequestURI();
        LOGGER.info("{}--被调用",requestURI);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        LOGGER.info("request 监听器被销毁");
    }
}
