package com.howie.crm.settings.web.controller;
import com.howie.crm.exception.LoginException;
import com.howie.crm.settings.domain.User;
import com.howie.crm.settings.service.UserService;
import com.howie.crm.settings.service.impl.UserServiceImpl;
import com.howie.crm.utils.MD5Util;
import com.howie.crm.utils.PrintJson;
import com.howie.crm.utils.ServiceFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到用户控制器");
        // 模板模式的应用
        String path = request.getServletPath(); // 获取请求路径
        if ("/settings/user/login.do".equals(path)) {
            login(request,response);
        } else if("/settings/user/xxx.do".equals(path)){
            System.out.println(456);
        }
    }
    public void login(HttpServletRequest request,HttpServletResponse response){
        // 获取验证参数
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        // 将密码铭文形式转换为 MD5 密文形式
        String newLoginPwd = MD5Util.getMD5(loginPwd);
        // 接收浏览器端ip地址
        String ip = request.getRemoteAddr();
        // System.out.println("ip地址：" + ip);

        // 未来业务层开发，统一使用代理类形态的接口对象
        // 获取代理类[传入被代理类(明星)获取代理类(经济人)]
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl()); // 动态生成代理类
        try{
            User user = userService.login(loginAct,newLoginPwd,ip); // 返回User对象
            // 将User对象保存到session域中
            request.getSession().setAttribute("user",user);
            // 如果程序执行到此处，说明业务层没有为Controller抛出任何异常，表示登录成功
            // 为前端返回{"success":true}
            PrintJson.printJsonFlag(response,true);
        } catch (Exception e){
            e.printStackTrace();
            String message = e.getMessage();
            System.out.println(message);
            /*
            如果程序执行到此处，说明业务层为我们验证登录失败，为controller抛出异常。表示登录失败
            此时，我们现在作为controller，需要为ajax请求提供多项信息
            可以有两种手段来处理：
                （1）将多项信息打包成为map，将map解析为json串
                （2）创建一个vo：
                    private boolean success;
                    private String message;
                如果对于展现的信息将来还会大量的使用，我们创建一个vo类，使用方便
                如果对于展现的信息只有在这个需求中能够使用，我们使用map就可以了
             */
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("message",message);
            PrintJson.printJsonObj(response,map);
        }
    }
}
