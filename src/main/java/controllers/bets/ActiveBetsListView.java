package main.java.controllers.bets;

import main.java.managers.bets.BetManager;
import main.java.models.bets.UserBet;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;

@ManagedBean
@ViewScoped
public class ActiveBetsListView implements Serializable {

    @EJB
    private BetManager betManager;

    private List<UserBet> activeBets;

    @PostConstruct
    public void init() {
        activeBets = getActiveBets();
    }

    public List<UserBet> getActiveBets() {

        Principal user = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();

        activeBets = betManager.getActiveBetsByUser(user.getName());
        return activeBets;
    }

    public void setActiveBets(List<UserBet> activeBets) {
        this.activeBets = activeBets;
    }

    private UserBet selectedBet;
    public UserBet getSelectedBet() {
        return selectedBet;
    }

    public void setSelectedBet(UserBet selectedBet) {
        this.selectedBet = selectedBet;
    }

    public void onSelectLiveBet(SelectEvent event) {

        activeBets = getActiveBets();
    }
}
