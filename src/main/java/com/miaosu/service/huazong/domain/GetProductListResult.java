package com.miaosu.service.huazong.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 商品列表查询
 */
public class GetProductListResult extends Result {
    /**
     * 账户余额
     */
    @JSONField(name = "Products")
    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public static class Product {
        @JSONField(name = "ProductCode")
        private String productCode;

        @JSONField(name = "ProductName")
        private String productName;

        @JSONField(name = "ProductType")
        private String productType;

        @JSONField(name ="ProductPrice")
        private String productPrice;

        @JSONField(name ="ApplicableArea")
        private String applicableArea;

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        public String getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(String productPrice) {
            this.productPrice = productPrice;
        }

        public String getApplicableArea() {
            return applicableArea;
        }

        public void setApplicableArea(String applicableArea) {
            this.applicableArea = applicableArea;
        }
    }
}
