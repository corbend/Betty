package main.java.models.bets;

import main.java.models.abc.UserTemporal;
import main.java.models.users.User;
import org.primefaces.model.SelectableDataModel;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="user_bets")
@NamedQueries({
    //@NamedQuery(name="GET_AMOUNT_SUM", query="SELECT COUNT(b.amount) FROM UserBet b WHERE b.status=b.Status.ACTIVE")
        //TODO - оставить только активные ставки
    @NamedQuery(name="UserBet.getActiveByUser", query="SELECT b FROM UserBet b")
})
public class UserBet extends UserTemporal implements Serializable {

    public static enum Status {
        PENDING, ACTIVE, FREEZE, RESOLVED, CANCELLED
    }

    @Id
    @GeneratedValue
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    //модель ставок
    @JoinColumn(name="live_bet_id")
    private LiveBet liveBet;

    @Column(name="ammount")
    private Double amount;

    private Status status;

    private Boolean result;
    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

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

    @Transient
    public String userName;
    public String getUserName() {
        return getUser().getLogin();
    }

    public void setUserName(String user_name) {
        this.userName = user_name;
    }


}
