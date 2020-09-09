package com.howie.crm.workbench.service.impl;

import com.howie.crm.settings.dao.UserDao;
import com.howie.crm.settings.domain.User;
import com.howie.crm.utils.SqlSessionUtil;
import com.howie.crm.vo.PaginationVo;
import com.howie.crm.workbench.dao.ActivityDao;
import com.howie.crm.workbench.dao.ActivityRemarkDao;
import com.howie.crm.workbench.domain.Activity;
import com.howie.crm.workbench.domain.ActivityRemark;
import com.howie.crm.workbench.service.ActivityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class); // 引dao层
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public List<Activity> getActivityListByNameAndNotClueId(Map<String, Object> map) {
        List<Activity> activityList = activityDao.getActivityListByNameAndNotClueId(map);
        return activityList;
    }

    @Override
    public boolean save(Activity activity) {
        boolean flag = true;
        int count = activityDao.save(activity);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVo<Activity> pageList(Map<String, Object> map) {
        // 1.取得dataList
        List<Activity> dataList = activityDao.getActivityByCondition(map);
        // 2.取得total
        Integer total = activityDao.getTotalByCondition(map);
        // 3.将dataList 和 total封装到vo中
        PaginationVo<Activity> vo = new PaginationVo<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        // 4.将vo返回
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        // 查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);
        // 删除备注，返回受到影响的条数(实际删除的数量)
        int count2 =activityRemarkDao.deleteByAids(ids);
        if(count1 != count2){
            flag = false;
        }
        // 删除市场活动
        int count3 = activityDao.delete(ids);
        if(ids.length != count3)
            flag = false;
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        Map<String,Object> map = new HashMap<String,Object>();
        // 1.获取用户列表
        List<User> userList = userDao.getUserList();
        map.put("userList",userList);
        // 2.根据id获取市场活动信息
        Activity activity = activityDao.getById(id);
        map.put("activity",activity);
        return map;
    }

    @Override
    public boolean update(Activity activity) {
        boolean flag = true;
        int count = activityDao.update(activity);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        return activityDao.detail(id);
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String id) {
        List<ActivityRemark> remarkList = activityRemarkDao.getRemarkListByAid(id);
        return remarkList;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;
        int count = activityRemarkDao.deleteRemark(id);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(activityRemark);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(activityRemark);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByName(String aname) {
        return activityDao.getActivityListByName(aname);
    }
}
