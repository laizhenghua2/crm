package com.howie.crm.web.listener;

import com.howie.crm.settings.domain.DicValue;
import com.howie.crm.settings.service.DicService;
import com.howie.crm.settings.service.impl.DicServiceImpl;
import com.howie.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

// 监听器与过滤器不一样，监听那个域对象，就得实现那个域的接口
public class SystemInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /*
        该方法是用来监听上下文域对象的方法，当服务器启动，上下文域对象创建，对象创建完毕后，马上执行该方法
        sce:该参数能够取得监听的对象，监听的是什么对象，就可以通过该参数能取得什么对象
        例如我们选择监听的是上下文域对象，通过该参数就可以取得上下文域对象
         */
        System.out.println("服务器缓存数据字典开始");
        // 1.获取上下文作用域对象
        ServletContext application = sce.getServletContext();
        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());
        /*
        业务层提供7个list，可以打包成一个map，业务层应该是这样保存数据的
        map.put("appellationList",list1<DicValue>)
        map.put("ClueStateList",list2<DicValue>)
        map.put("StateList",list3<DicValue>)
        ...
         */
        Map<String, List<DicValue>> map = dicService.getAll();
        // 2.将数据字典的数据分类保存到上下文域对中
        Set<String> keys = map.keySet();
        // 将map解析为上下文域对象中保存的键值对
        for(String key:keys){
            application.setAttribute(key,map.get(key));
            // System.out.println(key);
        }
        System.out.println("服务器缓存数据字典结束");

        // 数据字典处理完成后，处理stageToPossibility.properties文件
        /*
        处理stageToPossibility.properties文件步骤
            解析该文件，将该属性文件中的键值对应关系处理为java中键值对应关系(map)
            Map<String(阶段stage),String(可能性possibility)> pMap = ...
            pMap.put("01资质审查","10");
            pMap.put("02需求分析","25");
            ...

            pMap保存值之后，放在服务器缓存中
            application.setAttribute("PMap",pMap);
         */

        // 解析properties文件
        Map<String,String> pMap = new HashMap<>();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> keyEnums = resourceBundle.getKeys();
        System.out.println(123);
        while(keyEnums.hasMoreElements()){
            // 阶段
            String key = keyEnums.nextElement();
            // 可能性
            String value = resourceBundle.getString(key);
            pMap.put(key,value);
        }

        // 将pMap保存到服务器缓存中
        application.setAttribute("pMap",pMap);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}













