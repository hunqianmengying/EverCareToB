package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-12-6 16:41
 * 邮箱：842202389@qq.com
 * 项目信息
 */
public class ProjectInfoItem {

    private String productID;//产品源id
    private String quantity;//数量
    private boolean isPriceOverridden = true;//定价
    private String newNum;//次数
    private String newNursingType;//护理类型
    private String pricePerUnit;//单价

    private boolean isPackage;

    public boolean isPackage() {
        return isPackage;
    }

    public void setPackage(boolean aPackage) {
        isPackage = aPackage;
    }

    private String oldPrice;//原始单价

    private String remark;//备注

    private String project_name;

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    private boolean isShowImg = false;

    public boolean isShowImg() {
        return isShowImg;
    }

    public void setShowImg(boolean showImg) {
        isShowImg = showImg;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isPriceOverridden() {
        return isPriceOverridden;
    }

    public void setPriceOverridden(boolean priceOverridden) {
        isPriceOverridden = priceOverridden;
    }

    public String getNewNum() {
        return newNum;
    }

    public void setNewNum(String newNum) {
        this.newNum = newNum;
    }

    public String getNewNursingType() {
        return newNursingType;
    }

    public void setNewNursingType(String newNursingType) {
        this.newNursingType = newNursingType;
    }

    public String getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(String pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
