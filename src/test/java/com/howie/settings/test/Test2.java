package com.howie.settings.test;

import com.howie.crm.settings.domain.DicValue;
import com.howie.crm.settings.domain.User;
import com.howie.crm.settings.service.DicService;
import com.howie.crm.settings.service.UserService;
import com.howie.crm.settings.service.impl.DicServiceImpl;
import com.howie.crm.settings.service.impl.UserServiceImpl;
import com.howie.crm.utils.ServiceFactory;
import com.howie.crm.utils.SqlSessionUtil;
import com.howie.crm.workbench.dao.ActivityDao;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Test2 {
    public static void main(String[] args) {
        /*ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
        System.out.println(activityDao);*/

        /*UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        for(User user:userList){
            System.out.println(user);
        }*/

        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue>> map = dicService.getAll();
        Set<String> keys = map.keySet();
        for(String s:keys){
            System.out.println(s);
        }
    }
}
