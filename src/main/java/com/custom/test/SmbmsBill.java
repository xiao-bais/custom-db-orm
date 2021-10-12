package com.custom.test;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbRelated;
import com.custom.annotations.DbTable;

import java.math.BigDecimal;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/10
 * @Description
 */
@DbTable(table = "smbms_bill")
public class SmbmsBill {

    @DbKey
    private long id;

    @DbField
    private String billCode;
    @DbField
    private String productName;
    @DbField
    private String productDesc;
    @DbField
    private BigDecimal productCount;
    @DbField
    private BigDecimal totalPrice;
    @DbField
    private int isPayment;
    @DbField
    private int providerId;

    @DbRelated(joinTable = "smbms_provider", joinAlias = "sp",  condition = "sp.id = a.providerId" ,field = "proName")
    private String providerName;

    @DbRelated(joinTable = "smbms_provider", joinAlias = "sp",  condition = "sp.id = a.providerId" ,field = "proDesc")
    private String providerDesc;

    @DbRelated(joinTable = "smbms_provider", joinAlias = "sp",  condition = "sp.id = a.providerId" ,field = "proContact")
    private String providerPerson;

    @DbRelated(joinTable = "smbms_provider", joinAlias = "sp",  condition = "sp.id = a.providerId" ,field = "proPhone")
    private String providerPhone;

    @DbRelated(joinTable = "smbms_provider", joinAlias = "sp",  condition = "sp.id = a.providerId" ,field = "proAddress")
    private String providerAddr;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public BigDecimal getProductCount() {
        return productCount;
    }

    public void setProductCount(BigDecimal productCount) {
        this.productCount = productCount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int isPayment() {
        return isPayment;
    }

    public void setPayment(int payment) {
        isPayment = payment;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderDesc() {
        return providerDesc;
    }

    public void setProviderDesc(String providerDesc) {
        this.providerDesc = providerDesc;
    }

    public String getProviderPerson() {
        return providerPerson;
    }

    public void setProviderPerson(String providerPerson) {
        this.providerPerson = providerPerson;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public String getProviderAddr() {
        return providerAddr;
    }

    public void setProviderAddr(String providerAddr) {
        this.providerAddr = providerAddr;
    }

    @Override
    public String toString() {
        return "SmbmsBill{" +
                "id=" + id +
                ", billCode='" + billCode + '\'' +
                ", productName='" + productName + '\'' +
                ", productDesc='" + productDesc + '\'' +
                ", productCount=" + productCount +
                ", totalPrice=" + totalPrice +
                ", isPayment=" + isPayment +
                ", providerId=" + providerId +
                ", providerName='" + providerName + '\'' +
                ", providerDesc='" + providerDesc + '\'' +
                ", providerPerson='" + providerPerson + '\'' +
                ", providerPhone='" + providerPhone + '\'' +
                ", providerAddr='" + providerAddr + '\'' +
                '}';
    }
}
