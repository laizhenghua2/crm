package com.howie.crm.workbench.dao;

import com.howie.crm.workbench.domain.Activity;
import com.howie.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {

	List<Activity> getActivityListByClueId(String clueId);

	int unbund(String id);
	int bund(ClueActivityRelation clueActivityRelation);
	List<ClueActivityRelation> getListByClueId(String clueId);

	int delete(String id);
}
