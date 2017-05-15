package com.miaosu.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.miaosu.mapper.UserSupLimitMapper;

/**
 * 账单统计任务
 */
@Component
public class SystemTask implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(SystemTask.class);

    @Autowired
    private UserSupLimitMapper userSupLimitMapper;
    /**
     * 每天凌晨2点10分执行
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void execute() {
        userSupLimitMapper.updateZero();
        logger.info("归0");
    }

    @Override
    public void destroy() throws Exception {
    }
}
