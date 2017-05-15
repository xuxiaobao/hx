package com.miaosu.controller;

import com.miaosu.Page;
import com.miaosu.annotation.Security;
import com.miaosu.service.acct.AcctService;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.base.ServiceException;
import com.miaosu.service.bill.BillService;
import com.miaosu.model.Bill;
import com.miaosu.model.enums.BillChannel;
import com.miaosu.model.enums.BillStatus;
import com.miaosu.model.enums.BillType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Bill Controller
 * Created by angus on 15/10/2.
 */
@RestController
@RequestMapping("/api/bill")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private AcctService acctService;

    /**
     * 获取列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    @Security(url="/api/bill/search")
    public QueryResult<List<Bill>> list(@RequestParam(value = "start", required = false) Integer start,
                                        @RequestParam(value = "limit", required = false) Integer size,
                                        @RequestParam(value = "username", required = false) String username,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "begin", required = false) Date begin,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "end", required = false) Date end,
                                        @RequestParam(value = "type", required = false) Integer type,
                                        @RequestParam(value = "channel", required = false) Integer channel,
                                        @RequestParam(value = "status", required = false) Integer status) {
        if (begin == null) {
            begin = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        }
        if (end == null) {
            end = new Date();
        }

        if (end.getTime() - begin.getTime() > 7 * 24 * 60 * 60 * 1000) {
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
////            throw new ServiceException(ResultCode.ACCESS_DENIED);
//        }
        BillType billType = (type == null ? null : BillType.forValue(type));
        BillChannel billChannel = (channel == null ? null : BillChannel.forValue(channel));
        BillStatus billStatus = (status == null ? null : BillStatus.forValue(status));

        int current = 0;
        if (start != null) {
            current = (start / size);
        }
        Page<Bill> bills = billService.findByCondition(begin, end, username, billType, billChannel,
                billStatus, new Page(current, size));

//        Page<Bill> bills = billService.find(username, begin, end, PaginationUtil.generatePageRequest(start, size));
        return new QueryResult<>(true,(long)bills.getTotalCount(), bills.getData());
    }

    /**
     * 入账
     */
    @RequestMapping(value = "/checkin", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo checkin(@RequestParam(value = "id") String id) {
        acctService.checkin(id);
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }
}
