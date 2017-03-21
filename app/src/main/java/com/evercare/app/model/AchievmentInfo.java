package com.evercare.app.model;

import java.util.List;

/**
 * 作者：xlren on 2016-11-15 16:07
 * 邮箱：renxianliang@126.com
 * 业绩
 */
public class AchievmentInfo {

    private String sale_count;

    private String target;

    private String cash;
    private List<SaleList> sale_list;

    public String getSale_count() {
        return sale_count;
    }

    public void setSale_count(String sale_count) {
        this.sale_count = sale_count;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public List<SaleList> getSale_list() {
        return sale_list;
    }

    public void setSale_list(List<SaleList> sale_list) {
        this.sale_list = sale_list;
    }

    public class SaleList{
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCustom_name() {
            return custom_name;
        }

        public void setCustom_name(String custom_name) {
            this.custom_name = custom_name;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        private String id;
        private String custom_name;
        private String product_name;
        private String price;

    }
}
