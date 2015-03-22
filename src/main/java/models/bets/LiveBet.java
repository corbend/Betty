package main.java.models.bets;

import main.java.models.abc.UserTemporal;
import main.java.models.games.GameEvent;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="live_bets")
@NamedQueries({
        @NamedQuery(name="LiveBet.findAll", query="SELECT g FROM LiveBet g"),
        @NamedQuery(name="LiveBet.findBetsForEvent", query="SELECT g FROM LiveBet g WHERE g.gameEvent=:gameEvent")
})
public class LiveBet {

    //модель живых ставок
    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private Double coefficient;

    @Transient
    private Date lastChange;

    @Transient
    private Double funds;
    public Double getFunds() {
        return funds;
    }

    public void setFunds(Double funds) {
        this.funds = funds;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_event_id")
    private GameEvent gameEvent;

    @OneToMany(mappedBy = "liveBet", fetch = FetchType.EAGER)
    private List<UserBet> userBets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return type;
    }

    public void setGameEvent(GameEvent v) {
        gameEvent = v;
    }

    public GameEvent getGameEvent() {
        return gameEvent;
    }

    public void setCoefficient(Double v) {
        coefficient = v;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public List<UserBet> getUserBets() {
        return userBets;
    }

    public void setUserBet(List<UserBet> userBets) {
        this.userBets = userBets;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

}
