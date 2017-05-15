package com.miaosu.controller;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.miaosu.Page;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.model.OrderStat;
import com.miaosu.model.RechargeFail;
import com.miaosu.model.RechargeFailRatio;
import com.miaosu.report.orderstat.OrderStatForm;
import com.miaosu.report.orderstat.OrderStatService;

/**
 * 账单统计Controller
 * Created by angus on 15/10/20.
 */
@RestController
@RequestMapping("/api/orderstat")
public class OrderStatController {

    @Autowired
    private OrderStatService orderStatService;

    /**
     * 获取列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_SYS_ADMIN"})
    public QueryResult<List<OrderStatForm>> list(@RequestParam(value = "start", required = false) Integer start,
                                            @RequestParam(value = "limit", required = false) Integer size,
                                            @RequestParam(value = "username", required = false) String username,
                                            @RequestParam(value = "productId", required = false) String productId,
                                            @RequestParam(value = "province", required = false) String province,
                                            @RequestParam(value = "operator", required = false) String operator,
                                            @RequestParam(value = "supId", required = false) String supId,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "begin", required = false) Date begin,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "end", required = false) Date end,
                                            @RequestParam(value = "groupConditions", required = false) int[] inputGroupConditions
    ) {
        if (begin == null) {
            begin = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        }
        if (end == null) {
            end = new Date();
        }

        if (end.getTime() - begin.getTime() > 60 * 24 * 60 * 60 * 1000l) {
            throw new ServiceException(ResultCode.DATA_CONSTRAINT_VIOLATION);
        }

        Boolean[] groupConditions = new Boolean[]{false, false, false, false, false, false};

        if (inputGroupConditions == null) {
            groupConditions = new Boolean[]{true, true, true, true, true, true};
        }else{
            for(int i : inputGroupConditions){
                if(i < 6) {
                    groupConditions[i] = true;
                }
            }
        }

//        String currentUserName = principal.getName();
//        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
//                .getAuthorities();
//        // 不允许非管理员用户查询其他用户信息
//        if (!authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
//                && !authorities.contains(new SimpleGrantedAuthority("ROLE_SYS_ADMIN"))
//                && !currentUserName.equals(username)) {
//            username = principal.getName();
//        }
        List<OrderStatForm> orderStatForms = new ArrayList<>();

        Page<OrderStat> orderStats = null;

        int current = 0;
        if (start != null) {
            current = (start / size);
        }

        if(isAllTrue(groupConditions)){
            orderStats = orderStatService.findByCondition(begin, end, username, productId, province, operator, supId,
                new Page(current, size));
        }else {
            orderStats = orderStatService.findByGroup(begin, end, username, productId, province, operator, supId, groupConditions,  new Page(current, size));
        }

        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        for(OrderStat orderStat : orderStats.getData()){
            OrderStatForm orderStatForm = new OrderStatForm();
            BeanUtils.copyProperties(orderStat, orderStatForm);
            String rechargeOkRate = "N/A";
            if(orderStatForm.getTotalCount() != 0l) {
                rechargeOkRate = numberFormat.format(Double.longBitsToDouble(orderStatForm.getRechargeOkSum()) / Double.longBitsToDouble(orderStatForm.getTotalCount()));
            }
            orderStatForm.setRechargeOkRate(rechargeOkRate);
            orderStatForms.add(orderStatForm);
        }

        return new QueryResult<>(true , (long)orderStats.getTotalCount(), orderStatForms);
    }

    private boolean isAllTrue(Boolean[] booleans){
        if (booleans == null){
            return true;
        }
        for (Boolean bool : booleans) {
            if(!bool){
                return false;
            }
        }
        return true;
    }

    /**
     * 数据汇总
     */
    @RequestMapping(value = "/sum", method = {RequestMethod.GET, RequestMethod.POST})
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_SYS_ADMIN"})
    public QueryResult<Map<String, Object>> sum(@RequestParam(value = "username", required = false) String username,
                                                    @RequestParam(value = "productId", required = false) String productId,
                                                    @RequestParam(value = "province", required = false) String province,
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "begin", required = false) Date begin,
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "end", required = false) Date end) {
        if (begin == null) {
            begin = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        }
        if (end == null) {
            end = new Date();
        }

        if (end.getTime() - begin.getTime() > 60 * 24 * 60 * 60 * 1000l) {
            throw new ServiceException(ResultCode.DATA_CONSTRAINT_VIOLATION);
        }

//        String currentUserName = principal.getName();
//        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
//                .getAuthorities();
//        // 不允许非管理员用户查询其他用户信息
//        if (!authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
//                && !authorities.contains(new SimpleGrantedAuthority("ROLE_SYS_ADMIN"))
//                && !currentUserName.equals(username)) {
//            username = principal.getName();
//        }

        Map<String, Object> result = orderStatService.sumByStatDateBetween(begin, end, username, productId, province);

        return new QueryResult<>(true , 1l, result);
    }

    /**
     * 
     * @param username
     * @param supId
     * @param operator
     * @param begin
     * @param end
     * @return
     */
    @RequestMapping(value = "/queryfailphone", method = {RequestMethod.GET, RequestMethod.POST})
    //@Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_SYS_ADMIN"})
    public QueryResult<List<RechargeFail>> queryRechageFailPhone(@RequestParam(value = "start", required = false) Integer start,
            @RequestParam(value = "limit", required = false) Integer size,
            @RequestParam(value = "username", required = false) String userName,
            @RequestParam(value = "supId", required = false) String supId,
            @RequestParam(value = "operator", required = false) String operator,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "begin", required = true) Date begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "end", required = true) Date end)
    {
    	 int current = 0;
         if (start != null) {
             current = (start / size);
         }
         
         Page<RechargeFail> page = orderStatService.queryRechageFailPhone(begin, end, userName, supId, operator, new Page(current, size));
    	
         return new QueryResult<>(true,(long)page.getTotalCount(), page.getData());
    }
    
    /**
     * 
     * @param username
     * @param supId
     * @param operator
     * @param begin
     * @param end
     * @return
     */
    @RequestMapping(value = "/queryfailreason", method = {RequestMethod.GET, RequestMethod.POST})
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_SYS_ADMIN"})
    public QueryResult<List<RechargeFail>> queryRechageFailReason(@RequestParam(value = "start", required = false) Integer start,
            @RequestParam(value = "limit", required = false) Integer size,@RequestParam(value = "username", required = false) String userName,
            @RequestParam(value = "supId", required = false) String supId,
            @RequestParam(value = "operator", required = false) String operator,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "begin", required = true) Date begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "end", required = true) Date end)
    {
    	int current = 0;
        if (start != null) {
            current = (start / size);
        }
        Page<RechargeFail> page = orderStatService.queryRechageFailReson(begin, end, userName, supId, operator, new Page(current, size));
   	
        return new QueryResult<>(true,(long)page.getTotalCount(), page.getData());
    }
    
    
    /**
     * 
     * @param username
     * @param supId
     * @param operator
     * @param begin
     * @param end
     * @return
     */
    @RequestMapping(value = "/queryfailratio", method = {RequestMethod.GET, RequestMethod.POST})
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_SYS_ADMIN"})
    public QueryResult<RechargeFailRatio> queryRechageFailRatio(
            @RequestParam(value = "username", required = false) String userName,
            @RequestParam(value = "supId", required = false) String supId,
            @RequestParam(value = "operator", required = false) String operator,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "begin", required = true) Date begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "end", required = true) Date end)
    {
        RechargeFailRatio ratio = orderStatService.queryRechageFailRatio(begin, end, userName, supId, operator);
        return new QueryResult<RechargeFailRatio>(true, 1L, ratio);
    }
    
}
