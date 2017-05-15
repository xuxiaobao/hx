package com.miaosu.controller;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miaosu.controller.openapi.OpenOrderController;
import com.miaosu.monitor.MonitorService;
import com.miaosu.service.recharge.task.RechargeTask;

/**
 * 类的描述
 *
 * @author CaoQi
 * @Time 2016/1/3
 */
@RestController
public class AdminController {

    @Autowired
    private RechargeTask rechargeTask;

    @Autowired
    private OpenOrderController openOrderController;
    
    @Autowired
    private MonitorService monitorService;
    
    @RequestMapping("open")
    public String open(boolean open){

        rechargeTask.setOpen(open);

        return "OK";
    }
    
    /**
     * 邮件警告通知
     * @param open
     * @return
     */
    @RequestMapping("mail")
    public String alarmBalance(String open)
    {
    	openOrderController.setAlarmBalance(open);
    	return "OK";
    }
    

    /**
     * 设置达到指定订单数（充值中）启动告警
     * @param number
     * @return
     */
    @RequestMapping("recharging")
    public String recharging(String number)
    {
    	if(NumberUtils.isNumber(number))
    	{
    		monitorService.setRecharging(Integer.valueOf(number));
    		return "OK";
    	}
    	else
    	{
    		return "ERROR";
    	}
    }
    
    /**
     * 设置充值成功率阈值
     * @param ratio
     * @return
     */
    @RequestMapping("ratio")
    public String successRatio(String ratio)
    {
    	if(NumberUtils.isNumber(ratio))
    	{
    		monitorService.setSuccessRatio(Integer.valueOf(ratio));
    		return "OK";
    	}
    	else
    	{
    		return "ERROR";
    	}
    }
    
}
