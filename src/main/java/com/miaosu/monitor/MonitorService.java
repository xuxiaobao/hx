package com.miaosu.monitor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.miaosu.model.RechargeSuccessRatio;
import com.miaosu.service.locks.LockService;
import com.miaosu.service.orders.AbstractOrderService;

@Service
public class MonitorService
{
	private static Logger logger = LoggerFactory.getLogger(MonitorService.class);

	private static final String RECHARGING_SUM_LOCK = "recharging_sum_lock";

	private static final String RECHARGE_RATIO_LOCK = "recharge_ratio_lock";

	@Value("${monitor.adminmail}")
	private String adminMail = "570225948@qq.com";
	
	@Value("${monitor.mailServer}")
	private String mailServer;
	
	@Value("${monitor.mailAccount}")
	private String mailAccount;
	
	@Value("${monitor.mailPassword}")
	private String mailPassword;
	
	@Autowired
	private AbstractOrderService abstractOrderService;

	private List<String> adminAddr = Arrays.asList(adminMail);

	private ExecutorService pool = Executors.newSingleThreadScheduledExecutor();

	@Autowired
	private LockService lockService;

	@NumberFormat
	@Value("${monitor.recharging.number}")
	private int recharging;

	@NumberFormat
	@Value("${monitor.recharge.ratio}")
	private int successRatio;

	public void alarmBalance(BigDecimal balance, BigDecimal newBalance, String userName, String mailaddr)
	{
		logger.info("userName:{}, mailaddr:{}", userName, mailaddr);
		List<String> to = new ArrayList<String>();
		if (!StringUtils.isEmpty(mailaddr))
		{
			to.add(mailaddr);
		}
		to.addAll(adminAddr);
		if (balance.compareTo(BigDecimal.valueOf(5000l)) >= 0 && newBalance.compareTo(BigDecimal.valueOf(5000l)) < 0)
		{
			logger.info("email for balance 5000 ,user{}", to);
			pool.submit(new SendMail("秒速通知-余额预警",
					"尊敬的" + userName
							+ "客户,你好！\r\n    您的账户余额低于5000元,请及时登录系统查看补款。 \r\n                                                            南京秒速电子科技有限公司",
					to));

		}
		else if (balance.compareTo(BigDecimal.valueOf(10000l)) >= 0 && newBalance.compareTo(BigDecimal.valueOf(10000l)) < 0)
		{
			logger.info("email for balance 10000 ,user{}", to);
			pool.submit(new SendMail("秒速通知-余额预警",
					"尊敬的" + userName
							+ "客户,你好！\r\n    您的账户余额低于10000元,请及时登录系统查看补款。 \r\n                                                           南京秒速电子科技有限公司",
					to));

		}
		else if (balance.compareTo(BigDecimal.valueOf(20000l)) >= 0 && newBalance.compareTo(BigDecimal.valueOf(20000l)) < 0)
		{
			logger.info("email for balance 20000 ,user{}", to);
			pool.submit(new SendMail("秒速通知-余额预警",
					"尊敬的" + userName
							+ "客户,你好！\r\n    您的账户余额低于20000元,请及时登录系统查看补款。 \r\n                                                           南京秒速电子科技有限公司",
					to));
		}
	}

	/**
	 * 每半小时统计一次充值中订单数量
	 */
	@Scheduled(cron = "0 5/30 * * * ?")
	public void startRechargingNum()
	{
		// 获取锁
		boolean locked = lockService.acquireLock(RECHARGING_SUM_LOCK);

		if (locked)
		{
			try
			{
				// 查询充值订单数是否大于100
				Date begin = new Date((new Date().getTime() - 35 * 60 * 1000));
				Date end = new Date((new Date().getTime() - 5 * 60 * 1000));
				int count = abstractOrderService.findRechargingCount(begin, end);
				logger.info("最近半小时充值中的订单数：{}", count);
				if (count >= recharging)
				{
					String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
					pool.submit(
							new SendMail("秒速通知-卡单预警",
									time + ",最近半小时充值中订单数达到" + count + "单，超过警戒值" + recharging
											+ ",请及时处理。 \r\n                                                          南京秒速电子科技有限公司",
									adminAddr));
				}
			}
			catch (Exception ex)
			{
				logger.warn("RECHARGING_SUM_LOCK 发生异常", ex);
			}
			finally
			{
				lockService.releaseLock(RECHARGING_SUM_LOCK);
			}
		}
	}

	@Scheduled(cron = "0 0 0/1 * * ?")
	public void startSuccessRatio()
	{
		// 获取锁
		boolean locked = lockService.acquireLock(RECHARGE_RATIO_LOCK);

		if (locked)
		{
			try
			{
				Date begin = new Date((new Date().getTime() - 60 * 60 * 1000));
				Date end = new Date();
				List<RechargeSuccessRatio> rechargeTotalList = abstractOrderService.findRechargingCountBySup(begin, end);
				List<RechargeSuccessRatio> rechargeSuccessList = abstractOrderService.findRechargingSuccessCountBySup(begin, end);
				StringBuilder sb = null;
				for (RechargeSuccessRatio ratio : rechargeTotalList)
				{
					int success = 0;
					for (RechargeSuccessRatio succ : rechargeSuccessList)
					{
						if (ratio.getName().equals(succ.getName()))
						{
							success = succ.getSuccess();
							break;
						}
					}
					ratio.setSuccess(success);
					logger.info("{}上一个整点通道成功率为{},总订单数{},成功订单数{}", ratio.getName(), ratio.getRatio(), ratio.getTotal(), ratio.getSuccess());
					if (ratio.getRatio() > 0 && ratio.getRatio() < successRatio)
					{
						String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
						logger.info("开始发送邮件(订单成功率过低)");
						sb = new StringBuilder();
						sb.append(time).append(",").append(ratio.getName()).append("供货商上一个整点成功率低于").append(successRatio).append("%(")
								.append(ratio.getRatio()).append("%),总订单数").append(ratio.getTotal()).append(",成功的订单数")
								.append(ratio.getSuccess()).append(",请及时处理")
								.append("\r\n                                                          南京秒速电子科技有限公司");
						pool.submit(new SendMail("秒速通知-成功率预警", sb.toString(), adminAddr));
					}
				}
			}
			catch (Exception ex)
			{
				logger.warn("RECHARGE_RATIO_LOCK 发生异常", ex);
			}
			finally
			{
				lockService.releaseLock(RECHARGE_RATIO_LOCK);
			}
		}
	}

	class SendMail implements Runnable
	{
		private String title;
		private String content;
		private List<String> addr;

		public SendMail(String title, String content, List<String> addr)
		{
			this.title = title;
			this.content = content;
			this.addr = addr;
		}

		@Override
		public void run()
		{
			//String SMTP_MAIL_HOST = "smtp.163.com"; // 此邮件服务器地址
			//String EMAIL_USERNAME = "njms2016@163.com";
			//String EMAIL_PASSWORD = "njms66015756";
			List<Address> address = new ArrayList<Address>();
			/* 服务器信息 */
			Properties props = new Properties();
			props.put("mail.smtp.host", mailServer);
			props.put("mail.smtp.auth", "true");

			/* 创建Session */
			Session session = Session.getDefaultInstance(props, new SimpleAuthenticator(mailAccount, mailPassword));

			/* 邮件信息 */
			MimeMessage message = new MimeMessage(session);
			try
			{
				for (String ad : addr)
				{
					address.add(new InternetAddress(ad));
				}
				Address[] array = (Address[]) address.toArray(new Address[address.size()]);

				message.setFrom(new InternetAddress(mailAccount));
				message.addRecipients(Message.RecipientType.TO, array);
				message.setSubject(title);
				message.setText(content);
				// 发送
				Transport.send(message);
				logger.info("send email success");
			}
			catch (AddressException e)
			{
				logger.warn("send email address error:{}", e);
			}
			catch (MessagingException e)
			{
				logger.warn("send email messaging error:{}", e);
			}
		}
	}

	public void setRecharging(int recharging)
	{
		this.recharging = recharging;
	}

	public void setSuccessRatio(int successRatio)
	{
		this.successRatio = successRatio;
	}
}
