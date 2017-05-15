package com.miaosu.service.orders;

import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.miaosu.Page;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.mapper.BillMapper;
import com.miaosu.mapper.OrderMapper;
import com.miaosu.model.Bill;
import com.miaosu.model.Order;
import com.miaosu.model.Product;
import com.miaosu.model.RechargeSuccessRatio;
import com.miaosu.model.enums.BillChannel;
import com.miaosu.model.enums.BillStatus;
import com.miaosu.model.enums.BillType;
import com.miaosu.model.enums.PayState;
import com.miaosu.model.enums.RechargeState;
import com.miaosu.service.dispatch.DispatchService;
import com.miaosu.service.serialno.SerialNoUtil;

/**
 * 订单Service
 */
public abstract class AbstractOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BillMapper billRepository;

    @Autowired
    private SerialNoUtil serialNoUtil;

    @Autowired
    private DispatchService dispatchService;

    public Page<Order> find(String id, String username, String externalId, Date begin, Date end, String phone,
                            Integer effectType, PayState payState, RechargeState rechargeState, String productId, String supId, String operator,Integer isManual, Page page) {
        if (begin == null) {
            begin = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        }
        if (end == null) {
            end = new Date();
        }

        page.getPageParam().put("id",id);
        page.getPageParam().put("username",username);
        page.getPageParam().put("externalId",externalId);
        page.getPageParam().put("begin",begin);
        page.getPageParam().put("end",end);
        page.getPageParam().put("phone",phone);
        page.getPageParam().put("effectType",effectType);
        page.getPageParam().put("payState",payState);
        page.getPageParam().put("rechargeState",rechargeState);
        page.getPageParam().put("productId",productId);
        page.getPageParam().put("supId",supId);
        page.getPageParam().put("operator", operator);
        page.getPageParam().put("isManual", isManual);
        page.setData(orderMapper.selectByCondition(page));

        return page;
    }

    /**
     * 根据用户名与外部订单号查询订单
     * @param username 用户名
     * @param externalId 外部订单号
     * @return 订单
     */
    public Order findByUsernameAndExternalId(String username, String externalId) {
        return orderMapper.selectByUsernameAndExternalId(username, externalId);
    }

    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单
     */
    public Order get(String id) {
        return orderMapper.selectByIdAndUsername(id, null);
    }

    /**
     * 根据订单ID与用户名查询订单，防止用户越权查询订单
     * @param id 订单ID
     * @param userName 用户名
     * @return 订单
     */
    public Order findByIdAndUsername(String id, String userName) {
        return orderMapper.selectByIdAndUsername(id, userName);
    }

    /**
     * 查询未充值的订单，超过1分钟的订单
     * @param maxOrders 获取数量
     * @return 订单列表
     */
    public List<Order> findUnRechargeOrders(Date start, Date end, int maxOrder) {
        return orderMapper.selectUnRechargeOrders(start, end, maxOrder);
    }

    /**
     * 删除订单
     * @param ids id数组
     */
    public void remove(String... ids) {
        orderMapper.delByIds(ids);
    }


    /**
     *  匹配商品
     * @return
     */
    public abstract Product matchProduct(String productId);

    /**
     * 创建订单，并生成支付流水
     * @param order 订单
     * @return 订单
     */
    @Transactional
    public Order create(Order order) {
        try {
            int row = orderMapper.insertInfo(order);
            if (row==1){
                billRepository.insertInfo(new Bill(order.getPayId(), order.getUsername(), order.getPrice().negate(), null,
                        BillType.SUBTRACTION, BillChannel.PAYMENT, order.getId() + "-订单支付", BillStatus.INIT, new Date()));
            }

//            分配供货商
            dispatchService.dispatch(order);
            return order;
        } catch (ConstraintViolationException ex) {
            throw new ServiceException(ResultCode.DATA_EXISTS);
        }
    }

    /**
     * 充值失败，更新订单充值结果，并生成退款流水
     * @param orderId 订单编号
     * @param failedReason 失败原因
     * @return 退款流水ID
     */
    @Transactional
    public String rechargeFailed(final String orderId, final String failedReason) {
        String billId = serialNoUtil.genrateBillNo();

        // 更新充值结果
        int rows = orderMapper.updateRechargeFailed(orderId, failedReason, billId);

        // 幂等，一个订单只能生成一个退款流水
        if(rows == 1) {
            Order order = orderMapper.selectByIdAndUsername(orderId, null);
            // 生成退款流水
            billRepository.insertInfo(new Bill(billId, order.getUsername(), order.getPrice(), null, BillType.ADD,
                    BillChannel.REFUND, orderId + "-订单退款", BillStatus.INIT, new Date()));
            return billId;
        }
        return null;
    }

    /**
     * 充值成功
     * @param orderId 订单编号
     */
    public void rechargeSuccess(final String orderId) {
        orderMapper.updateRechargeSuccess(orderId);
    }

    /**
     * 设置订单为充值中，只能将未充值的订单设置为充值中
     * @param orderId 订单编号
     * @return 更新行数
     */
    public int setToRecharging(String orderId) {
        return orderMapper.updateToRecharging(orderId);
    }


    /**
     * 设置订单为初始化
     * @param orderId 订单编号
     * @return 更新行数
     */
    public int setToInit(String orderId) {
        return orderMapper.updateToInit(orderId);
    }

    /**
     * 设置充值单号
     * @param id 订单ID
     * @param rechargeId 充值单号
     * @param rechargeSystem 充值系统
     */
    public void setRechargeId(String id, String rechargeId, String rechargeSystem) {
        orderMapper.updateRechargeId(id, rechargeId, rechargeSystem);
    }

    /**
     * 根据充值ID查询指定时间之后的订单
     * @param begin 起始时间
     * @param rechargeId 充值ID
     */
    public Order findByCreateTimeAfterAndRechargeId(Date begin, String rechargeId) {
        return orderMapper.selectByCreateTimeAfterAndRechargeId(begin, rechargeId);
    }
    
    
    /**
     * 根据本系统的OrderID查询指定时间之后的订单
     * @param begin 起始时间
     * @param rechargeId 充值ID
     */
    public Order findByCreateTimeAfterAndOrderId(Date begin, String orderId) {
        return orderMapper.selectByCreateTimeAfterAndOrderId(begin, orderId);
    }
    

    /**
     * 查询5分钟前，60分钟内的充值中的订单
     * @param page 分页信息
     * @return 订单列表
     */
    public Page<Order> findUnknownRechargeStatusOrders(Page page) {
        Date now = new Date();

        page.getPageParam().put("begin",new Date(now.getTime() - 60 * 60 * 1000));
        page.getPageParam().put("end",new Date(now.getTime() - 5 * 60 * 1000));
        List<Order> orderList = orderMapper.selectUnknownRechargeStatusOrders(page);
        page.setData(orderList);
        return page;
    }

    /**
     * 查询1小时内，支付成功、且充值状态大于等于充值中，但充值ID为空的订单
     * @param maxOrders 最大订单数
     * @return 订单列表
     */
    public List<Order> findUnknownRechargeIdOrders(int maxOrders) {
        return orderMapper.selectUnknownRechargeIdOrders(maxOrders);
    }

    /**
     * 根据指定的id查询订单
     * @param ids id数组
     * @return 订单列表
     */
    public List<Order> find(String... ids) {
        return orderMapper.selectByIds(ids);
    }

    public int updateSupInfo(String orderId, String supId, String supList){
        return orderMapper.updateSupInfo(orderId, supId, supList);
    }
    
    /**
     * 更新订单ID
     * @param rechargeId
     * @param orderId
     * @return
     */
    public int updateNotifyRechargeId(String rechargeId,String orderId)
    {
    	return orderMapper.updateNotifyRechargeId(rechargeId,orderId);
    }
    
    /**
     * 查询充值中订单总数
     * @param begin
     * @param end
     * @return
     */
    public int findRechargingCount(Date begin, Date end)
    {
    	return orderMapper.selectRechargingCount(begin, end);
    }
    
    /**
     * 查询充值中订单总数（根据供应商）
     * @param start
     * @param end
     * @return
     */
    public List<RechargeSuccessRatio> findRechargingCountBySup(Date start, Date end)
    {
    	return orderMapper.selectRechargingCountBySup(start, end);
    }
    
    /**
     * 根据供应商查询充值成功总数
     * @param start
     * @param end
     * @return
     */
    public List<RechargeSuccessRatio> findRechargingSuccessCountBySup(Date start, Date end)
    {
    	return orderMapper.selectRechargingSuccessCountBySup(start, end);
    }
    
    /**
     * 查询订单总数
     * @param username
     * @param begin
     * @param end
     * @return
     */
    public int queryOrderCount(String username, String begin, String end)
    {
    	return orderMapper.selectOrderCount(username, begin, end);
    }
    
    /**
     * 查询订单列表
     * @param username
     * @param begin
     * @param end
     * @param limit
     * @param offset
     * @return
     */
    public List<Order> queryOrderList(String username, String begin, String end, 
    		int limit, int offset)
    {
    	return orderMapper.selectOrderList(username, begin, end, limit, offset);
    }
    
    /**
     * 查询同一手机号当前时间300秒内订单
     * @param phone 手机号
     * @return 订单列表
     */
    public List<Order> findTheSamePhoneNumber(String phone) {
        return orderMapper.selectTheSamePhoneNumber(phone);
    }
}

