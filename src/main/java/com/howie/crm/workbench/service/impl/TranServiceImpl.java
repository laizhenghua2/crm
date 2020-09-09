package com.howie.crm.workbench.service.impl;

import com.howie.crm.utils.DateTimeUtil;
import com.howie.crm.utils.SqlSessionUtil;
import com.howie.crm.utils.UUIDUtil;
import com.howie.crm.workbench.dao.CustomerDao;
import com.howie.crm.workbench.dao.TranDao;
import com.howie.crm.workbench.dao.TranHistoryDao;
import com.howie.crm.workbench.domain.Customer;
import com.howie.crm.workbench.domain.Tran;
import com.howie.crm.workbench.domain.TranHistory;
import com.howie.crm.workbench.service.TranService;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran tran, String customerName) {
        // 交易添加业务
        /*
        做添加交易之前，参数tran里面少了一项信息，就是客户的主键(customerId),先处理客户相关的需求
        1.判断customerName，根据客户名称在客户表进行精确查询，如果有这个客户，则取出这个客户的id，分装到tran对象中
        如果没有这个客户，则再客户表新建一条客户信息，然后将新建的客户的id取出，封装到tran对象中
        2.经过以上操作后，tran对象中的信息就全有了，需要执行添加交易的操作
        3.添加交易完毕后，需要创建一条交易历史
         */
        boolean flag = true;
        Customer customer = customerDao.getCustomerByName(customerName);
        if(customer == null){
            // 客户为空，需要创建新客户
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(tran.getCreateTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setDescription(tran.getDescription());
            customer.setOwner(tran.getOwner());
            // 添加客户
            int count1 = customerDao.save(customer);
            if(count1 != 1){
                flag = false;
            }
        }

        // 通过以上对于客户的处理，不论是查询出来已有的客户，还是以前没有我们新增的客户，总之客户已经有了，客户的id就有了
        // 将客户的id封装到tran对象中
        tran.setCustomerId(customer.getId());

        // 添加交易
        int count2 = tranDao.save(tran);
        if(count2 != 1){
            flag = false;
        }

        // 添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());

        int count3 = tranHistoryDao.save(tranHistory);
        if(count3 != 1){
            flag = false;
        }
        return flag;
    }
}



















