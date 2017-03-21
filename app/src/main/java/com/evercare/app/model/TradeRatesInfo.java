package com.evercare.app.model;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-30 14:43
 * 邮箱：842202389@qq.com
 * 成交率Model
 */
public class TradeRatesInfo {

    private int total;
    private int consumption_total;
    private double percent;

    private List<DealTradeInfo> lists;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getConsumption_total() {
        return consumption_total;
    }

    public void setConsumption_total(int consumption_total) {
        this.consumption_total = consumption_total;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public List<DealTradeInfo> getLists() {
        return lists;
    }

    public void setLists(List<DealTradeInfo> lists) {
        this.lists = lists;
    }

    public class DealTradeInfo {

        private String custom_id;//"825653",
        private String custom_name;//":"王雪琳",
        private String product_name;//:"术前检查项目-局麻手术",
        private String price;//395.00
        private int is_consumption;

        private String day;//时间

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getCustom_id() {
            return custom_id;
        }

        public void setCustom_id(String custom_id) {
            this.custom_id = custom_id;
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

        public int getIs_consumption() {
            return is_consumption;
        }

        public void setIs_consumption(int is_consumption) {
            this.is_consumption = is_consumption;
        }
    }
}
