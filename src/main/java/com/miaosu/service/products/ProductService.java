package com.miaosu.service.products;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miaosu.Page;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.mapper.ProductDetailMapper;
import com.miaosu.mapper.ProductMapper;
import com.miaosu.model.FlowRechargeInfo;
import com.miaosu.model.Product;
import com.miaosu.model.ProductDetail;
import com.miaosu.service.serialno.SerialNoUtil;


/**
 * 商品Service
 */
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductDetailMapper productDetailMapper;

    @Autowired
    private SerialNoUtil serialNoUtil;

    public Page<Product> find(String text, Page page) {
        if (text == null) {
            text = "";
        }

        page.getPageParam().put("text",text);
        page.setData(productMapper.selectByCondition(page));

        return page;
    }

    public List<Product> listAll(){
        return productMapper.selectAll();
    }

    public Product get(String id) {
        Product product = productMapper.selectById(id);
        if (product != null){
            product.setProductDetailList(productDetailMapper.selectByProductId(product.getId()));
        }

        return product;
    }


    public void remove(String... ids) {
        productMapper.delByIds(ids);
    }

    @Transactional
    public Product create(Product product) {
        String id = product.getId();
        if (productMapper.selectById(id)!=null) {
            throw new ServiceException(ResultCode.DATA_EXISTS);
        }

//        插入商品主表
        productMapper.insert(product);

        for (ProductDetail productDetail : product.getProductDetailList()){
            productDetailMapper.insertInfo(productDetail);
        }

        return product;
    }

    public boolean updateProductPrice(String productId, BigDecimal price)
    {
    	return productMapper.updateProductPrice(productId, price) > 0;
    }
    
    public Product update(Product product) {
        String id = product.getId();
        if (productMapper.selectById(id)==null) {
            throw new ServiceException(ResultCode.DATA_NOT_EXISTS);
        }

        productMapper.updateInfo(product);

        return productMapper.selectById(id);
    }

    public void disable(String... ids) {
        productMapper.updateDisable(ids);
    }

    public void enable(String... ids) {
        productMapper.updateEnable(ids);
    }


    public FlowRechargeInfo getFlowRechargeInfo(List<ProductDetail> productDetailList){

        FlowRechargeInfo flowRechargeInfo = new FlowRechargeInfo();

        if (CollectionUtils.isEmpty(productDetailList)){
            return flowRechargeInfo;
        }


        for (ProductDetail productDetail: productDetailList){
            if (StringUtils.equals(productDetail.getProName(),"省份")){
                flowRechargeInfo.setProvince(productDetail.getProValue());
            }

            if (StringUtils.equals(productDetail.getProName(),"生效范围")){
                flowRechargeInfo.setRange(productDetail.getProValue());
            }

            if (StringUtils.equals(productDetail.getProName(),"运营商")){
                flowRechargeInfo.setOperator(productDetail.getProValue());
            }

            if (StringUtils.equals(productDetail.getProName(),"面值")){
                flowRechargeInfo.setPrice(productDetail.getProValue());
            }

            if (StringUtils.equals(productDetail.getProName(),"流量值")){
                flowRechargeInfo.setFlowValue(productDetail.getProValue());
            }

            if (StringUtils.equals(productDetail.getProName(),"生效日期")){
                flowRechargeInfo.setEffectType(productDetail.getProValue());
            }

            if (StringUtils.equals(productDetail.getProName(),"流量有效期")){
                flowRechargeInfo.setExpiredDate(productDetail.getProValue());
            }

        }


        return flowRechargeInfo;
    }
}
