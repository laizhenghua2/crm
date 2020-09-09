package com.howie.crm.workbench.service.impl;

import com.howie.crm.utils.DateTimeUtil;
import com.howie.crm.utils.SqlSessionUtil;
import com.howie.crm.utils.UUIDUtil;
import com.howie.crm.workbench.dao.*;
import com.howie.crm.workbench.domain.*;
import com.howie.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {
    // 线索相关的表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    // 客户相关的表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    // 联系人相关的表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);
    // 交易相关的表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean convert(String clueId,String createBy,Tran tran) {
        // 线索转换
        boolean flag = true;
        String createTime = DateTimeUtil.getSysTime();
        // 1.根据线索id，获取线索对象(线索对象中封装了线索信息)
        Clue clue = clueDao.getById(clueId);
        // 2.通过线索对象提取客户信息，当该客户不存在的时候，新建客户(根据公司的名称精确匹配，判断该客户是否存在)
        String company = clue.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        if(customer == null){
            // 客户不存在，新建一个客户
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setName(company);
            customer.setDescription(clue.getDescription());
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setContactSummary(clue.getContactSummary());
            // 添加客户
            int count1 = customerDao.save(customer);
            if(count1 != 1){
                flag = false;
            }
        }
        /*
        经过第2步处理后，客户的信息已经拥有了，将来在处理其他表的时候，如果使用到客户的id，直接使用customer.getId()
         */
        // 3.通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAddress(clue.getAddress());
        contacts.setAppellation(clue.getAppellation());
        // 添加联系人
        int count2 = contactsDao.save(contacts);
        if(count2 != 1){
            flag = false;
        }
        /*
        经过第3步处理后，联系人的信息已经拥有了，将来在处理其他表的时候，如果使用到联系人的id，直接使用contacts.getId()
         */
        // 4.线索备注转换到客户备注以及联系人备注
        // 查询出与该线索关联的备注信息列表
        List<ClueRemark> clueRemarks = clueRemarkDao.getListByClueId(clueId);
        // 取出每一条线索备注
        for(ClueRemark clueRemark:clueRemarks){
            // 取出备注信息(主要转换到客户备注和联系人备注的备注信息)
            String noteContent = clueRemark.getNoteContent();
            // 创建客户备注对象，添加客户备注
            CustomerRemark cr = new CustomerRemark();
            cr.setId(UUIDUtil.getUUID());
            cr.setNoteContent(noteContent);
            cr.setCreateBy(createBy);
            cr.setCreateTime(createTime);
            cr.setEditFlag("0");
            cr.setCustomerId(customer.getId());
            int count3 = customerRemarkDao.save(cr);
            if(count3 != 1){
                flag = false;
            }
            // 创建联系人备注对象，添加联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setEditFlag("0");
            contactsRemark.setContactsId(contacts.getId());
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4 != 1){
                flag = false;
            }
        }

        // 5."线索和市场活动"的关系转换到"联系人和市场活动"的关系
        // 查询出与该条线索关联的市场活动列表,查询与市场活动的关联关系列表
        List<ClueActivityRelation> clueActivityRelations = clueActivityRelationDao.getListByClueId(clueId);
        // 遍历出每一条与市场活动关联的关联关系记录
        for(ClueActivityRelation car : clueActivityRelations){
            // 创建联系人与市场活动的关联关系对象，让第3步生成的联系人与市场活动做关联
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(car.getActivityId());
            contactsActivityRelation.setContactsId(contacts.getId());

            // 添加联系人与市场活动的关联关系
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5 != 1){
                flag = false;
            }
        }

        // 6.如果有创建交易需求，创建一条交易
        if(tran != null){
            /*
            tran对象在controller里面已经封装好的信息如下：id money name expectedDate stage activityId createBy createTime
            通过第1步生成的clue对象，取出一些信息，继续完善对tran对象的封装
             */
            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(customer.getId());
            tran.setContactSummary(clue.getContactSummary());
            tran.setContactsId(contacts.getId());

            // 添加交易
            int count6 =  tranDao.save(tran);
            if(count6 != 1){
                flag = false;
            }

            // 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setStage(tran.getStage());
            tranHistory.setTranId(tran.getId());

            int count7 = tranHistoryDao.save(tranHistory);
            if(count7 != 1){
                flag = false;
            }
        }

        // 8.删除线索备注
        for(ClueRemark clueRemark:clueRemarks){
            int count8 = clueRemarkDao.delete(clueRemark.getId());
            if(count8 != 1){
                flag = false;
            }
        }

        // 9.删除线索和市场活动的关系
        for(ClueActivityRelation car : clueActivityRelations){
            int count9 = clueActivityRelationDao.delete(car.getId());
            if(count9 != 1){
                flag = false;
            }
        }

        // 10.删除线索
        int count10 = clueDao.delete(clueId);
        if(count10 != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean saveClue(Clue clue) {
        boolean flag = true;
        int count = clueDao.saveClue(clue);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Clue detail(String id) {
        Clue clue = clueDao.detail(id);
        return clue;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> activityList = clueActivityRelationDao.getActivityListByClueId(clueId);
        return activityList;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag = true;
        int count = clueActivityRelationDao.unbund(id);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean bund(Map<String, Object> map) {
        boolean flag = true;
        String clueId = (String) map.get("clueId");
        String[] aids = (String[]) map.get("aids");
        for(String aid:aids){
            String id = UUIDUtil.getUUID();
            ClueActivityRelation car = new ClueActivityRelation(id,clueId,aid);
            int count = clueActivityRelationDao.bund(car);
            if(count != 1){
                flag = false;
            }
        }
        return flag;
    }
}
