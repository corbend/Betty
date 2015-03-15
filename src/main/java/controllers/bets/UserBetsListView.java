package main.java.controllers.bets;

import main.java.managers.bets.BetManager;
import main.java.managers.bets.LiveBetsManager;
import main.java.models.bets.LiveBet;
import main.java.models.bets.UserBet;
import main.java.models.games.GameEvent;
import main.java.models.users.User;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class UserBetsListView implements Serializable {

    private List<LiveBet> liveBets;
    public List<LiveBet> getLiveBets() {
        return liveBets;
    }

    public void setLiveBets(List<LiveBet> liveBets) {
        this.liveBets = liveBets;
    }

    private LiveBet selectedLiveBet;
    public LiveBet getSelectedLiveBet() {
        return selectedLiveBet;
    }

    public void setSelectedLiveBet(LiveBet selectedLiveBet) {
        this.selectedLiveBet = selectedLiveBet;
    }

    private UserBet selectedBet;
    public UserBet getSelectedBet() {
        return selectedBet;
    }

    public void setSelectedBet(UserBet selectedBet) {
        this.selectedBet = selectedBet;
    }

    private UserBet newBet = new UserBet();
    public UserBet getNewBet() {
        return newBet;
    }

    public void setNewBet(UserBet newBet) {
        this.newBet = newBet;
    }

    private GameEvent selectedEvent;
    public GameEvent getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(GameEvent selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    @EJB
    private LiveBetsManager liveBetsManager;

    @EJB
    private BetManager betManager;

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        liveBets = this.getBetsForEvent();
    }

    public List<LiveBet> getBetsForEvent() {

        List<LiveBet> emptyList = new ArrayList<>();
        //TODO - тестовый код (убрать после тестирования)
        //emptyList = liveBetsManager.getBetsForEvent(selectedEvent);

        if (selectedEvent == null) {
            return emptyList;
        }

        return liveBetsManager.getBetsForEvent(selectedEvent);
    }

    public void makeNewBet() {
        ExternalContext servletContext = FacesContext
                .getCurrentInstance().getExternalContext();
//        User curUser = em.createNamedQuery("User.findByLogin", User.class).
//                setParameter("login", servletContext.getUserPrincipal().getName()).getSingleResult();
        //TODO - проверить selection в panelGrid т.к кидается NullPointer
        //selectedLiveBet = liveBetsManager.get(1L);
        betManager.putBet(servletContext.getUserPrincipal().getName(), selectedLiveBet, newBet.getAmount());
    }

    public void onSelectGameEvent(SelectEvent event) {

        Object eEvent = event.getObject();

        if (eEvent == null && selectedEvent == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No selection!"));
            return;
        }

        liveBets = this.getBetsForEvent();
    }
}
