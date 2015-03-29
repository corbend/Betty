package main.java.models.bets;

import main.java.models.games.Game;
import main.java.models.games.GameEvent;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="bet_groups")
@NamedQueries({
        @NamedQuery(name="BetGroup.getByEvent", query="SELECT bg FROM BetGroup bg WHERE bg.gameEvent=:event")
})
public class BetGroup implements Serializable {

    //группы ставок для различных видов игр

    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name="game_id")
    private Game game;
    @JoinColumn(name="game_event_id")
    private GameEvent gameEvent;

    @Column(name="auto_activate")
    private Boolean autoActivate;

    @ManyToMany(mappedBy = "betGroups")
    public List<CustomBet> bets;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public GameEvent getGameEvent() {
        return gameEvent;
    }
    public void setGameEvent(GameEvent gameEvent) {
        this.gameEvent = gameEvent;
    }

    public List<CustomBet> getBets() {

        if (bets == null) {
            bets = new ArrayList<>();
        }
        return bets;
    }

    public void setBets(List<CustomBet> bets) {
        this.bets = bets;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Boolean getAutoActivate() {
        return autoActivate;
    }

    public void setAutoActivate(Boolean autoActivate) {
        this.autoActivate = autoActivate;
    }

}
