package main.java.managers.messages;

import java.io.Serializable;

public class BetPutMessage implements Serializable {
    //сообщение для запроса на постановку ставки

    private String type;
    private Long accountId;
    private Long betId;

    public Long getBetId() {
        return betId;
    }

    public void setBetId(Long betId) {
        this.betId = betId;
    }

    public BetPutMessage(String type) {
        this.type = type;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
