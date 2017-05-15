package com.miaosu.service.members;

import com.miaosu.Page;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.mapper.BalanceMapper;
import com.miaosu.mapper.MemberMapper;
import com.miaosu.model.Balance;
import com.miaosu.model.Member;
import com.miaosu.model.User;
import com.miaosu.service.user.UserService;
import com.miaosu.util.DESUtil;
import com.miaosu.util.UUIDGen;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Member Service
 * Created by angus on 15/6/19.
 */
@Service
@Transactional(readOnly = true, timeout = 10)
public class MemberService {
    private static Logger logger = LoggerFactory.getLogger(MemberService.class);

    private static final String DAFAULT_PASSWORD_SUFFIX = "1q2w(I";

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BalanceMapper balanceMapper;

    public Member get(String userName) {
        return memberMapper.selectByName(userName);
    }

    public Page<Member> find(String text,Page<Member> page) {
        page.getPageParam().put("text",text);
        List<Member> memberList = memberMapper.selectByCondition(page);
        page.setData(memberList);
        return page;
    }

    @Transactional
    public boolean create(Member member) {
        String userName = member.getUsername();
        if (userService.userExists(userName) != null) {
            throw new ServiceException(ResultCode.DATA_EXISTS);
        }
        User user = new User();
        user.setUserName(userName);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encodePassword(userName + DAFAULT_PASSWORD_SUFFIX, null));
        userService.createUser(user);
        Balance balance =new Balance();
        balance.setUsername(userName);
        balance.setBalance(new BigDecimal(0));
        balance.setCreateTime(new Date());
        balanceMapper.insert(balance);
        return memberMapper.insertInfo(member)==1;
    }

    @Transactional
    public boolean update(Member member) {
        String userName = member.getUsername();
        if (userService.userExists(userName)==null) {
            throw new ServiceException(ResultCode.DATA_NOT_EXISTS);
        }

        return memberMapper.updateInfo(member) ==1;
    }

    @Transactional
    public String resetToken(String userName) {
        if (userService.userExists(userName)==null) {
            throw new ServiceException(ResultCode.DATA_NOT_EXISTS);
        }
        String token = generateToken();
        String encryptToken = DESUtil.encryptToString(token, userName);
        if (!StringUtils.hasText(encryptToken)) {
            logger.warn("秘钥加密失败；token:{}, userName:{}", token, userName);
            throw new ServiceException(ResultCode.FAILED);
        }
        memberMapper.updateToken(userName, encryptToken);
        return token ;
    }

    @Transactional
    public void remove(String... userNames) {
            memberMapper.delByNames(userNames);
            userService.delByNames(userNames);
            balanceMapper.delByNames(userNames);
    }

    public void updateLastLoginInfo(String userName, Date lastLoginTime, String lastLoginIp) {
        memberMapper.updateLastLoginInfo(userName, lastLoginTime, lastLoginIp);
    }

    public String generateToken() {
        return Base64.encodeBase64String(UUIDGen.systemUuid().getBytes());
    }
}
