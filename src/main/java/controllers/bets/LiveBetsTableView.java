package main.java.controllers.bets;

import main.java.managers.bets.LiveBetsManager;
import main.java.models.bets.LiveBet;
import main.java.models.games.GameEvent;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class LiveBetsTableView implements Serializable {

    @EJB
    private LiveBetsManager liveBetsManager;

    private LiveBet selectedLiveBet;
    public LiveBet getSelectedLiveBet() {
        return selectedLiveBet;
    }

    public void setSelectedLiveBet(LiveBet selectedLiveBet) {
        this.selectedLiveBet = selectedLiveBet;
    }


    public List<LiveBet> getBets() {
        return bets;
    }

    public void setBets(List<LiveBet> bets) {
        this.bets = bets;
    }

    private List<LiveBet> bets;

    @PostConstruct
    public void init() {
        bets = new ArrayList<>();
    }

    public List<LiveBet> getLiveBets(GameEvent gameEvent) {
        return liveBetsManager.getBetsForEvent(gameEvent);
    }

    public void onSelectEvent(SelectEvent event) {
        GameEvent selectedEvent = (GameEvent) event.getObject();
        bets = getLiveBets(selectedEvent);
    }

    public void freeze() {

    }

    public void refresh() {

    }
}
