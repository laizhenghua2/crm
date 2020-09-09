package com.howie.crm.workbench.dao;

import com.howie.crm.workbench.domain.Clue;

public interface ClueDao {

	int saveClue(Clue clue);
	Clue detail(String id);
	Clue getById(String id);
	int delete(String id);
}
