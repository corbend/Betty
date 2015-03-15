package main.java.controllers.bets;


import main.java.managers.games.GameManager;
import main.java.managers.games.GameSheduleManager;
import main.java.models.games.Game;
import main.java.models.games.GameEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class UserEventsListView implements Serializable {

    private Date selectedDate;
    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    private Game selectedGame;
    public Game getSelectedGame() {
        return selectedGame;
    }

    public void setSelectedGame(Game selectedGame) {
        this.selectedGame = selectedGame;
    }


    private GameEvent selectedEvent;
    public GameEvent getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(GameEvent selectedEvent) {
        this.selectedEvent = selectedEvent;
    }


    @EJB
    private GameSheduleManager gameSheduleManager;

    @EJB
    private GameManager gameManager;

    public List<GameEvent> getEvents() {

        List<GameEvent> eventList = new ArrayList<>();

        if (selectedDate == null) {
            selectedDate = new Date();
        }

        List<Game> gamesToSearch = new ArrayList<>();

        if (selectedGame == null) {
            gamesToSearch = gameManager.findAllGames();
        } else {
            gamesToSearch.add(selectedGame);
        }

        for (Game game: gamesToSearch) {
            eventList.addAll(gameSheduleManager.getAvailableShedules(game, selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDay()));
        }

        return eventList;
    }

    public List<GameEvent> getEventsTest() {

        List<GameEvent> list = new ArrayList<>();
        return list;
    }

    public void addToWatchList(ActionEvent actionEvent) {
        //TODO
    }
}
