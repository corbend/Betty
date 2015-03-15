package main.java.controllers.bets;

import main.java.managers.bets.BetGroupManager;
import main.java.managers.bets.BetManager;
import main.java.models.bets.BetGroup;
import main.java.models.bets.CustomBet;
import main.java.models.games.GameEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.util.List;

@ManagedBean
public class BetAdminMenuCtrl {

    @EJB
    private BetGroupManager betGroupEJB;

    private BetGroup newBetGroup = new BetGroup();

    public void createNewBetGroup() {

        betGroupEJB.createNewBetGroup(newBetGroup);
    }

}
