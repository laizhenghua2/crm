package com.howie.crm.settings.service;
import com.howie.crm.exception.LoginException;
import com.howie.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct,String loginPwd,String ip) throws LoginException;
    List<User> getUserList();
}

