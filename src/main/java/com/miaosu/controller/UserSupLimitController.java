package com.miaosu.controller;

import com.miaosu.Page;
import com.miaosu.annotation.Security;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Balance;
import com.miaosu.model.Member;
import com.miaosu.model.User;
import com.miaosu.model.UserSupLimit;
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
@RequestMapping("/api/usersuplimit")
public class UserSupLimitController {

    @Autowired
    private UserSupLimitService userSupLimitService;

    /**
     * 获取会员列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public QueryResult<List<UserSupLimit>> list(@RequestParam(value = "start", required = false) Integer start,
                                              @RequestParam(value = "limit", required = false) Integer size,
                                              @RequestParam(value = "userName", required = false) String userName,
                                              @RequestParam(value = "supId", required = false) String supId,
                                              @RequestParam(value = "operator", required = false) String operator,
                                              HttpServletRequest request) {
        int current = 0;
        if (start != null) {
            current = (start / size);
        }

        Page page = new Page(current, size);
        page.getPageParam().put("userName",userName);
        page.getPageParam().put("supId",supId);
        page.getPageParam().put("operator",operator);
        Page<UserSupLimit> userSupLimitPage = userSupLimitService.findPage(page);

        return new QueryResult<>(true,(long)userSupLimitPage.getTotalCount(), userSupLimitPage.getData());
    }


    @RequestMapping(value = "/create")
    @ResponseBody
    public ResultInfo create(@RequestBody UserSupLimit userSupLimit) {
        userSupLimitService.create(userSupLimit);
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }


    /**
     * 修改会员
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultInfo update(@Valid @RequestBody UserSupLimit userSupLimit) {
        userSupLimitService.update(userSupLimit);
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }
}
