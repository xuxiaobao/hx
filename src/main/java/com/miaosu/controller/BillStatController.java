package com.miaosu.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miaosu.Page;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.model.BillStat;
import com.miaosu.report.billstat.BillStatService;

/**
 * 账单统计Controller
 * Created by angus on 15/10/20.
 */
@RestController
@RequestMapping("/api/billstat")
public class BillStatController {

    @Autowired
    private BillStatService billStatService;

    /**
     * 获取列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public QueryResult<List<BillStat>> list(@RequestParam(value = "start", required = false) Integer start,
                                            @RequestParam(value = "limit", required = false) Integer size,
                                            @RequestParam(value = "username", required = false) String username,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "begin", required = false) Date begin,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "end", required = false) Date end) {
        if (begin == null) {
            begin = new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
        }
        if (end == null) {
            end = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
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


        int current = 0;
        if (start != null) {
            current = (start / size);
        }

        Page<BillStat> billStats = billStatService.findByCondition(begin, end, username, new Page(current, size));

        return new QueryResult<>(true ,(long)billStats.getTotalCount(), billStats.getData());
    }

    /**
     * 数据汇总
     */
    @RequestMapping(value = "/sum", method = {RequestMethod.GET, RequestMethod.POST})
    public QueryResult<Map<String, BigDecimal>> sum(@RequestParam(value = "start", required = false) Integer start,
                                                    @RequestParam(value = "limit", required = false) Integer size,
                                                    @RequestParam(value = "username", required = false) String username,
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

        Map<String, BigDecimal> result = billStatService.sumByStatDateBetween(begin, end, username);

        return new QueryResult<>(true,1l, result);
    }

}
