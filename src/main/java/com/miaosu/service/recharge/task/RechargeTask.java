package com.miaosu.service.recharge.task;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.miaosu.model.FlowRechargeInfo;
import com.miaosu.model.Order;
import com.miaosu.service.locks.LockService;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.recharge.RechargeService;


/**
 *  查询代充值订单
 */
@Service
public class RechargeTask implements DisposableBean {

    private static final String RECHARGE_LOCK = "recharge_lock";

    private static Logger logger = LoggerFactory.getLogger(RechargeTask.class);

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private LockService lockService;

    @Autowired
    private RechargeService rechargeService;

    private ExecutorService pool = Executors.newFixedThreadPool(30);

    private boolean isOpen = false;

    @Scheduled(fixedDelay = 3000)
    public void execute() {

        if (!isOpen){
            logger.info("未开启，暂不执行");
            return ;
        }
        
        // 获取锁
        boolean locked = lockService.acquireLock(RECHARGE_LOCK);

        if (locked) {
            try {
                // 获取未充值的订单
            	Date start = new Date(new Date().getTime() - 24 * 3600 * 1000);
            	Date end = new Date(new Date().getTime() - 10 * 1000);
                List<Order> orderList = abstractOrderService.findUnRechargeOrders(start, end, 10);
                if(orderList != null && orderList.size() > 0)
                {
                	logger.info("查询待充值订单数:{}", orderList.size());
                }
                for (final Order order : orderList) {
                    // 设置订单为充值中
                    if (setToRecharging(order.getId()) == 1) {
                        if (StringUtils.isNotEmpty(order.getRechargeInfo())){
                            FlowRechargeInfo flowRechargeInfo = JSON.parseObject(order.getRechargeInfo(), FlowRechargeInfo.class);
                            order.setRechargeInfoObj(flowRechargeInfo);
                        }

                        pool.execute(new Runnable() {
                            @Override
                            public void run() {
                                rechargeService.recharge(order);
                            }
                        });
                    }
                }
            } catch (Exception ex) {
                logger.error("RechargeTask 发生异常", ex);
            } finally {
                lockService.releaseLock(RECHARGE_LOCK);
            }
        } else {
            logger.info("未获取到充值锁，等待下次执行...");
        }
    }

    /**
     * 设置订单为充值中，只能将未充值的订单设置为充值中
     * @param orderId 订单编号
     * @return 更新行数
     */
    public int setToRecharging(final String orderId) {
        try {
            return abstractOrderService.setToRecharging(orderId);
        } catch (Exception ex) {
            logger.error("设置订单{}为充值中失败，exMsg:{}", orderId, ex.getMessage());
        }
        return 0;
    }

    @Override
    public void destroy() throws Exception {
        lockService.releaseLock(RECHARGE_LOCK);
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
