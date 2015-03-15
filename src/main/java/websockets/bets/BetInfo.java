package main.java.websockets.bets;


import java.util.List;

public class BetInfo {

    public static final String SEPARATOR = ":";

    public BetInfo(String data) {
        String[] tokens = data.split(SEPARATOR);
        this.userId = tokens[0];
        this.liveBetId = Long.parseLong(tokens[1]);
        this.ammount = Double.parseDouble(tokens[2]);
    }

    public String userId;
    public Long liveBetId;
    public double ammount;

    public Long getLiveBetId() {
        return liveBetId;
    }

    public void setLiveBetId(Long liveBetId) {
        this.liveBetId = liveBetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAmmount() {
        return ammount;
    }

    public void setAmmount(double ammount) {
        this.ammount = ammount;
    }

    @Override
    public String toString() {
        return userId + SEPARATOR + liveBetId + SEPARATOR + ammount;
    }
}
