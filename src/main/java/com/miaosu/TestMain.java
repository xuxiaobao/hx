package com.miaosu;

import com.alibaba.fastjson.JSON;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.model.Order;
import com.miaosu.model.enums.BillType;
import com.miaosu.util.DESUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类的描述
 *
 * @author CaoQi
 * @Time 2015/12/20
 */
public class TestMain {

    private static AuthenticationManager am = new SampleAuthenticationManager();

    public static void main(String[] args) {
        Order order = new Order();
        order.setCreateTime(new Date());

        List<Order> orderList = new ArrayList<>();
        orderList.add(order);
        QueryResult queryResult = new QueryResult<>(true,1L, orderList);
        System.out.println(JSON.toJSONString(queryResult));
    }
}


class SampleAuthenticationManager implements AuthenticationManager {
    static final List<GrantedAuthority> AUTHORITIES = new ArrayList<>();

    static {
        AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.getName().equals(auth.getCredentials())) {
            return new UsernamePasswordAuthenticationToken(auth.getName(),
                    auth.getCredentials(), AUTHORITIES);
        }
        throw new BadCredentialsException("Bad Credentials");
    }
}
