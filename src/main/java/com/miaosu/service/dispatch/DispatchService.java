package com.miaosu.service.dispatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.miaosu.mapper.UserSupLimitMapper;
import com.miaosu.model.FlowRechargeInfo;
import com.miaosu.model.Order;
import com.miaosu.model.ProductSupInfo;
import com.miaosu.model.enums.Status;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.productsup.ProductSupService;
import com.miaosu.service.recharge.RechargeService;
import com.miaosu.util.SortUtil;

/**
 * 类的描述
 *
 * @author CaoQi
 * @Time 2015/12/27
 */
@Service
public class DispatchService {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProductSupService productSupService;

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private UserSupLimitMapper userSupLimitMapper;

    /**
     * 分配
     * @return
     */
    @Async(value = "dispatchTask")
    public void dispatch(Order order){
        logger.info("开始分配供货商,orderId:{};productName:{}",order.getId(),order.getProductName());
        ProductSupInfo productSupInfo = new ProductSupInfo();
        productSupInfo.setProductId(order.getProductId());
        productSupInfo.setStatus(Status.ON.getCode());
        List<ProductSupInfo> productSupInfoList = productSupService.searchProductSup(productSupInfo);

        if (CollectionUtils.isEmpty(productSupInfoList)){
            logger.warn("没有可用对接关系，productId：{}，orderId：{}",order.getProductId(),order.getId());
            rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(), "没有合适渠道", order.getExternalId());
            return ;
        }

        productSupInfoList = filter(order,productSupInfoList);

        productSupInfoList = orderBy(order, productSupInfoList);

        if (CollectionUtils.isEmpty(productSupInfoList)){
            logger.warn("没有可用对接关系，productId：{}，orderId：{}",order.getProductId(),order.getId());
            rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(), "没有合适渠道", order.getExternalId());
            return ;
        }

        StringBuffer supList = new StringBuffer();
        for (ProductSupInfo temp: productSupInfoList){
            supList.append(temp.getSupId()).append(",");
        }

        order.setSupList(supList.toString());
        order.setSupId(productSupInfoList.get(0).getSupId());
        order.setSupName(productSupInfoList.get(0).getSupName());

        logger.info("orderid:{}选中的供货商是：{}",order.getId(),productSupInfoList.get(0).getSupId());
        abstractOrderService.updateSupInfo(order.getId(),productSupInfoList.get(0).getSupId(),supList.toString());
    }

    /**
     * 过滤
     * @param productSupInfos
     * @return
     */
    private List<ProductSupInfo> filter(Order order , List<ProductSupInfo> productSupInfos){
        logger.info("orderId:{},开始过滤,产品数量{}",order.getId(),productSupInfos.size());
        Iterator<ProductSupInfo> iterator = productSupInfos.iterator();
        while(iterator.hasNext()){
            ProductSupInfo productSupInfo = iterator.next();
            if (blackFilter(order,productSupInfo)){
                iterator.remove();
            }

            if (user_sup_limit_Filter(order,productSupInfo)){
            	logger.info("remove product:{}", productSupInfo.getProductId());
                iterator.remove();
            }
        }

        logger.info("orderId:{},结束过滤",order.getId());
        return productSupInfos;
    }

    private boolean blackFilter(Order order ,ProductSupInfo productSupInfo){
        if (StringUtils.isNotEmpty(productSupInfo.getBlackList()) ){
            //号码段黑名单过滤
            for (String black : productSupInfo.getBlackList().split(",")){
                if (order.getPhone().indexOf(black)!=-1){
                    logger.info("orderId:{},supId:{},号码段黑名单过滤", order.getId() , productSupInfo.getSupId());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean user_sup_limit_Filter(Order order ,ProductSupInfo productSupInfo){

        if (order.getRechargeInfoObj() == null){
            logger.warn("充值对象信息为空");
            return false;
        }

        FlowRechargeInfo flowRechargeInfo = (FlowRechargeInfo) order.getRechargeInfoObj();

        if(null!=userSupLimitMapper.selectInfo(order.getUsername(),flowRechargeInfo.getOperator(),productSupInfo.getSupId())){
            if(userSupLimitMapper.updateAdd(order.getUsername(),flowRechargeInfo.getOperator(),productSupInfo.getSupId())!=1){
                logger.info("orderId：{}，supid：{}商户供货商限制存在，但是超过限制",order.getId(),productSupInfo.getSupId());
                return true;
            }
            logger.info("orderId：{}，supid：{}供货商符合要求",order.getId(),productSupInfo.getSupId());
            return false;            
        }
        else
        {
        	//如果没有配置限制则直接不使用该充值通道
        	return true;
        }
    }

    /**
     * 过滤
     * @param productSupInfos
     * @return
     */
    private List<ProductSupInfo> orderBy(Order order , List<ProductSupInfo> productSupInfos){
        logger.info("orderId:{},List:{},开始排序",order.getId(),productSupInfos);
        List<Integer> weightList = new ArrayList<>();
        for (ProductSupInfo productSupInfo : productSupInfos){
            weightList.add(productSupInfo.getWeight());
        }

        List<ProductSupInfo> selectedList = new ArrayList<>();
        int[] randomIndexes = SortUtil.sortWeightIndex(weightList);

        for (int index : randomIndexes) {
            // 索引-对应可选供货商集合中的位置索引
            // 可选供货商信息实体
            ProductSupInfo productSupInfo = productSupInfos.get(index);
            selectedList.add(productSupInfo);
        }
        logger.info("orderId:{},List:{},结束排序",order.getId(),selectedList);
        return selectedList;
    }
}
