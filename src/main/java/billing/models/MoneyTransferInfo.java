package main.java.billing.models;

public class MoneyTransferInfo {

    private String description;
    private Double ammount;
    private String action;
    private Long accountId;
    private String currency;

    public String getPaymentServiceName() {
        return paymentServiceName;
    }

    public void setPaymentServiceName(String paymentServiceName) {
        this.paymentServiceName = paymentServiceName;
    }

    private String paymentServiceName;

    public MoneyTransferInfo(String description, Double ammount) {
        this.description = description;
        this.ammount = ammount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getCurrentAmmount() {
        return this.ammount;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
