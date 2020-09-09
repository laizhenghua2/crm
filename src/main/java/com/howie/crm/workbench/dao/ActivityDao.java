package com.howie.crm.workbench.dao;

import com.howie.crm.workbench.domain.Activity;
import java.util.List;
import java.util.Map;

public interface ActivityDao {
    int save(Activity activity);
    Integer getTotalByCondition(Map<String,Object> map);
    List<Activity> getActivityByCondition(Map<String,Object> map);
    // int deleteByAids(String[] ids);
    int delete(String[] ids);
    Activity getById(String id);
    Activity detail(String id);
    int update(Activity activity);

    List<Activity> getActivityListByNameAndNotClueId(Map<String,Object> map);
    List<Activity> getActivityListByName(String aname);
}
