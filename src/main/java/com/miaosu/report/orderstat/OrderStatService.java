package com.miaosu.report.orderstat;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miaosu.Page;
import com.miaosu.mapper.OrderStatMapper;
import com.miaosu.model.OrderStat;
import com.miaosu.model.RechargeFail;
import com.miaosu.model.RechargeFailRatio;

/**
 * 订单统计服务
 */
@Service
public class OrderStatService {
    @Autowired
    private OrderStatMapper orderStatMapper;

    public Page<OrderStat> findByCondition(Date begin, Date end, String userName, String productId, String province, String operator, String supId, Page page) {
        page.getPageParam().put("begin",begin);
        page.getPageParam().put("end",end);
        page.getPageParam().put("userName",userName);
        page.getPageParam().put("productId",productId);
        page.getPageParam().put("province",province);
        page.getPageParam().put("operator",operator);
        page.getPageParam().put("supId",supId);
        page.setData(orderStatMapper.selectByCondition(page));
        return page;
    }

    public Map<String, Object> sumByStatDateBetween(Date begin, Date end, String userName, String productId, String province) {
        return orderStatMapper.sumByStatDateBetween(begin, end, userName, productId, province);
    }

    /**
     * 执行统计任务
     * @param beginDate 开始统计日期（包含），用yyyy-MM-dd格式
     * @param endDate 结束统计日期（不包含），用yyyy-MM-dd格式
     * @return 统计结果行数
     */
    public int executeStat(String beginDate, String endDate) {
        return orderStatMapper.executeStat(beginDate, endDate);
    }


    /**
     * 按分组查询统计数据
     */
    public Page<OrderStat> findByGroup(Date begin, Date end, String userName, String productId, String province, String operator, String supId, final Boolean[] groupConditions, Page page) {
        page.getPageParam().put("begin",begin);
        page.getPageParam().put("end",end);
        page.getPageParam().put("userName",userName);
        page.getPageParam().put("productId",productId);
        page.getPageParam().put("province",province);
        page.getPageParam().put("operator",operator);
        page.getPageParam().put("supId",supId);
        page.getPageParam().put("groupConditions",groupConditions);

        StringBuffer sb = new StringBuffer();
        boolean isFirst=true;
        for (int i=0;i<groupConditions.length;i++){
            boolean groupCondition = groupConditions[i];
            if (groupCondition){
                if (i==0){
                    if (!isFirst){
                        sb.append(",");
                    }else {
                        isFirst = false;
                    }
                    sb.append("stat_date");
                }else if (i==1){
                    if (!isFirst){
                        sb.append(",");
                    }else {
                        isFirst = false;
                    }
                    sb.append("username");
                }else if(i==2){
                    if (!isFirst){
                        sb.append(",");
                    }else {
                        isFirst = false;
                    }
                    sb.append("province");
                }else if(i == 3){
                    if (!isFirst){
                        sb.append(",");
                    }else {
                        isFirst = false;
                    }
                    sb.append("product_id");
                }
                else if (i == 4)
                {
                	if (!isFirst){
                        sb.append(",");
                    }else {
                        isFirst = false;
                    }
                    sb.append("operator");
                }
                else 
                {
                	if (!isFirst){
                        sb.append(",");
                    }else {
                        isFirst = false;
                    }
                    sb.append("sup_id");
                }
            }
        }

        page.getPageParam().put("groupCondition",sb.toString());
        page.setData(orderStatMapper.selectByGroup(page));
        return page;
    }
    
    /**
     * 
     * @param begin
     * @param end
     * @param userName
     * @param supId
     * @param operator
     * @return
     */
    public Page<RechargeFail> queryRechageFailPhone(Date begin, Date end, String userName, String supId, String operator, Page page)
    {
    	page.getPageParam().put("begin",begin);
        page.getPageParam().put("end",end);
        page.getPageParam().put("userName",userName);
        page.getPageParam().put("supId",supId);
        page.getPageParam().put("operator",operator);
        page.setData(orderStatMapper.selectRechargeFailPhone(page));
		return page;
    }
    
    
    public Page<RechargeFail> queryRechageFailReson(Date begin, Date end, String userName, String supId, String operator, Page page)
    {
    	page.getPageParam().put("begin",begin);
        page.getPageParam().put("end",end);
        page.getPageParam().put("userName",userName);
        page.getPageParam().put("supId",supId);
        page.getPageParam().put("operator",operator);
        page.setData(orderStatMapper.selectRechargeFailReson(page));
		return page;
    }
    
    
    public RechargeFailRatio queryRechageFailRatio(Date begin, Date end, String userName, String supId, String operator)
    {
        int total = orderStatMapper.selectRechargeFailTotal(begin, end, userName, supId, operator);
        int first = orderStatMapper.selectRechargeFirstFail(begin, end, userName, supId, operator);
        RechargeFailRatio rechargeFailRatio = new RechargeFailRatio();
        rechargeFailRatio.setTotal(total);
        rechargeFailRatio.setRepeat(total - first);
        float ratio = 0f;
        if(total > 0 && (total - first) > 0)
        {
        	ratio = Float.valueOf(rechargeFailRatio.getRepeat())/Float.valueOf(rechargeFailRatio.getTotal()) * 100;
        }
        
        DecimalFormat format=new DecimalFormat("#.####");
        rechargeFailRatio.setRatio(format.format(ratio));
		return rechargeFailRatio;
    }
}
