###过滤器

过滤器的英文名称为Filter,是Servlet技术中最实用的技术

过滤器是处于客户端和服务器资源文件之间的一道过滤网，帮助
我们掉一些不符合要求的请求，通常作用Session校验，判断用户权限
，如果不符合设定条件，则会被拦截到特殊的地址或者基于特殊的响应

###过滤器的使用

首先需要实现Filter接口然后重写他的三个方法

*  init方法：在容器中创建当前过滤器的时候自动调用

*  doFilter方法： 过滤的具体操作

*  destroy 方法：在过滤器中销毁当前过滤器的时候自动调用


```
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

```


###拦截器

Java中的拦截器是动态拦截action调用的对象，然后提供了可以在action执行
前后增加一些操作，也可以在action执行前后停止操作，功能与过滤器类似，但是
标准与实现方式不同

* 登录认证：在一些应用中，可能会通过拦截器来验证用户的登录状态，如果没有登录或者登录失败
就会给用户一个友好的提示或者返回登录界面，当然在大型项目中都不采用这种方式，都是调用单点登录
系统接口来验证用户。

* 记录系统日志：我们在常见的应用中，通常都要记录用户请求信息，比如请求ip,方法执行时间等,通过这些记录可以
记录可以监控系统状况，以便于对系统进行信息监控、信息统计、计算pv、性能调优等

* 通用处理：在应用程序中可能存在所有方法都要有返回的信息，这时可以利用拦截器来实现，省去每个方法
冗余重代码


###使用拦截器

实现HandlerInterceptor类，重写三个方法

* preHandler :在Controller处理请求之前被调用，在返回值是boolean类型,如果是true就进行下一步操作；
若返回false,则证明不符合拦截条件，在失败的时候不会包含任何响应，此时需要调用对应的response返回对应响应

* postHandler:在Controller处理请求执行后，返回之前执行，可以通过ModelAndView进行视图处理

* afterCompletion:在DispatcherServlet完全处理请求后被调用，通常用于记录消耗时间，也可以对一些
资源进行处理


```
@Component
public class MyInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOGGER.info("MyInterceptor调用了：{}", request.getRequestURI());
        request.setAttribute("requestTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!request.getRequestURI().contains("/online")) {
            HttpSession session = request.getSession();
            String sessionName = (String) session.getAttribute("name");
            if ("test".equals(sessionName)) {
                LOGGER.info("MyInterceptor当前浏览器存在session{}", sessionName);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long time = (Long) request.getAttribute("requestTime");
        LOGGER.info("MyInterceptor[{}}调用耗时：{} ms", request.getRequestURI(), System.currentTimeMillis() - time);
    }
}

```


###监听器

监听器通常用于监听 Web 应用程序中对象的创建、销毁等动作的发送，同时对监听的情况作出相应的处理，
最常用于统计网站的在线人数、访问量等。

###监听器大概分为以下几种：

* ServletContextListener：用来监听 ServletContext 属性的操作，比如新增、修改、删除。
* HttpSessionListener：用来监听 Web 应用种的 Session 对象，通常用于统计在线情况。
* ServletRequestListener：用来监听 Request 对象的属性操作。

###监听器的使用

我们通过 HttpSessionListener来统计当前在线人数、ip等信息，为了避免并发问题我们使用原子int来计数。

ServletContext,是一个全局的储存信息的空间，它的生命周期与Servlet容器也就是服务器保持一致，服务器关闭才销毁。

request，一个用户可有多个；

session，一个用户一个；而servletContext，所有用户共用一个。所以，为了节省空间，提高效率，ServletContext中，要放必须的、重要的、所有用户需要共享的线程又是安全的一些信息。

因此我们这里用ServletContext来存储在线人数sessionCount最为合适。

```
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
```