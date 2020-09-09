package com.howie.crm.web.filter;

import com.howie.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("进入到验证有没有登录过的过滤器");
        HttpServletRequest request = (HttpServletRequest) servletRequest; // 向下转型
        HttpServletResponse response = (HttpServletResponse) servletResponse; // 向下转型

        String path = request.getServletPath();
        // 不应该拦截的资源自动放行
        if("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){
            filterChain.doFilter(servletRequest, servletResponse);
            // return ;
        } else{
            // 获取session域对象
            HttpSession session = request.getSession();
            // System.out.println(session);
            User user = (User) session.getAttribute("user");

            // 如果user不等于null，说明登录过。否则没有登录过(重定向到登录页面)
            if(user != null){
                filterChain.doFilter(servletRequest,servletResponse);
            } else{
            /*
            重定向复习：
                1.重定向的路径？
                    在实际项目开发中，对于路径的使用，无论操作的是前端还是后端，应该一律使用绝对路径
                    关于转发和重定向的路径写法如下：
                        转发：
                            使用的是一种特殊的绝对路径的使用方式，这种绝对路径前面不加"/项目名"，这种路径也称之为内部路径
                            login.jsp
                        重定向：
                            使用的是传统绝对路径的写法，前面必须以"/项目名"开头，后面跟具体的资源路径
                            /crm/login.jsp
                2.为什么使用重定向？不使用请求转发的原因
                    转发之后，路径会停留在老路径上，而不是跳转之后最新的资源的路径
                    我们应该在为用户跳转到登录页的同时，将浏览器的地址栏应该自动设置为当前的登录页的路径
             */
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                // ${pageContext.request.contextPath} /项目名
            }
        }
    }
}













