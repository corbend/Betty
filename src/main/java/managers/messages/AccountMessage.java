package main.java.managers.messages;


import java.io.Serializable;

public class AccountMessage implements Serializable {

    private String action;
    private String nextAction;
    private String username;
    private String status;
    private String entityId;
    private Long accountId;
    private Double amount;
    private String receiverJNDI;

    private String outputMessage;

    public AccountMessage() {}

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntityId() {
        return entityId;
    }
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getNextAction() {
        return nextAction;
    }
    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    public String getReceiverJNDI() {
        return receiverJNDI;
    }

    public void setReceiverJNDI(String receiverJNDI) {
        this.receiverJNDI = receiverJNDI;
    }

    public String getOutputMessage() {
        return outputMessage;
    }

    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }
}
