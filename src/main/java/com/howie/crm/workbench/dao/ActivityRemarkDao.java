package com.howie.crm.workbench.dao;

import com.howie.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int getCountByAids(String[] ids);
    int deleteByAids(String[] ids);
    List<ActivityRemark> getRemarkListByAid(String aid);
    int deleteRemark(String id);
    int saveRemark(ActivityRemark activityRemark);
    int updateRemark(ActivityRemark activityRemark);
}
