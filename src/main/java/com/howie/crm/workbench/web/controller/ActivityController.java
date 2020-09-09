package com.howie.crm.workbench.web.controller;

import com.howie.crm.settings.dao.UserDao;
import com.howie.crm.settings.domain.User;
import com.howie.crm.settings.service.UserService;
import com.howie.crm.settings.service.impl.UserServiceImpl;
import com.howie.crm.utils.*;
import com.howie.crm.vo.PaginationVo;
import com.howie.crm.workbench.domain.Activity;
import com.howie.crm.workbench.domain.ActivityRemark;
import com.howie.crm.workbench.service.ActivityService;
import com.howie.crm.workbench.service.impl.ActivityServiceImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("市场活动控制器");
        String path = request.getServletPath(); // 获取请求路径
        if("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        } else if("/workbench/activity/save.do".equals(path)){
            save(request,response);
        } else if("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        } else if("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        } else if("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        } else if("/workbench/activity/update.do".equals(path)){
            update(request,response);
        } else if("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        } else if("/workbench/activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(request,response);
        } else if("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        } else if("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        } else if("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }
    }

    public void updateRemark(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行更新市场活动备注信息操作");
        // 获取更新信息
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");

        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setId(id);
        activityRemark.setNoteContent(noteContent);
        activityRemark.setEditTime(editTime);
        activityRemark.setEditBy(editBy);
        activityRemark.setEditFlag(editFlag);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.updateRemark(activityRemark);
        Map<String,Object> map = new HashMap<>();
        if(flag){
            map.put("remark",activityRemark);
        }
        map.put("success",flag);
        PrintJson.printJsonObj(response,map);
    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行通过市场活动id，获取市场活动备注信息列表");
        String activityId = request.getParameter("activityId");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<ActivityRemark> activityRemarkList = activityService.getRemarkListByAid(activityId);
        PrintJson.printJsonObj(response,activityRemarkList);
    }

    public void getUserList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("获取用户信息列表");
        /*
        此时应该使用userService，不应该使用activityService，应为获取用户列表没有涉及到市场活动相关的业务。
         */
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl()); // 获取接口UserService的接口实现类
        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);
    }

    public void save(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行市场活动的添加操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost"); // 成本
        String description = request.getParameter("description"); // 描述
        // 创建时间，应该为当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        // 创建人：当前登录用户
        String createBy = ((User) request.getSession().getAttribute("user")).getName();

        // 获取代理类(传明星得到经济人)
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        // 创建市场对象
        Activity activity = new Activity();
        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name); // 市场名称
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);

        boolean flag = activityService.save(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    public void pageList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行市场活动信息列表的操作(结合条件查询+分页查询)");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageSizeStr = request.getParameter("pageSize");
        String pageNoStr = request.getParameter("pageNo");

        // 每页展现的记录数
        int pageSize = Integer.valueOf(pageSizeStr);

        int pageNo = Integer.valueOf(pageNoStr);
        // 计算略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        /*
        市场活动信息列表 + 查询的总条数
        业务层拿到了以上两项信息之后，以vo方式返回
        vo
        PaginationVo<T>
        private Integer total;
        private List<T> dataList;

        PaginationVo<Activity> vo = new PaginationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        将来分页查询，每个模块都有，所以我们选择使用一个通用的vo，操作起来比较方便
         */

        PaginationVo<Activity> vo = activityService.pageList(map);
        PrintJson.printJsonObj(response,vo);
    }

    public void delete(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行市场活动删除操作");
        String[] ids = request.getParameterValues("id");

        /*for(String s:ids){
            System.out.println(s);
        }*/
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.delete(ids);
        PrintJson.printJsonFlag(response,flag);
    }

    public void getUserListAndActivity(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行修改市场活动的操作(查询用户信息列表和根据市场活动id查询市场活动)");
        // 1.获取修改市场活动的id
        String id = request.getParameter("id");
        // System.out.println(id);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        /*
        业务层：需要获取用户列表和市场活动对象
        总结：
            controller调用service的方法，返回值应该是什么？前端需要什么数据，就要从service取得并按情况(vo/map)打包
            前端需要的数据，管业务层去要
         */
        Map<String,Object> map = activityService.getUserListAndActivity(id);
        PrintJson.printJsonObj(response,map);
    }

    public void update(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行市场活动更新操作");
        // 获取市场活动更新数据
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost"); // 成本
        String description = request.getParameter("description"); // 描述
        // 修改时间，应该为当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        // 修改人：当前登录用户
        String editBy = ((User) request.getSession().getAttribute("user")).getName();

        // 获取代理类(传明星得到经济人)
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        // 创建市场对象
        Activity activity = new Activity();
        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name); // 市场名称
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setEditTime(editTime);
        activity.setEditBy(editBy);

        boolean flag = activityService.update(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    public void detail(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        System.out.println("执行跳转到详细信息页的操作");
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity activity = activityService.detail(id);
        // System.out.println(activity);

        // 将activity对象保存到session域中
        request.setAttribute("activity",activity);

        // 通过请求转发的方式响应给浏览器
        request.getRequestDispatcher("detail.jsp").forward(request,response);
    }

    public void deleteRemark(HttpServletRequest request,HttpServletResponse response){
        System.out.println("执行删除市场备注操作");
        String id = request.getParameter("id");

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);
    }

    public void saveRemark(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("执行添加备注信息的操作");
        // 获取添加信息和市场活动id
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");

        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setId(id);
        activityRemark.setNoteContent(noteContent);
        activityRemark.setCreateTime(createTime);
        activityRemark.setCreateBy(createBy);
        activityRemark.setEditFlag("0");
        activityRemark.setActivityId(activityId);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.saveRemark(activityRemark);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        if(flag){
            map.put("remark",activityRemark);
        } else{
            map.put("remark",null);
        }
        PrintJson.printJsonObj(response,map);
    }
}














