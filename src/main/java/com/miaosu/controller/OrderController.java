package com.miaosu.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import com.miaosu.Page;
import com.miaosu.annotation.Security;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Order;
import com.miaosu.model.User;
import com.miaosu.model.enums.PayState;
import com.miaosu.model.enums.RechargeState;
import com.miaosu.service.huazong.HuaZongPlatform;
import com.miaosu.service.huazong.domain.GetOrderStatusResult;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.recharge.RechargeService;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private HuaZongPlatform huaZongPlatform;

    @Autowired
    private RechargeService rechargeService;

    public static final String LOGIN_KEY="loginUser";

    /**
     * 获取列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Security(url = "/api/order/search")
    public QueryResult<List<Order>> list(@RequestParam(value = "start", required = false) Integer start,
                                         @RequestParam(value = "limit", required = false) Integer size,
                                         @RequestParam(value = "id", required = false) String id,
                                         @RequestParam(value = "username", required = false) String username,
                                         @RequestParam(value = "externalId", required = false) String externalId,
                                         @RequestParam(value = "phone", required = false) String phone,
                                         @RequestParam(value = "effectType", required = false) Integer effectType,
                                         @RequestParam(value = "payState", required = false) Integer payState,
                                         @RequestParam(value = "rechargeState", required = false) Integer rechargeState,
                                         @RequestParam(value = "productId", required = false) String productId,
                                         @RequestParam(value = "supId", required = false) String supId,
                                         @RequestParam(value = "operator", required = false) String operator,
                                         @RequestParam(value = "isManual", required = false, defaultValue = "0") int isManual,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "begin", required = false) Date begin,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "end", required = false) Date end,
                                         HttpServletRequest request) {
        if (begin == null) {
            begin = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        }
        if (end == null) {
            end = new Date();
        }

        if (end.getTime() - begin.getTime() > 7 * 24 * 60 * 60 * 1000) {
            throw new ServiceException(ResultCode.DATA_CONSTRAINT_VIOLATION);
        }

        User loginUser = (User) WebUtils.getSessionAttribute(request,LOGIN_KEY);
        if (!loginUser.isAdmin()){
            username = loginUser.getUserName();
        }


        PayState pay_State = (payState == null ? null : PayState.forValue(payState));
        RechargeState recharge_State = (rechargeState == null ? null : RechargeState.forValue(rechargeState));
        int current = 0;
        if (start != null) {
            current = (start / size);
        }
        Page<Order> productsPage = abstractOrderService.find(id, username, externalId, begin, end, phone,
                effectType, pay_State, recharge_State, productId, supId, operator, isManual,
                new Page(current, size));
        return new QueryResult<>(true,(long)productsPage.getTotalCount(), productsPage.getData());
    }

    @RequestMapping(value = "/checkRechargeState", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo checkRechargeState(@RequestParam("ids") String... ids) {
        List<Order> orders = abstractOrderService.find(ids);

        for (Order order : orders) {
            try {
                if (RechargeState.FAILED.equals(order.getRechargeState())) {
                    logger.info("支付失败的订单不需要核实状态，id: {}", order.getId());

                    // 保护措施，支付失败订单不需要核实状态
                    continue;
                }
                // 充值ID不存在时，先获取充值ID
                if (!StringUtils.hasText(order.getRechargeId())) {
                    try {
                        logger.debug("查询{}的充值单号", order.getId());
                        // 获取充值ID
                        GetOrderStatusResult result = huaZongPlatform.queryOrderStatus(order.getId(), null);

                        // 更新充值单号
                        abstractOrderService.setRechargeId(order.getId(), result.getOrderNo(), "HZ");
                    } catch (Exception ex) {
                        logger.warn("{}订单充值单号查询失败", order.getId(), ex);
                    }
                }

                rechargeService.resultQuery(order);
            } catch (Exception e) {
                logger.warn("查询{}充值状态发生异常, errMsg:{}", order.getId(), e.getMessage());
            }
        }
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }

    @RequestMapping(value = "/setToRechargeFailed", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo setToRechargeFailed(@RequestParam("failedReason") String failedReason, @RequestParam("ids") String[] ids) {
        List<Order> orders = abstractOrderService.find(ids);
        for (Order order : orders) {
            try {
                if (RechargeState.SUCCESS.equals(order.getRechargeState())) {
                    logger.info("{}订单充值已成功，不能设置为失败", order.getId());

                    // 保护措施，已成功的订单不能设置为失败
                    continue;
                }
                rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),
                        "[ADMIN]" + failedReason, order.getExternalId());
            } catch (Exception e) {
                logger.warn("手工设置订单为失败时异常, errMsg:{}", e.getMessage());
            }
        }
        return new ResultInfo(true , ResultCode.SUCCESSFUL);
    }
}
