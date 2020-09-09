package com.howie.settings.test;
import com.howie.crm.utils.DateTimeUtil;
import com.howie.crm.utils.MD5Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test1 {
    public static void main(String[] args) {
        // 1.验证失效时间
        // String expireTime = "2019-10-10 10:10:10"; // 假设失效时间
        // 获取当前系统时间
        /*String currentDate = DateTimeUtil.getSysTime();
        int result = expireTime.compareTo(currentDate);
        System.out.println(result);*/

        /*Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newDate = sdf.format(date);
        System.out.println(newDate);*/

        // 2.验证锁定状态
        /*String lockState = "0";
        if("0".equals(lockState)){
            System.out.println("账号已锁定！");
        }*/

        // 3.验证ip地址
        /*String ip = "192.167.10"; // 假设是浏览器端的ip地址
        String allowIps = "192.167.10,192.167.20"; // 允许访问的ip地址群
        if(allowIps.contains(ip)){
            System.out.println("有效ip地址，允许访问！");
        }else{
            System.out.println("无效ip地址，请联系管理员！");
        }*/

        // 4.MD5加密方式验证(不可逆的加密方式)
        /*String password = "abc";
        System.out.println(MD5Util.getMD5(password)); // 900150983cd24fb0d6963f7d28e17f72*/

        /*String sysTime = DateTimeUtil.getSysTime();
        System.out.println(sysTime);*/
    }
}
