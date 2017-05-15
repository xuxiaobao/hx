package com.miaosu.service.productsup;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.miaosu.Page;
import com.miaosu.mapper.ProductSupMapper;
import com.miaosu.mapper.SupMapper;
import com.miaosu.model.ProductSupInfo;

/**
 * 供货关系service
 *
 * @author CaoQi
 * @Time 2015/12/27
 */
@Service
public class ProductSupService {

    @Autowired
    private ProductSupMapper productSupMapper;
    
    @Autowired
    private SupMapper supMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());


    public List<ProductSupInfo> searchProductSupPage(Page<ProductSupInfo> productSupInfoPage){
        return productSupMapper.selectInfoPage(productSupInfoPage);
    }

    public List<ProductSupInfo> searchProductSup(ProductSupInfo productSupInfo){
        return productSupMapper.selectInfo(productSupInfo);
    }

    public int updateProductSup(ProductSupInfo productSupInfo){
        return productSupMapper.updateInfo(productSupInfo);
    }

    public int save(ProductSupInfo productSupInfo){
        int row = productSupMapper.insertInfo(productSupInfo);
        if (row >0){
            logger.info("productID:{},productName:{},supId:{},supName:{};保存成功",productSupInfo.getProductId(),productSupInfo.getProductName(),
                    productSupInfo.getSupId(),productSupInfo.getSupName());

        }
        return row;
    }
    
    public List<JSONObject> querySupList()
    {
    	return supMapper.querySupList();
    }
}
