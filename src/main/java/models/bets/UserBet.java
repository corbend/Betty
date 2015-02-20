package main.java.models.bets;

import main.java.models.abc.UserTemporal;

import javax.persistence.*;


@Table(name="user_bets")
//@NamedQueries({
//    @NamedQuery(name="GET_AMOUNT_SUM", query="SELECT COUNT(b.amount) FROM UserBet b WHERE b.status=b.Status.ACTIVE")
//})
public class UserBet extends UserTemporal {

    public static enum Status {
        PENDING, ACTIVE, FREEZE, RESOLVED, CANCELLED
    }

    @Id
    @GeneratedValue
    private Long id;

    //модель ставок
    @ManyToOne
    @JoinColumn(name="live_bet_id")
    private LiveBet liveBet;

    private Double amount;

    private Status status;

    public void setAmount(Double v) {
        amount = v;
    }

    public Double getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status v) {
        status = v;
    }

    public void setLiveBet(LiveBet v) {
        liveBet = v;
    }

    public LiveBet getLiveBet() {
        return liveBet;
    }

}
