package com.howie.crm.workbench.web.controller;

import com.howie.crm.settings.domain.User;
import com.howie.crm.settings.service.UserService;
import com.howie.crm.settings.service.impl.UserServiceImpl;
import com.howie.crm.utils.DateTimeUtil;
import com.howie.crm.utils.PrintJson;
import com.howie.crm.utils.ServiceFactory;
import com.howie.crm.utils.UUIDUtil;
import com.howie.crm.workbench.domain.Activity;
import com.howie.crm.workbench.domain.Clue;
import com.howie.crm.workbench.domain.Tran;
import com.howie.crm.workbench.service.ActivityService;
import com.howie.crm.workbench.service.ClueService;
import com.howie.crm.workbench.service.impl.ActivityServiceImpl;
import com.howie.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到线索(潜在客户)控制器...");
        // 获取请求路径
        String path = request.getServletPath();
        if("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);
        } else if("/workbench/clue/saveClue.do".equals(path)){
            saveClue(request,response);
        } else if("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        } else if("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);
        } else if("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);
        } else if("/workbench/clue/getActivityListByNameAndNotClueId.do".equals(path)){
            getActivityListByNameAndNotClueId(request,response);
        } else if("/workbench/clue/bund.do".equals(path)){
            bund(request,response);
        } else if("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);
        } else if("/workbench/clue/convert.do".equals(path)){
            convert(request,response);
        }
    }

    public void getUserList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行获取用户列表操作");
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();

        PrintJson.printJsonObj(response,userList);
    }

    public void saveClue(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行线索的添加操作");

        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue clue = new Clue();
        clue.setId(id);
        clue.setFullname(fullname);
        clue.setAppellation(appellation);
        clue.setOwner(owner);
        clue.setCompany(company);
        clue.setJob(job);
        clue.setEmail(email);
        clue.setPhone(phone);
        clue.setWebsite(website);
        clue.setMphone(mphone);
        clue.setState(state);
        clue.setSource(source);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setContactSummary(contactSummary);
        clue.setNextContactTime(nextContactTime);
        clue.setAddress(address);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.saveClue(clue);
        PrintJson.printJsonFlag(response,flag);
    }

    public void detail(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到线索详细信息页");
        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = clueService.detail(id);

        request.setAttribute("clue",clue);
        // 请求转发
        request.getRequestDispatcher("detail.jsp").forward(request,response);
    }

    public void getActivityListByClueId(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行根据线索id，获取关联的市场活动信息列表");

        String clueId = request.getParameter("clueId");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        List<Activity> activityList = clueService.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response,activityList);
    }

    public void unbund(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行解除市场活动关联的操作");

        String id = request.getParameter("id");

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.unbund(id);
        PrintJson.printJsonFlag(response,flag);
    }

    public void getActivityListByNameAndNotClueId(HttpServletRequest request,HttpServletResponse response){
        System.out.println("查询市场活动列表(根据名称模糊查询+排除掉已经关联指定线索的的列表)");
        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");

        Map<String,Object> map = new HashMap<>();
        map.put("aname",aname);
        map.put("clueId",clueId);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByNameAndNotClueId(map);
        PrintJson.printJsonObj(response,activityList);
    }

    public void bund(HttpServletRequest request,HttpServletResponse response){
        // 执行关联市场活动操作
        String clueId = request.getParameter("clueId");
        String[] aids = request.getParameterValues("aid");

        Map<String,Object> map = new HashMap<>();
        map.put("clueId",clueId);
        map.put("aids",aids);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.bund(map);
        PrintJson.printJsonFlag(response,flag);
    }

    public void getActivityListByName(HttpServletRequest request,HttpServletResponse response){
        // 根据市场活动名称做模糊查询，得到市场列表
        String aname = request.getParameter("aname");

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByName(aname);

        PrintJson.printJsonObj(response,activityList);
    }

    public void convert(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
        // 执行线索转客户的操作

        // 接收是否需要创建交易的标记
        String flag = request.getParameter("flag");
        String clueId = request.getParameter("clueId");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        // System.out.println(flag);
        Tran t = null;
        if("true".equals(flag)){
            t = new Tran();
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");

            String id = UUIDUtil.getUUID();
            String createDate = DateTimeUtil.getSysTime();

            t.setId(id);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setCreateBy(createBy);
            t.setCreateTime(createDate);
        }

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        /*
        为业务层传递的参数
        1.必须传递的参数是clueId，有了这个clueId之后我们才知道要转换那条记录
        2.必须传递的参数t，因为在线转换的过程中，有可能会临时创建一笔交易(业务层接收的t也有可能是一个null)
         */
        boolean newFlag = clueService.convert(clueId,createBy,t);
        if(newFlag){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }
    }
}













