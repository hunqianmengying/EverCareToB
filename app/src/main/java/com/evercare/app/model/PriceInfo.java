package com.evercare.app.model;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-2 10:41
 * 邮箱：842202389@qq.com
 * 价目列表
 */
public class PriceInfo {

    private String id;//价目id
    private String business_id;//pc端id
    private String name;//价目名称
    private String price;//价格
    private String frequency;//次数
    private String price_status;//
    private String productstructure;//productstructure=3 时候有套餐
    private List<PackAge> packAge;//套餐内容

    public boolean isSelected = false;

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_status() {
        return price_status;
    }

    public void setPrice_status(String price_status) {
        this.price_status = price_status;
    }

    public String getProductstructure() {
        return productstructure;
    }

    public void setProductstructure(String productstructure) {
        this.productstructure = productstructure;
    }

    public List<PackAge> getPackAge() {
        return packAge;
    }

    public void setPackAge(List<PackAge> packAge) {
        this.packAge = packAge;
    }

    public class PackAge {
        private String id;
        private String business_id;
        private String product_id;
        private String business_pid;
        private String create_time;
        private String update_time;
        private String status;
        private String name;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String price;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBusiness_id() {
            return business_id;
        }

        public void setBusiness_id(String business_id) {
            this.business_id = business_id;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getBusiness_pid() {
            return business_pid;
        }

        public void setBusiness_pid(String business_pid) {
            this.business_pid = business_pid;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
