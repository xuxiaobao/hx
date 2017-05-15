package com.miaosu.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.miaosu.model.Order;
import com.miaosu.service.export.ExportService;
import com.miaosu.service.locks.LockService;
import com.miaosu.service.orders.AbstractOrderService;

@Component
public class ExportTask
{
	private static Logger LOGGER = LoggerFactory.getLogger(ExportTask.class);

	private static final String EXPORT_ORDER_LOCK = "export_order_lock";

	@Autowired
	private ExportService exportService;

	@Autowired
	private AbstractOrderService abstractOrderService;

	@Value("${export.path}")
	private String path;

	@Value("${export.username}")
	private String usernames;

	@Value("${export.prefix}")
	private String prefix;
	
	private final int pageSize = 500;

	@Autowired
	private LockService lockService;

	@Scheduled(cron = "${export.cron}")
	public void export()
	{
		InetAddress addr = null;
		String ip = "";
		try
		{
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// 获得本机IP
		}
		catch (UnknownHostException e)
		{
			LOGGER.warn("获取IP异常:", e);
		}
		LOGGER.info("订单导出任务，服务IP为{}", ip);
		if (ip.equals("120.55.240.26") || ip.equals("10.46.66.77"))
		{
			// 获取锁
			boolean locked = lockService.acquireLock(EXPORT_ORDER_LOCK);

			if (locked)
			{
				try
				{
					LOGGER.info("获取到订单导出锁");
					String day = new SimpleDateFormat("yyyy-MM-dd").format(new Date(new Date().getTime() - 24 * 3600 * 1000));
					String begin = day + " 00:00:00";
					String end = day + " 23:59:59";

					String[] names = usernames.split("\\|");
					for (String username : names)
					{
						export(username, begin, end);
					}
				}
				catch (Exception ex)
				{
					LOGGER.warn("订单导出任务发生异常{}", ex);
				}
				finally
				{
					lockService.releaseLock(EXPORT_ORDER_LOCK);
				}
			}
			else
			{
				LOGGER.info("未获取到订单导出任务锁！");
			}
		}
	}
	
	@Async(value = "dispatchTask")
	public void export(String username, String begin, String end)
	{
		LOGGER.info("开始导出用户{}的订单", username);
		boolean result = false;
		String day = "";
		if(begin.substring(0, 10).equals(end.substring(0, 10)))
		{
			day = begin.substring(0, 10).replace("-", "");
		}
		else
		{
			day = begin.substring(0, 10).replace("-", "") + "-" + end.substring(0, 10).replace("-", "");
		}
		
		String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		// 查询订单总行数
		int total = abstractOrderService.queryOrderCount(username, begin, end);
		if (total > 0)
		{
			List<Order> orderList = null;
			SXSSFWorkbook wb = new SXSSFWorkbook(100);
			Sheet sh = wb.createSheet();
			Row row = null;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			row = sh.createRow(0);
			row.createCell(0).setCellValue("用户名");
			row.createCell(1).setCellValue("手机号");
			row.createCell(2).setCellValue("省份");
			row.createCell(3).setCellValue("运营商");
			row.createCell(4).setCellValue("供货商");
			row.createCell(5).setCellValue("生效类型");
			row.createCell(6).setCellValue("外部单号");
			row.createCell(7).setCellValue("产品ID");
			row.createCell(8).setCellValue("产品名称");
			row.createCell(9).setCellValue("产品价格");
			row.createCell(10).setCellValue("订单价格");
			row.createCell(11).setCellValue("支付状态");
			row.createCell(12).setCellValue("支付失败原因");
			row.createCell(13).setCellValue("支付ID");
			row.createCell(14).setCellValue("退款ID");
			row.createCell(15).setCellValue("充值状态");
			row.createCell(16).setCellValue("充值失败原因");
			row.createCell(17).setCellValue("回调URL");
			row.createCell(18).setCellValue("支付时间");
			row.createCell(19).setCellValue("充值时间");
			row.createCell(20).setCellValue("创建时间");
			row.createCell(21).setCellValue("充值结束时间");
			for (int i = 0; i * pageSize < total; i++)
			{
				orderList = abstractOrderService.queryOrderList(username, begin, end, pageSize, i * pageSize);
				// 写excel文件
				Order order = null;
				for (int rownum = 0; rownum < pageSize && rownum < orderList.size(); rownum++)
				{
					row = sh.createRow(i * pageSize + rownum + 1);
					order = orderList.get(rownum);
					row.createCell(0).setCellValue(order.getUsername());
					row.createCell(1).setCellValue(order.getPhone());
					row.createCell(2).setCellValue(order.getProvince());
					row.createCell(3).setCellValue(order.getOperator());
					row.createCell(4).setCellValue(order.getSupId());
					row.createCell(5).setCellValue(order.getEffectType());
					row.createCell(6).setCellValue(order.getExternalId());
					row.createCell(7).setCellValue(order.getProductId());
					row.createCell(8).setCellValue(order.getProductName());
					row.createCell(9).setCellValue(order.getProductPrice().toString());
					row.createCell(10).setCellValue(order.getPrice().toString());
					row.createCell(11).setCellValue(order.getPayState().getMsg());
					row.createCell(12).setCellValue(order.getPayFailedReason());
					row.createCell(13).setCellValue(order.getPayId());
					row.createCell(14).setCellValue(order.getRefundId());
					row.createCell(15).setCellValue(order.getRechargeState().getMsg());
					row.createCell(16).setCellValue(order.getRechargeFailedReason());
					row.createCell(17).setCellValue(order.getNotifyUrl());
					row.createCell(18).setCellValue(order.getPayTime() == null ? "" : format.format(order.getPayTime()));
					row.createCell(19)
							.setCellValue(order.getRechargeTime() == null ? "" : format.format(order.getRechargeTime()));
					row.createCell(20)
							.setCellValue(order.getCreateTime() == null ? "" : format.format(order.getCreateTime()));
					row.createCell(21).setCellValue(
							order.getRechargeEndTime() == null ? "" : format.format(order.getRechargeEndTime()));
				}
			}

			String fileName = username + "_" + day + ".xlsx";
			
			FileOutputStream out = null;
			try
			{
				out = new FileOutputStream(path + fileName);
				wb.write(out);
				out.close();
				// dispose of temporary files backing this workbook on disk
				wb.dispose();
				String exportTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				result = exportService.insertExportRecord(username, begin.substring(0, 10), end.substring(0, 10), createTime, exportTime, prefix + fileName);
			}
			catch (Exception e)
			{
				LOGGER.warn("订单单导出异常{}", e);
			}
			finally
			{
				if(out != null)
				{
					try
					{
						out.close();
					}
					catch (IOException e)
					{
						LOGGER.warn("订单单导出任务发生异常{}", e);
					}
				}
			}
		}
		LOGGER.info("用户{}订单导出完成,{}",username, result);
	}
}
