package com.miaosu.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.miaosu.Page;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Product;
import com.miaosu.model.ProductDetail;
import com.miaosu.model.PropCategory;
import com.miaosu.service.products.CategoryPropService;
import com.miaosu.service.products.ProductService;
import com.miaosu.service.serialno.SerialNoUtil;

/**
 * 商品Controller
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryPropService categoryPropService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SerialNoUtil serialNoUtil;
 
    /**
     * 获取列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public QueryResult<List<Product>> list(@RequestParam(value = "start", required = false) Integer start,
            @RequestParam(value = "limit", required = false) Integer size,
            @RequestParam(value = "text", required = false) String text) {

        int current = 0;
        if (start != null) {
            current = (start / size);
        }
        Page<Product> productsPage = productService.find(text, new Page(current, size));
        return new QueryResult<>(true ,(long)productsPage.getTotalCount(), productsPage.getData());
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo remove(@RequestParam("ids") String... ids) {
        productService.remove(ids);
        return new ResultInfo(true, ResultCode.SUCCESSFUL);
    }

    /**
     * 获取单个
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public QueryResult<Map<String,Object>> get(@PathVariable String id) {
        Product product = productService.get(id);
        
        Map<String,Object> pdt = new HashMap<String,Object>();
        List<ProductDetail> detailList = product.getProductDetailList();
        for(ProductDetail detail : detailList)
        {
        	if("1".equals(detail.getProId()))
        	{
        		pdt.put("province", detail.getProValue());
        	}
        	if("2".equals(detail.getProId()))
        	{
        		pdt.put("range", detail.getProValue());
        	}
        	if("3".equals(detail.getProId()))
        	{
        		pdt.put("operator", detail.getProValue());
        	}
        	if("4".equals(detail.getProId()))
        	{
        		pdt.put("faceValue", detail.getProValue());
        	}
        	if("5".equals(detail.getProId()))
        	{
        		pdt.put("flowValue", detail.getProValue());
        	}
        	if("6".equals(detail.getProId()))
        	{
        		pdt.put("effectType", detail.getProValue());
        	}
        	if("7".equals(detail.getProId()))
        	{
        		pdt.put("expiredDate", detail.getProValue());
        	}
        }
        pdt.put("id", product.getId());
        pdt.put("enabled",product.isEnabled());
        pdt.put("price", product.getPrice().toString());
        
        if (product != null) {
            return new QueryResult<Map<String,Object>>(true ,1L, pdt);
        } else {
            throw new ServiceException(ResultCode.DATA_NOT_EXISTS);
        }
    }

    /**
     * 添加会
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultInfo create(@Valid @RequestBody String params) {
        try {
            Map<String,String> map = JSON.parseObject(params, Map.class);

            String provinceCondi = map.get("province");


            List<PropCategory> propCategoryList =categoryPropService.findAllById("1");

            String template = "{省份}{生效范围}{运营商}{面值}元{流量值}M{生效日期}{流量有效期}流量包";

            String[] provinces=new String[]{"北京市","天津市","上海市","重庆市","河北","河南","云南",
                    "辽宁","黑龙江","湖南","安徽","山东","新疆维吾尔","江苏","浙江","江西","湖北",
                    "广西壮族","甘肃","山西","内蒙古","陕西","吉林","福建","贵州","广东","青海",
                    "西藏","四川","宁夏回族","海南","台湾"};
            //for (String province:provinces){
                try {
                    Product product = new Product();
                    product.setEnabled(Boolean.parseBoolean(map.get("enabled")));

                    //if (StringUtils.isEmpty(map.get("id"))){
                    	//如果没有商品编号就自动生成一个
                        //product.setId(serialNoUtil.genrateProductNo());
                    	product.setId(map.get("id"));
                    //}

//                  第一个是省份 第二个是使用范围
                    product.setName(provinceCondi+map.get("range")+map.get("operator")+
                            map.get("flowValue")+"M"+map.get("effectType")+map.get("expiredDate")+"流量包");

                    logger.info("新增商品，{}",product.getName());

                    for (PropCategory propCategory : propCategoryList){

                        if (StringUtils.equals(propCategory.getPropName(),"省份")){
                            ProductDetail productDetail = new ProductDetail();
                            productDetail.setProductId(product.getId());
                            productDetail.setProductName(product.getName());
                            productDetail.setProId(propCategory.getPropId());
                            productDetail.setProName(propCategory.getPropName());
                            //if (StringUtils.equals("全国",provinceCondi)){
                                //productDetail.setProValue("全国");
                            //}else{
                            productDetail.setProValue(provinceCondi);
                            //}

                            product.getProductDetailList().add(productDetail);
                        }


                        if (StringUtils.equals(propCategory.getPropName(),"生效范围")){
                            ProductDetail productDetail = new ProductDetail();
                            productDetail.setProductId(product.getId());
                            productDetail.setProductName(product.getName());
                            productDetail.setProId(propCategory.getPropId());
                            productDetail.setProName(propCategory.getPropName());
                            productDetail.setProValue(map.get("range"));
                            product.getProductDetailList().add(productDetail);
                        }

                        if (StringUtils.equals(propCategory.getPropName(),"运营商")){
                            ProductDetail productDetail = new ProductDetail();
                            productDetail.setProductId(product.getId());
                            productDetail.setProductName(product.getName());
                            productDetail.setProId(propCategory.getPropId());
                            productDetail.setProName(propCategory.getPropName());
                            productDetail.setProValue(map.get("operator"));
                            product.getProductDetailList().add(productDetail);
                        }


                        if (StringUtils.equals(propCategory.getPropName(),"面值")){
                            ProductDetail productDetail = new ProductDetail();
                            productDetail.setProductId(product.getId());
                            productDetail.setProductName(product.getName());
                            productDetail.setProId(propCategory.getPropId());
                            productDetail.setProName(propCategory.getPropName());
                            productDetail.setProValue(map.get("faceValue"));
                            product.setPrice(new BigDecimal(map.get("faceValue")));
                            product.getProductDetailList().add(productDetail);
                        }

                        if (StringUtils.equals(propCategory.getPropName(),"流量值")){
                            ProductDetail productDetail = new ProductDetail();
                            productDetail.setProductId(product.getId());
                            productDetail.setProductName(product.getName());
                            productDetail.setProId(propCategory.getPropId());
                            productDetail.setProName(propCategory.getPropName());
                            productDetail.setProValue(map.get("flowValue"));
                            product.getProductDetailList().add(productDetail);
                        }

                        if (StringUtils.equals(propCategory.getPropName(),"生效日期")){
                            ProductDetail productDetail = new ProductDetail();
                            productDetail.setProductId(product.getId());
                            productDetail.setProductName(product.getName());
                            productDetail.setProId(propCategory.getPropId());
                            productDetail.setProName(propCategory.getPropName());
                            productDetail.setProValue(map.get("effectType"));
                            product.getProductDetailList().add(productDetail);
                        }

                        if (StringUtils.equals(propCategory.getPropName(),"流量有效期")){
                            ProductDetail productDetail = new ProductDetail();
                            productDetail.setProductId(product.getId());
                            productDetail.setProductName(product.getName());
                            productDetail.setProId(propCategory.getPropId());
                            productDetail.setProName(propCategory.getPropName());
                            productDetail.setProValue(map.get("expiredDate"));
                            product.getProductDetailList().add(productDetail);
                        }
                    }

                    productService.create(product);

                    /*if (StringUtils.equals("全国",provinceCondi)){
                        break;
                    }*/
                } catch (Exception e) {
                    logger.error("添加商品失败",e);
                }
            //}
        } catch (ConstraintViolationException e) {
            throw new ServiceException(ResultCode.DATA_CONSTRAINT_VIOLATION);
        }
        return new ResultInfo(true, ResultCode.SUCCESSFUL);
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultInfo update(@Valid @RequestBody Product product) {
    	productService.updateProductPrice(product.getId(), product.getFaceValue());
        return new ResultInfo(true, ResultCode.SUCCESSFUL);
    }


    /**
     * 禁用
     */
    @RequestMapping(value = "/disable", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo disable(@RequestParam("ids") String... ids) {
        productService.disable(ids);
        return new ResultInfo(true, ResultCode.SUCCESSFUL);
    }


    /**
     * 启用
     */
    @RequestMapping(value = "/enable", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo enable(@RequestParam("ids") String... ids) {
        productService.enable(ids);
        return new ResultInfo(true, ResultCode.SUCCESSFUL);
    }

    @RequestMapping(value = "/category")
    @ResponseBody
    public ResultInfo category(@RequestParam("id") String id) {
        ResultInfo resultInfo = new ResultInfo(true, ResultCode.SUCCESSFUL);
        resultInfo.setData(categoryPropService.findAllById(id));
        return resultInfo;
    }
}
