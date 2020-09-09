package com.howie.settings.test;

import com.howie.crm.settings.dao.UserDao;
import com.howie.crm.settings.domain.User;
import com.howie.crm.utils.ServiceFactory;
import com.howie.crm.utils.SqlSessionUtil;
import com.howie.crm.vo.PaginationVo;
import com.howie.crm.workbench.dao.ActivityDao;
import com.howie.crm.workbench.dao.ActivityRemarkDao;
import com.howie.crm.workbench.domain.Activity;
import com.howie.crm.workbench.service.ActivityService;
import com.howie.crm.workbench.service.impl.ActivityServiceImpl;

import java.util.*;

public class Test3 {
    public static void main(String[] args) {
        /*ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
        String[] ids = new String[]{"23c41382b8fe40ec83a081e2878a5bee"};
        // int count1 = activityRemarkDao.deleteByAids(ids);
        int count2 = activityRemarkDao.getCountByAids(ids);
        // System.out.println(count1);
        System.out.println(count2);*/

        /*ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
        Activity activity = activityDao.detail("07606b5bdd424a0d90082aff4e67d73e");
        System.out.println(activity);*/

        /*UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
        List<User> userList = userDao.getUserList();
        for(User u:userList){
            System.out.println(u);
        }*/

        Map<String,String> pMap = new HashMap<>();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> keyEnums = resourceBundle.getKeys();
        System.out.println(keyEnums);
    }
}
