package com.miaosu.service.huazong.domain;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.ToString;

/**
 * 校验订购接口返回对象
 */
@Data
@ToString(callSuper=true)
public class CheckOrderFlowResult extends Result{

	private static final long serialVersionUID = 1L;

	/**
     * 状态（Y:可以订购,N:不可订购)
     */
    @JSONField(name = "Status")
    private String status;

    /**
     * 不可订购的原因(成功时为空值)
     */
    @JSONField(name = "FailedReason")
    private String failedReason;

    /**
     * 平台订单号
     */
    @JSONField(name = "OrderNo")
    private String orderNo;
}
