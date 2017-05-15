package com.miaosu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.miaosu.Page;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.mapper.SupMapper;
import com.miaosu.model.Product;
import com.miaosu.model.ProductSupInfo;
import com.miaosu.model.Sup;
import com.miaosu.service.products.ProductService;
import com.miaosu.service.productsup.ProductSupService;

/**
 * 对接关系的controller
 *
 * @author CaoQi
 * @Time 2015/12/31
 */
@RestController
@RequestMapping("/api/sp")
public class ProductSupController {

    @Autowired
    private ProductSupService productSupService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupMapper supMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public QueryResult<List<ProductSupInfo>> list(@RequestParam("productId") String productId,@RequestParam("productName") String productName,
                                                  @RequestParam("supId")String supId,@RequestParam("supName")String supName,
                                                  @RequestParam("status")String status,@RequestParam(value = "start",defaultValue = "0")int start,
                                                  @RequestParam("limit")int limit) {

        int current = 0;
        current = (start / limit);

        Page<ProductSupInfo> productSupInfoPage = new Page<>(current,limit);
        productSupInfoPage.getPageParam().put("productId",productId);
        productSupInfoPage.getPageParam().put("productName",productName);
        productSupInfoPage.getPageParam().put("supId",supId);
        productSupInfoPage.getPageParam().put("supName",supName);
        productSupInfoPage.getPageParam().put("status",status);


        List<ProductSupInfo> productSupInfoList = productSupService.searchProductSupPage(productSupInfoPage);
        productSupInfoPage.setData(productSupInfoList);
        return new QueryResult<>(true ,(long)productSupInfoPage.getTotalCount(), productSupInfoPage.getData());
    }


    /**
     * 获取列表
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo update(@RequestBody ProductSupInfo productSupInfo) {

        if (StringUtils.isEmpty(productSupInfo.getProductId())
                || StringUtils.isEmpty(productSupInfo.getSupId())){
            return new ResultInfo(true, ResultCode.PARAM_WRONG);
        }

        int row = productSupService.updateProductSup(productSupInfo);
        return new ResultInfo(true, ResultCode.SUCCESSFUL);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo create(@RequestBody ProductSupInfo productSupInfo){

        if (StringUtils.isEmpty(productSupInfo.getProductId()) ||
                StringUtils.isEmpty(productSupInfo.getSupId())){
            return new ResultInfo(false, ResultCode.PARAM_WRONG);
        }

        Product product = productService.get(productSupInfo.getProductId());

        if (product == null){
            logger.warn("商品编号不存在，productId：{}",productSupInfo.getProductId());
            return new ResultInfo(false, ResultCode.PARAM_WRONG);
        }

        Sup sup = supMapper.selectInfo(productSupInfo.getSupId());
        if (sup == null){
            logger.warn("供货商编号不存在，productId：{}",productSupInfo.getProductId());
            return new ResultInfo(false, ResultCode.PARAM_WRONG);
        }

        productSupInfo.setProductId(product.getId());
        productSupInfo.setProductName(product.getName());
        productSupInfo.setSupId(sup.getSupId());
        productSupInfo.setSupName(sup.getSupName());

        if(CollectionUtils.isNotEmpty(productSupService.searchProductSup(productSupInfo))){
            logger.warn("对接关系已经存在，productId：{}，supid:{}",productSupInfo.getProductId(),productSupInfo.getSupId());
            return new ResultInfo(false, ResultCode.PARAM_WRONG);
        }

        productSupService.save(productSupInfo);

        return new ResultInfo(true, ResultCode.SUCCESSFUL);
    }
    
    @RequestMapping(value="querySupList", method=RequestMethod.GET)
    @ResponseBody
    public ResultInfo querySupList(HttpServletRequest request, HttpServletResponse response)
    {
    	List<JSONObject> list = productSupService.querySupList();
    	List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
    	Map<String,String> map = null;
    	for(JSONObject json : list)
    	{
    		map = new HashMap<String,String>();
    		map.put("id", json.getString("id"));
    		map.put("name", json.getString("name"));
    		mapList.add(map);
    	}
    	return new ResultInfo<>(true, ResultCode.SUCCESSFUL, mapList);
    }
}
