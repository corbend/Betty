package main.java.billing.models;

import javax.persistence.*;

@Entity
@Table(name="transactions")
public class Transaction {

    public static enum Status {
        NOT_APPROVED, APPROVED, BLOCKED, UNKNOWN;
    }

    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Account srcAccount;

    private Account destAccount;

    private Double amount;

    public Account getSrcAccount() {
        return srcAccount;
    }

    public void setSrcAccount(Account srcAccount) {
        this.srcAccount = srcAccount;
    }

    public Account getDestAccount() {
        return destAccount;
    }

    public void setDestAccount(Account destAccount) {
        this.destAccount = destAccount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTargetText() {
        return targetText;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    private String uuid;
    private Status status;

    private String targetText;

    public void setAmount(Double v) {
        amount = v;
    }

    public Double getAmount() {
        return amount;
    }
}
