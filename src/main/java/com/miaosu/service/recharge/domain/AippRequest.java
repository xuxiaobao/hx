package com.miaosu.service.recharge.domain;

import lombok.Data;

/**
 * Created by Administrator on 2017/5/19.
 */
@Data
public class AippRequest {
    private String partId;
    private String data;
    private String time;
    private String sign;

}
