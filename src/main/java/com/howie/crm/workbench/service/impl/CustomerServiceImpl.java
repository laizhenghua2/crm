package com.howie.crm.workbench.service.impl;

import com.howie.crm.utils.SqlSessionUtil;
import com.howie.crm.workbench.dao.CustomerDao;
import com.howie.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    public List<String> getCustomerName(String name) {
        return customerDao.getCustomerName(name);
    }
}
