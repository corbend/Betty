package main.java.managers.messages;


import java.io.Serializable;

public class AccountMessage implements Serializable{

    private String action;
    private String nextAction;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getNextAction() {
        return nextAction;
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    private Long entityId;
    private Long accountId;
    private Double amount;

    public AccountMessage(String action, Long accountId) {
        this.action = action;
        this.accountId = accountId;
    }

    public AccountMessage(String action, String nextAction, Long accountId, Double amount) {
        this.action = action;
        this.nextAction = nextAction;
        this.accountId = accountId;
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
