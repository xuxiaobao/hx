package com.miaosu.controller;

import com.miaosu.Page;
import com.miaosu.annotation.Security;
import com.miaosu.service.acct.AcctService;
import com.miaosu.service.balance.BalanceService;
import com.miaosu.base.*;
import com.miaosu.service.members.MemberForm;
import com.miaosu.service.members.MemberService;
import com.miaosu.model.Balance;
import com.miaosu.model.Member;
import com.miaosu.model.User;
import com.miaosu.service.serialno.SerialNoUtil;
import com.miaosu.service.user.UserService;
import com.miaosu.util.DESUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User Controller
 */
@RestController
@RequestMapping("/api/member")
public class MemberController extends BaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    @Autowired
    private SerialNoUtil serialNoUtil;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private AcctService acctService;

    public static final String LOGIN_KEY="loginUser";

    /**
     * 账户充值
     */
    @RequestMapping(value = "/recharge", method = {RequestMethod.POST})
    @ResponseBody
    public ResultInfo rechargeBalance(@RequestParam String userName, @RequestParam BigDecimal amount,
                                      @RequestParam(required = false) String remark,HttpServletRequest request) {
        Assert.isTrue(amount != null && amount.compareTo(BigDecimal.ZERO) >= 0, "充值金额必须大于0");

        String remarkInfo = "账户充值";
        if(StringUtils.isNotEmpty(remark)){
            remarkInfo = remark;
        }

        String info = String.format("%s(操作人:%s)", remarkInfo,"admin");

        acctService.recharge(userName, amount, info);
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }

    /**
     * 账户扣款
     */
    @RequestMapping(value = "/deduct", method = {RequestMethod.POST})
    @ResponseBody
    public ResultInfo deductBalance(@RequestParam String userName, @RequestParam BigDecimal amount,
                                    @RequestParam(required = false) String remark) {
        Assert.isTrue(amount != null && amount.compareTo(BigDecimal.ZERO) >= 0, "扣款金额必须大于0");

        String remarkInfo = "账户扣款";
        if(StringUtils.isNotEmpty(remark)){
            remarkInfo = remark;
        }
        String info = String.format("%s(操作人:%s)", remarkInfo,"admin");

        acctService.deduct(userName, amount, info);
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }


    /**
     * 生成会员名
     *
     * @return 会员名
     */
    @RequestMapping(value = "/generateMemberName", method = RequestMethod.GET)
    @ResponseBody
    public String generateMemberName() {
        String memberName = serialNoUtil.genrateMemberName();
        if (memberName == null) {
            throw new ServiceException(ResultCode.FAILED);
        }
        return memberName;
    }

    /**
     * 重置接口token
     */
    @RequestMapping(value = "/resetToken", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultInfo resetToken(@RequestParam String userName) {
        String token = memberService.resetToken(userName);
        return new ResultInfo(true, ResultCode.SUCCESSFUL, "新token:" + token);
    }

    /**
     * 获取会员列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    @Security(url="/api/member/search")
    public QueryResult<List<MemberForm>> list(@RequestParam(value = "start", required = false) Integer start,
                                              @RequestParam(value = "limit", required = false) Integer size,
                                              @RequestParam(value = "text", required = false) String text,
                                              HttpServletRequest request) {
        List<MemberForm> memberForms = new ArrayList<>();
        User loginUser = (User) WebUtils.getSessionAttribute(request,LOGIN_KEY);
        if (!loginUser.isAdmin()){
            text = loginUser.getUserName();
        }


        int current = 0;
        if (start != null) {
            current = (start / size);
        }

        Page<Member> members = memberService.find(text, new Page(current, size));

        for (Member member : members.getData()) {
            User user = userService.get(member.getUsername());
            MemberForm memberForm = new MemberForm();
            BeanUtils.copyProperties(member, memberForm);
            memberForm.setEnabled(user.isEnabled());
            String address = String.format("%s, %s, %s, %s", member.getProvince(), member.getCity(), member.getArea(), member.getDetailAddr());
            memberForm.setAddress(address);

            Balance balance = balanceService.get(member.getUsername());
            memberForm.setBalance(balance.getBalance());

            memberForms.add(memberForm);
        }

        return new QueryResult<>(true,(long)members.getTotalCount(), memberForms);
    }

    /**
     * 删除会员
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo remove(@RequestParam("userNames") String... userNames) {
        memberService.remove(userNames);
        return new ResultInfo(true,ResultCode.SUCCESSFUL);
    }

    /**
     * 获取单个会员
     */
    @RequestMapping(value = "/get/{userName}", method = RequestMethod.GET)
    @ResponseBody
    public QueryResult<Member> get(@PathVariable String userName) {
        Member member = memberService.get(userName);
        if (member != null) {
            Member cloneMember = new Member();
            BeanUtils.copyProperties(member, cloneMember);
            cloneMember.setToken(DESUtil.decryptToString(member.getToken(), userName));
            return new QueryResult<>(true , 1l, cloneMember);
        } else {
            throw new ServiceException(ResultCode.DATA_NOT_EXISTS);
        }
    }

    /**
     * 添加会员
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultInfo create(@Valid @RequestBody Member member) {
        String generateToken = memberService.generateToken();
        member.setToken(DESUtil.encryptToString(generateToken, member.getUsername()));
        member.setRegTime(new Date());
        try {
            memberService.create(member);
        } catch (ConstraintViolationException e) {
            throw new ServiceException(ResultCode.DATA_CONSTRAINT_VIOLATION);
        }
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }

    /**
     * 修改会员
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultInfo update(@Valid @RequestBody Member member,HttpServletRequest request) {

        Member userFind = memberService.get(member.getUsername());

        if (userFind != null) {
            User loginUser = (User) WebUtils.getSessionAttribute(request,LOGIN_KEY);
            if (!loginUser.isAdmin()){
                if (!StringUtils.equals(member.getUsername(),loginUser.getUserName())){
                    throw new ServiceException(ResultCode.ACCESS_DENIED);
                }
            }
            member.setToken(DESUtil.encryptToString(member.getToken(), member.getUsername()));
            memberService.update(member);
            return new ResultInfo(true , ResultCode.SUCCESSFUL);
        } else {
            throw new ServiceException(ResultCode.DATA_NOT_EXISTS);
        }
    }

}
