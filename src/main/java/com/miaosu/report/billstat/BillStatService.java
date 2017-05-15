package com.miaosu.report.billstat;

import com.miaosu.Page;
import com.miaosu.mapper.BillStatMapper;
import com.miaosu.model.BillStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 账单统计服务
 */
@Service
public class BillStatService {

    @Autowired
    private BillStatMapper billStatMapper;

    public Page<BillStat> findByCondition(Date begin, Date end, String userName, Page page) {
        page.getPageParam().put("begin",begin);
        page.getPageParam().put("end",end);
        page.getPageParam().put("userName",userName);
        page.setData(billStatMapper.selectByCondition(page));

        return page;
    }

    public Map<String, BigDecimal> sumByStatDateBetween(Date begin, Date end, String userName) {
        return billStatMapper.sumByStatDateBetween(begin, end, userName);
    }

    /**
     * 执行统计任务
     * @param beginDate 开始统计日期（包含），用yyyy-MM-dd格式
     * @param endDate 结束统计日期（不包含），用yyyy-MM-dd格式
     * @return 统计结果行数
     */
    @Transactional(timeout = 120)
    public int executeStat(String beginDate, String endDate) {
        return billStatMapper.selectStat(beginDate, endDate);
    }
}
