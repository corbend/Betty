package main.java.models.games;

import main.java.models.bets.BetType;

public class BetResult {

    private String betType;
    private Integer value;
    private Boolean result;

    public BetResult(String type, Integer value, Boolean result) {
        this.betType = type;
        this.value = value;
        this.result = result;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getBetType() {
        return betType;
    }

    public void setBetType(String betType) {
        this.betType = betType;
    }

    @Override
    public int hashCode() {
        return betType.hashCode();
    }

}
