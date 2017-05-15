package com.miaosu.report;

import com.miaosu.mapper.BalanceMapper;
import com.miaosu.report.billstat.BillStatService;
import com.miaosu.service.locks.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 账单统计任务
 */
@Component
public class BillStatTask implements DisposableBean {
	private static final String BILL_STAT_LOCK = "bill_stats_lock";

	private static final String DATE_BALANCE_LOCK = "date_balance_lock";

	private static final Logger logger = LoggerFactory.getLogger(BillStatTask.class);

	@Autowired
	private LockService lockService;

	@Autowired
	private BillStatService billStatService;

	@Autowired
	private BalanceMapper balanceMapper;

	/**
	 * 每天凌晨2点10分执行
	 */
	@Scheduled(cron = "0 10 3 * * *")
	public void execute() {

		// 获取锁
		boolean locked = lockService.acquireLock(BILL_STAT_LOCK);

		if (locked) {
			try {
				logger.info("获取到账单统计锁");

				Date now = new Date();
				Date yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

				int rows = billStatService.executeStat(simpleDateFormat.format(yesterday),
						simpleDateFormat.format(now));

				logger.info("账单统计任务完成，生成数据{}行", rows);
			} catch (Exception ex) {
				logger.warn("账单统计任务发生异常", ex);
			} finally {
				lockService.releaseLock(BILL_STAT_LOCK);
			}

		} else {
			logger.info("未获取到账单统计锁！");
		}
	}

	/**
	 * 用来保存每天用户的余额，提供给账单统计
	 */
	@Scheduled(cron = "59 59 23 * * *")
	public void saveBalance() {
		// 获取锁
		boolean locked = lockService.acquireLock(DATE_BALANCE_LOCK);
		if (locked) {
			try {
				logger.info("获取到每日余额统计锁");
				int ret = balanceMapper.saveBalance(
						new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis() - 5 * 60 * 1000));
				logger.info("统计账户余额完成{}", ret);
			} catch (Exception e) {
				logger.warn("账户余额统计任务发生异常", e);
			} finally {
				lockService.releaseLock(DATE_BALANCE_LOCK);
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		lockService.releaseLock(BILL_STAT_LOCK);
	}
}
