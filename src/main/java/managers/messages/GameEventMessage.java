package main.java.managers.messages;

import main.java.models.games.GameEvent;

import java.io.Serializable;

public class GameEventMessage implements Serializable {

    private GameEvent event;
    private int score1;
    private int score2;

    public GameEvent getEvent() {
        return event;
    }

    public void setEvent(GameEvent event) {
        this.event = event;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public GameEventMessage(GameEvent gameEvent, int score1, int score2) {
        this.event = gameEvent;
        this.score1 = score1;
        this.score2 = score2;
    }

    @Override
    public String toString() {
        GameEvent event = getEvent();
        return event.getEventName() + "<" + event.getTeam1Name() + event.getTeam2Name() + ">=" + getScore1() + "," + getScore2();
    }
}
