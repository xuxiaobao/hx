package com.miaosu.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miaosu.Page;
import com.miaosu.base.QueryResult;
import com.miaosu.model.Export;
import com.miaosu.report.ExportTask;
import com.miaosu.service.export.ExportService;

@RestController
@RequestMapping("/api/export")
public class ExportController
{
	private static Logger logger = LoggerFactory.getLogger(ExportController.class);

	@Autowired
	private ExportService exportService;

	@Autowired
	private ExportTask exportTask;

	@RequestMapping(value = "search", method = RequestMethod.GET)
	@Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_SYS_ADMIN" })
	public QueryResult queryExportList(@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "limit", required = false) Integer size,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "supid", required = false) String supId,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "begin", required = true) Date exportBegin,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "end", required = true) Date exportEnd)
	{
		int current = 0;
		if (start != null)
		{
			current = (start / size);
		}
		Page<Export> page = exportService.find(username, operator, supId, exportBegin, exportEnd, new Page(current, size));
		return new QueryResult<>(true, (long) page.getTotalCount(), page.getData());
	}

	/**
	 * 主动导出订单
	 * 
	 * @param username
	 * @param begin
	 * @param end
	 * @return
	 */
	@RequestMapping(value = "doexport", method = {RequestMethod.GET, RequestMethod.POST})
	@Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_SYS_ADMIN" })
	public QueryResult exportReport(@RequestParam(value = "username", required = false) String username,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "begin", required = true) String begin,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "end", required = true) String end)
	{
		logger.info("begin export order,{},{},{}", username, begin, end);
		exportTask.export(username, begin + " 00:00:00", end + " 23:59:59");
		return new QueryResult(true, 1L, null);
	}
}
