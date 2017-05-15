package com.miaosu.controller;

import com.miaosu.Page;
import com.miaosu.annotation.Security;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.base.ServiceException;
import com.miaosu.mapper.UserDiscountMapper;
import com.miaosu.model.*;
import com.miaosu.service.discount.UserDiscountService;
import com.miaosu.service.members.MemberForm;
import com.miaosu.service.usersuplimit.UserSupLimitService;
import com.miaosu.util.DESUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 类的描述
 *
 * @author CaoQi
 * @Time 2016/1/10
 */
@Controller
@RequestMapping("/api/userDiscount")
public class DisCountController {

    @Autowired
    private UserDiscountService userDiscountService;
    /**
     * 获取会员列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public QueryResult<List<UserSupLimit>> list(@RequestParam(value = "start", required = false) Integer start,
                                                @RequestParam(value = "limit", required = false) Integer size,
                                                @RequestParam(value = "userName", required = false) String userName,
                                                @RequestParam(value = "pid", required = false) String pid,
                                                HttpServletRequest request) {
        int current = 0;
        if (start != null) {
            current = (start / size);
        }

        Page page = new Page(current, size);
        page.getPageParam().put("userName",userName);
        page.getPageParam().put("pid",pid);
        Page<UserSupLimit> userSupLimitPage = userDiscountService.findPage(page);

        return new QueryResult<>(true,(long)userSupLimitPage.getTotalCount(), userSupLimitPage.getData());
    }


    @RequestMapping(value = "/create")
    @ResponseBody
    public ResultInfo create(@RequestBody UserDiscount userDiscount) {
        if (Double.parseDouble(userDiscount.getDiscount())>1){
            return new ResultInfo(false , ResultCode.FAILED);
        }
        userDiscountService.create(userDiscount);
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }


    /**
     * 修改会员
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultInfo update(@Valid @RequestBody UserDiscount userDiscount) {
        if (Double.parseDouble(userDiscount.getDiscount())>1){
            return new ResultInfo(false , ResultCode.FAILED);
        }
        userDiscountService.update(userDiscount);
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }
}
