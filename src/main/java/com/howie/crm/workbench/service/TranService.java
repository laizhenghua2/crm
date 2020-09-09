package com.howie.crm.workbench.service;

import com.howie.crm.workbench.domain.Tran;

public interface TranService {
    boolean save(Tran tran,String customerName);
}
