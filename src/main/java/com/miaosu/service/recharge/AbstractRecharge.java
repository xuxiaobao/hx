package com.miaosu.service.recharge;

import com.miaosu.model.Order;

import java.util.List;
import java.util.Map;

/**
 * 供货商充值的抽象类
 *
 * @author CaoQi
 * @Time 2015/12/27
 */
public abstract class AbstractRecharge {


    public abstract Map<String, List> products();

    /**
     * 充值接口
     * @param order
     */
    public abstract void recharge(Order order);

    /**
     * 超时查询
     * @param order
     */
    public abstract void queryResult(Order order);

    /**
     * 回调处理
     * @param rechargeResult
     */
    public abstract void callBack(RechargeResult rechargeResult);
}
