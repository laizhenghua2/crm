package com.howie.crm.settings.service.impl;

import com.howie.crm.exception.LoginException;
import com.howie.crm.settings.dao.UserDao;
import com.howie.crm.settings.domain.User;
import com.howie.crm.settings.service.UserService;
import com.howie.crm.utils.DateTimeUtil;
import com.howie.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class); // 帮我们创建实现类
    // 1.登录验证
    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        // 将参数封装为map
        Map<String,String> map = new HashMap<String,String>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userDao.login(map);

        if(user == null){
            throw new LoginException("账号密码错误");
        }
        // 如果程序能够成功的执行到该行，说明账号密码正确。需要继续验证其他3项信息
        // 1.验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if(expireTime.compareTo(currentTime) < 0){
            throw new LoginException("账号已失效");
        }
        // 2.验证锁定状态
        String lockState = user.getLockState();
        if ("0".equals(lockState)) {
            throw new LoginException("账号已锁定");
        }

        // 3.验证ip地址
        String allowIps = user.getAllowIps();
        // System.out.println(ip);
        if(allowIps.contains(ip)){
            throw new LoginException("ip地址受限");
        }
        return user;
    }

    // 获取用户列表
    @Override
    public List<User> getUserList() {
        List<User> userList = userDao.getUserList();
        return userList;
    }
}

















