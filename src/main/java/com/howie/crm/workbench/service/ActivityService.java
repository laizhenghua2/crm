package com.howie.crm.workbench.service;

import com.howie.crm.vo.PaginationVo;
import com.howie.crm.workbench.domain.Activity;
import com.howie.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    boolean save(Activity activity);
    PaginationVo<Activity> pageList(Map<String,Object> map);
    boolean delete(String[] ids);
    Map<String,Object> getUserListAndActivity(String id);
    boolean update(Activity activity);
    Activity detail(String id);
    List<ActivityRemark> getRemarkListByAid(String id);
    boolean deleteRemark(String id);
    boolean saveRemark(ActivityRemark activityRemark);
    boolean updateRemark(ActivityRemark activityRemark);

    List<Activity> getActivityListByNameAndNotClueId(Map<String,Object> map);
    List<Activity> getActivityListByName(String aname);
}
