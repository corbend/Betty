package main.java.models.bets;

import main.java.models.abc.UserTemporal;
import main.java.models.games.GameShedule;

import javax.persistence.*;
import java.util.Date;


@Table(name="live_bets")
@NamedQueries({
        @NamedQuery(name="FIND_ALL", query="SELECT g FROM LiveBet g")
})
public class LiveBet extends UserTemporal {

    //модель живых ставок
    @Id
    @GeneratedValue
    private Integer id;

    private String type;

    private Double coeficcient;

    @OneToOne
    @JoinColumn(name="shedule_id")
    private GameShedule shedule;

    @OneToMany
    @JoinColumn(name="user_bet_id")
    private UserBet userBet;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return type;
    }

    public void setShedule(GameShedule v) {
        shedule = v;
    }

    public GameShedule getShedule() {
        return shedule;
    }

    public void setСoeficcient(Double v) {
        coeficcient = v;
    }

    public Double getСoeficcient() {
        return coeficcient;
    }
}
