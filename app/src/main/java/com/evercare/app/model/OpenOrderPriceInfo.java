package com.evercare.app.model;

import java.util.List;
/**
 * 作者：xlren on 2017-3-1 14:24
 * 邮箱：renxianliang@126.com
 * 今日已经开单详情
 */
public class OpenOrderPriceInfo {

    private String data_time;
    private List<Prodect> product;

    public String getData_time() {
        return data_time;
    }

    public void setData_time(String data_time) {
        this.data_time = data_time;
    }

    public List<Prodect> getProduct() {
        return product;
    }

    public void setProduct(List<Prodect> product) {
        this.product = product;
    }

    public class Prodect{
        private String id;
        private String sales_order_id;
        private String custom_name;
        private String product_id;
        private String product_name;
        private String order_time;
        private String quantity;
        private String product_price;
        private String description;
        private List<PackAge> packAge;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public boolean isSelected = false;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSales_order_id() {
            return sales_order_id;
        }

        public void setSales_order_id(String sales_order_id) {
            this.sales_order_id = sales_order_id;
        }

        public String getCustom_name() {
            return custom_name;
        }

        public void setCustom_name(String custom_name) {
            this.custom_name = custom_name;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getOrder_time() {
            return order_time;
        }

        public void setOrder_time(String order_time) {
            this.order_time = order_time;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getProduct_price() {
            return product_price;
        }

        public void setProduct_price(String product_price) {
            this.product_price = product_price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<PackAge> getPackAge() {
            return packAge;
        }

        public void setPackAge(List<PackAge> packAge) {
            this.packAge = packAge;
        }

        public class PackAge{
            private String id;
            private String sales_order_id;
            private String custom_name;
            private String product_id;
            private String product_name;
            private String parent_id;
            private String order_time;
            private String quantity;
            private String product_price;
            private String product_type_code;

            public String getParent_id() {
                return parent_id;
            }

            public void setParent_id(String parent_id) {
                this.parent_id = parent_id;
            }

            public String getProduct_type_code() {
                return product_type_code;
            }

            public void setProduct_type_code(String product_type_code) {
                this.product_type_code = product_type_code;
            }

            private String description;
            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getSales_order_id() {
                return sales_order_id;
            }

            public void setSales_order_id(String sales_order_id) {
                this.sales_order_id = sales_order_id;
            }

            public String getCustom_name() {
                return custom_name;
            }

            public void setCustom_name(String custom_name) {
                this.custom_name = custom_name;
            }

            public String getProduct_id() {
                return product_id;
            }

            public void setProduct_id(String product_id) {
                this.product_id = product_id;
            }

            public String getProduct_name() {
                return product_name;
            }

            public void setProduct_name(String product_name) {
                this.product_name = product_name;
            }

            public String getOrder_time() {
                return order_time;
            }

            public void setOrder_time(String order_time) {
                this.order_time = order_time;
            }

            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public String getProduct_price() {
                return product_price;
            }

            public void setProduct_price(String product_price) {
                this.product_price = product_price;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }


        }
    }
}
