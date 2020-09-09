package com.howie.crm.workbench.web.controller;


import com.howie.crm.settings.domain.User;
import com.howie.crm.settings.service.UserService;
import com.howie.crm.settings.service.impl.UserServiceImpl;
import com.howie.crm.utils.DateTimeUtil;
import com.howie.crm.utils.PrintJson;
import com.howie.crm.utils.ServiceFactory;
import com.howie.crm.utils.UUIDUtil;
import com.howie.crm.workbench.domain.Tran;
import com.howie.crm.workbench.service.CustomerService;
import com.howie.crm.workbench.service.TranService;
import com.howie.crm.workbench.service.impl.CustomerServiceImpl;
import com.howie.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 交易控制器
        // 获取请求路径
        String path = request.getServletPath();
        if("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        } else if("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        } else if("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 执行市场交易保存操作
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String customerName = request.getParameter("customerName"); // 此处暂时只有客户的名称，没有客户的id
        String expectedDate = request.getParameter("expectedDate");
        // String customerId = request.getParameter("customerId");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User)request.getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran tran = new Tran();
        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.save(tran,customerName);
        if(flag){
            // 如果添加交易成功，跳转到列表页
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }
    }

    public void add(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
        // 执行跳转到交易添加页的操作
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        request.setAttribute("userList",userList);

        // 请求转发
        // 通过当前请求对象生成资源文件申请报告对象,并将报告对象发送给Tomcat
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }

    public void getCustomerName(HttpServletRequest request,HttpServletResponse response){
        // 执行查询客户姓名操作(取得客户名称列表，按照客户的名称进行模糊查询)
        String name = request.getParameter("name");

        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> nameList = customerService.getCustomerName(name);
        PrintJson.printJsonObj(response,nameList);
    }
}
