package com.miaosu.service.recharge.task;

import com.miaosu.Page;
import com.miaosu.service.locks.LockService;
import com.miaosu.model.Order;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.recharge.RechargeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订单超时查询job
 */
@Service
public class OrderTimeOutJob {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String RECHARGE_STATUS_LOCK = "recharge_status_lock";

    private ExecutorService pool = Executors. newFixedThreadPool(10);

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private LockService lockService;

    @Autowired
    private RechargeService rechargeService;

    /**
     * 充值中的订单主动更新充值结果
     */
    @Scheduled(cron = "${OrderRechargeJob.cronExpression}")
    public void jobStart() {
        // 获取锁
        boolean locked = lockService.acquireLock(RECHARGE_STATUS_LOCK);

        if (locked) {
            try {
                    logger.info("获取充值状态查询锁");
                    // 获取未充值的订单
                    Page<Order> ordersPage = abstractOrderService.findUnknownRechargeStatusOrders(new Page(0, 200));
                    logger.info("获取到{}条待查询记录",ordersPage.getTotalCount());
                    
                    for (final Order order : ordersPage.getData()) {
                        // 设置订单为充值中
                        pool.execute(new Runnable() {
                            @Override
                            public void run() {
                                rechargeService.resultQuery(order);
                            }
                        });
                    }
            } catch (Exception ex) {
                logger.warn("RechargeStatusQueryTask 发生异常", ex);
            } finally {
                lockService.releaseLock(RECHARGE_STATUS_LOCK);
            }
        } else {
            logger.info("未获取到充值状态查询锁，等待下次执行...");
        }
    }

}
