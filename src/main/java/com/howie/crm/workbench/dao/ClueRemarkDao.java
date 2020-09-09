package com.howie.crm.workbench.dao;

import com.howie.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {
    List<ClueRemark> getListByClueId(String clueId);
    int delete(String id);
}
