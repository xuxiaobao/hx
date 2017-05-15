package com.miaosu.service.orders;

import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Order;
import com.miaosu.model.Product;
import com.miaosu.service.products.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 简单类型
 *
 * @author CaoQi
 * @Time 2015/12/27
 */
@Service
public class SimpleOrderService extends AbstractOrderService {

    @Autowired
    private ProductService productService;

    @Override
    public Product matchProduct(String productId) {
        // Step.3 商品校验
        Product product = productService.get(productId);
        if (product == null || !product.isEnabled()) {
            // 商品不存在或被禁用
            throw new ServiceException(ResultCode.OPEN_PRODUCT_NOT_EXISTS);
        }

        return product;
    }
}
