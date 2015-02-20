package main.java.controllers;

import main.java.managers.bets.BetManager;
import main.java.models.bets.LiveBet;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

//@Named
//@RequestScoped
public class LiveBetCtrl {

    @PersistenceContext
    private EntityManager em;

    private LiveBet bet;

    public String doCreateLiveBet() {
        bet = new LiveBet();
        return "createLiveBet.xhtml";
    }

    public String createLiveBet() {
        em.persist(bet);
        return "liveBetList.xhtml";
    }

    public List<LiveBet> findAllBets() {
        List<LiveBet> lst = em.createNamedQuery("FIND_ALL", LiveBet.class).getResultList();
        return lst;
    }
}
