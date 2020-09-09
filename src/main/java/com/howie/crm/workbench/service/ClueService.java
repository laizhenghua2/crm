package com.howie.crm.workbench.service;

import com.howie.crm.workbench.domain.Activity;
import com.howie.crm.workbench.domain.Clue;
import com.howie.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    public abstract boolean saveClue(Clue clue);
    Clue detail(String id);
    List<Activity> getActivityListByClueId(String clueId);
    boolean unbund(String id);
    boolean bund(Map<String,Object> map);

    boolean convert(String clueId,String createBy,Tran tran);
}
