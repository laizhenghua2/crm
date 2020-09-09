package com.howie.crm.workbench.dao;

import com.howie.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerDao {
    Customer getCustomerByName(String name);
    int save(Customer customer);
    List<String> getCustomerName(String name);
}
