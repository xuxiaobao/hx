package com.miaosu.controller.openapi;

import com.miaosu.service.balance.BalanceService;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.base.ServiceException;
import com.miaosu.service.members.MemberService;
import com.miaosu.model.Balance;
import com.miaosu.model.Member;
import com.miaosu.model.User;
import com.miaosu.service.user.UserService;
import com.miaosu.util.DESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 会员开放接口
 */
@RestController
@RequestMapping("/openapi/user")
public class OpenUserController extends OpenBaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    @Autowired
    private BalanceService balanceService;

    @RequestMapping(value = "balance", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultInfo<Map<String, Object>> create(@RequestParam(value = "userId") final String userId,
                                                  @RequestParam(value = "sign") final String sign) {
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);

        // Step.1 签名校验
        Member member = memberService.get(userId);
        User user = userService.get(userId);
        if (member == null || user == null || !user.isEnabled()) {
            // 用户不存在或被禁用
            throw new ServiceException(ResultCode.OPEN_USER_NOT_EXISTS);
        }
        checkSign("/openapi/user/balance",paramMap, sign, DESUtil.decryptToString(member.getToken(), userId));

        Balance balance = balanceService.get(userId);
        data.put("userId", userId);
        data.put("balance", balance.getBalance());
        return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);
    }
}