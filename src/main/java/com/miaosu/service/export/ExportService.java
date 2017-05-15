package com.miaosu.service.export;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miaosu.Page;
import com.miaosu.mapper.ExportMapper;
import com.miaosu.model.Export;

/**
 * 
 * @author hjimi-test
 *
 */
@Service
public class ExportService
{

	@Autowired
	private ExportMapper exportMapper;
	
	/**
	 * 查询导出列表
	 * @param userName
	 * @param operator
	 * @param supId
	 * @param exportBegin
	 * @param exportEnd
	 * @return
	 */
	public Page<Export> find(String username, String operator, String supId, Date exportBegin, Date exportEnd, Page<Export> page)
	{
		page.getPageParam().put("username", username);
		page.getPageParam().put("exportBegin", exportBegin);
		page.getPageParam().put("exportEnd", exportEnd);
		page.setData(exportMapper.selectByCondition(page));
		return page;
	}
	
	/**
	 * 插入导入
	 * @param username
	 * @param exportBegin
	 * @param exportEnd
	 * @param createTime
	 * @param exportTime
	 * @return
	 */
	public boolean insertExportRecord(String username,String exportBegin, String exportEnd, String createTime, String exportTime, String resource)
	{
		return exportMapper.insertInfo(username, exportBegin, exportEnd, createTime, exportTime, resource) > 0;
	}
}
