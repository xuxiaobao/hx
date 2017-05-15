package com.miaosu.base;

/**
 * 结果码
 * Created by angus on 15/9/29.
 */
public enum ResultCode {
    /**
     * 操作成功；
     */
    SUCCESSFUL("000000","操作成功"),

    /**
     * 操作失败；
     */
    FAILED("000001","操作失败"),

    /**
     * 禁止访问；
     */
    ACCESS_DENIED("000002","禁止访问"),

    /**
     * 参数错误
     */
    PARAM_WRONG("000003","参数错误"),

    /**
     * 数据已存在
     */
    DATA_EXISTS( "000004","数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXISTS ( "000005","数据不存在"),

    /**
     * 数据不合法，违反约束
     */
    DATA_CONSTRAINT_VIOLATION ( "000006","数据不合法，违反约束"),
    
    /**
     * 同一手机号，重复非法计费
     */
    SAME_PHONE_NUMBER_ILLEGAL_BILLING ( "000008","同一手机号，重复非法计费"),

    /**
     * 修改密码时，验证旧密码失败；
     */
    CHANGE_PWD_WITH_WORNG_OLD_PWD ( "000101","修改密码时，验证旧密码失败；"),

    /**
     * 开放接口——用户不存在或被禁用
     */
    OPEN_USER_NOT_EXISTS ( "010001","开放接口——用户不存在或被禁用"),

    /**
     * 开放接口——签名校验异常
     */
    OPEN_SIGN_ERROR ( "010002","开放接口——签名校验异常"),

    /**
     * 开放接口——商品不存在或已下架
     */
    OPEN_PRODUCT_NOT_EXISTS ( "010003","开放接口——商品不存在或已下架"),

    /**
     * 开放接口——余额不足
     */
    OPEN_NO_BALANCE ( "010004","开放接口——余额不足"),

    /**
     * 开放接口——订单不存在
     */
    OPEN_ORDER_NOT_EXISTS ( "010005","开放接口——订单不存在"),
	
	/**
	 * 该供货商下不提供此产品(没有匹配到产品)
	 */
	NOT_SUPORT_PRODUCT("020001","不支持此产品");

    private ResultCode(String code,String msg){
        this.code=code;
        this.msg = msg;
    }

    private String code;

    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
