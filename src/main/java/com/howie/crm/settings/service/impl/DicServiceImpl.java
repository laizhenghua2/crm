package com.howie.crm.settings.service.impl;

import com.howie.crm.settings.dao.DicTypeDao;
import com.howie.crm.settings.dao.DicValueDao;
import com.howie.crm.settings.domain.DicType;
import com.howie.crm.settings.domain.DicValue;
import com.howie.crm.settings.service.DicService;
import com.howie.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<String, List<DicValue>> getAll() {
        Map<String,List<DicValue>> map = new HashMap<String,List<DicValue>>();
        // 1.将字典类型列表取出
        List<DicType> dicTypeList = dicTypeDao.getTypeList();
        // 2.将字典类型列表遍历，根据类型取出字典值
        for(DicType type:dicTypeList){
             List<DicValue> list = dicValueDao.getListByCode(type.getCode());
             map.put(type.getCode()+"List",list);
        }
        return map;
    }
}

















