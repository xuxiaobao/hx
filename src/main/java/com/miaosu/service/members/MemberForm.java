package com.miaosu.service.members;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * 会员Form
 */
@Data
public class MemberForm implements Serializable {
    /**
     * 会员名
     */
    private String username;

    /**
     * 真实名称
     */
    private String realName;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 性别；0：女；1：男
     */
    private int sex;

    /**
     * 手机号码
     */
    private String mobilePhone;

    /**
     * 地址
     */
    private String address;

    /**
     * 注册时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date regTime;

    /**
     * 上次登录时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /**
     * 上次登录IP
     */
    private String lastLoginIp;

    /**
     * 会员折扣，不大于1
     */
    private BigDecimal discount;

    /**
     * 用户状态
     */
    private boolean enabled;

    /**
     * 用户余额
     */
    private BigDecimal balance;
    
    /**
     * 邮箱
     */
    private String email;
    
}
