package com.miaosu.service.bill;

import com.miaosu.Page;
import com.miaosu.mapper.BillMapper;
import com.miaosu.model.Bill;
import com.miaosu.model.enums.BillChannel;
import com.miaosu.model.enums.BillStatus;
import com.miaosu.model.enums.BillType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 账单Service
 */
@Service
public class BillService {

    @Autowired
    private BillMapper billMapper;

    public Page<Bill> findByCondition(Date begin, Date end, String username, BillType type, BillChannel channel,
                                      BillStatus status,Page page) {
        if(begin == null) {
            // begin为空时默认查询一天内的账单
            begin = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        }
        if(end == null) {
            end = new Date();
        }

        page.getPageParam().put("begin",begin);
        page.getPageParam().put("end",end);
        page.getPageParam().put("username",username);
        page.getPageParam().put("type",type);
        page.getPageParam().put("channel",channel);
        page.getPageParam().put("status",status);
        List<Bill> billList = billMapper.selectByCondition(page);
        page.setData(billList);

        return page;
    }

    @Deprecated
    public Page<Bill> find(String username, Date begin, Date end,Page page) {
        if(begin == null) {
            begin = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        }
        if(end == null) {
            end = new Date();
        }

        if (StringUtils.hasText(username)) {

            page.getPageParam().put("begin",begin);
            page.getPageParam().put("end",end);
            page.getPageParam().put("username",username);
            List<Bill> billList = billMapper.selectByCondition(page);
            page.setData(billList);

            return page;
        } else {

            page.getPageParam().put("begin",begin);
            page.getPageParam().put("end",end);
            List<Bill> billList = billMapper.selectByCondition(page);
            page.setData(billList);
            return page;
        }
    }
}
