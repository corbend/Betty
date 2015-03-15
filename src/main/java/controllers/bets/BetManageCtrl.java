package main.java.controllers.bets;

import main.java.models.bets.CustomBet;
import main.java.models.games.GameEvent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
public class BetManageCtrl implements Serializable {

    private List<CustomBet> customBets;
    public List<CustomBet> getCustomBets() {
        return customBets;
    }

    public void setCustomBets(List<CustomBet> customBets) {
        this.customBets = customBets;
    }

    @PersistenceContext
    public EntityManager em;

    private List<GameEvent> events;

    private GameEvent selectedEvent;
    public GameEvent getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(GameEvent selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    private Date scheduleStartDate = new Date();
    public Date getScheduleStartDate() {
        return scheduleStartDate;
    }

    public void setScheduleStartDate(Date scheduleStartDate) {
        this.scheduleStartDate = scheduleStartDate;
    }

    private Date scheduleEndDate = new Date();
    public Date getScheduleEndDate() {
        return scheduleEndDate;
    }

    public void setScheduleEndDate(Date scheduleEndDate) {
        this.scheduleEndDate = scheduleEndDate;
    }

    public void onDateStartSelect() {
        //при выборе даты начала интервала
        this.events = this.getEventScheduleForToday();
    }

    public void onDateEndSelect() {
        //при выборе даты конца интервала
        this.events = this.getEventScheduleForToday();
    }

    @PostConstruct
    public void init() {

        events = getEventScheduleForToday();
        customBets = getAllCustomBets();
    }

    public List<GameEvent> getEvents() {
        return events;
    }

    public void setEvents(List<GameEvent> events) {
        this.events = events;
    }

    public List<GameEvent> getEventScheduleForToday() {

        TypedQuery<GameEvent> tquery = em.createNamedQuery("GameEvent.getForInterval", GameEvent.class);
        tquery.setParameter("dateStart", scheduleStartDate);
        tquery.setParameter("dateEnd", scheduleEndDate);

        return tquery.getResultList();
    }

    public List<CustomBet> getAllCustomBets() {

        return em.createNamedQuery("CustomBet.findAll", CustomBet.class).getResultList();
    }


}
