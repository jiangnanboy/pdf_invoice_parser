package sy.service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发票代码
 * 发票号码
 * 开票日期
 * 校验码
 *
 * 发票金额
 * 大写金额
 * 发票税额
 * 不含税金额
 *
 * 购买方名称
 * 购买方税号
 * 购买方地址、电话
 * 购买方开户行及账号
 *
 * 销售方名称
 * 销售方税号
 * 销售方地址、电话
 * 销售方开户行及账号
 *
 * 发票类型
 * 发票详情
 *
 *
 */
public class Invoice {
    private int msgCode;//返回状态码200成功，500错误
    private String msg;//根据状状码返回相关信息

    private String code;//发票代码
    private String number;//发票号码
    private String date;//开票日期
    private String checksum;//校验码

    private BigDecimal totalAmount; //发票金额
    private String totalAmountString; //大写金额
    private BigDecimal taxAmount; //发票税额
    private BigDecimal amount; //不含税金额

    private String buyerName;//购买方名称
    private String buyerCode;//购买方税号
    private String buyerAddress;//购买方地址、电话
    private String buyerAccount;//购买方开户行及账号

    private String sellerName;//销售方名称
    private String sellerCode;//销售方税号
    private String sellerAddress;//销售方地址、电话
    private String sellerAccount;//销售方开户行及账号

    private String type; //发票类型
    private List<Detail> detailList; //发票详情

    public int getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(int msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getBuyerAccount() {
        return buyerAccount;
    }

    public void setBuyerAccount(String buyerAccount) {
        this.buyerAccount = buyerAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTotalAmountString() {
        return totalAmountString;
    }

    public void setTotalAmountString(String totalAmountString) {
        this.totalAmountString = totalAmountString;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Detail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<Detail> detailList) {
        this.detailList = detailList;
    }

    @Override
    public String toString() {
        return "Invoice [msgCode=" + msgCode +", msg=" + msg + ", code=" + code + ", number=" + number
                + ", date=" + date + ", checksum=" + checksum + ", buyerName=" + buyerName + ", buyerCode=" + buyerCode
                + ", buyerAddress=" + buyerAddress + ", buyerAccount=" + buyerAccount + ", amount="
                + amount + ", taxAmount=" + taxAmount + ", totalAmountString=" + totalAmountString + ", totalAmount="
                + totalAmount + ", sellerName=" + sellerName + ", sellerCode=" + sellerCode + ", sellerAddress=" + sellerAddress
                + ", sellerAccount=" + sellerAccount
                + ", type=" + type + ", detailList=" + detailList + "]";
    }
}

