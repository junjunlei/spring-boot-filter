package com.junjun.lei.filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器
 * @author junjun.lei
 * @create 2020-09-21 16:39
 */
public class MyFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("初始化过滤器");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI();
        if (requestUri.contains("/addSession")
                || requestUri.contains("/removeSession")
                || requestUri.contains("/online")
                || requestUri.contains("/favicon.ico")) {
            //放行
            filterChain.doFilter(servletRequest, response);
        } else {
            response.sendRedirect("/online");
        }
    }

    @Override
    public void destroy() {
        LOGGER.info("销毁过滤器");
    }
}
