package main.java.models.games;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="game_shedules")
public class GameShedule {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="game_id")
    private Game game;

    private String eventName;
    private String eventLocation;
    private String eventTime;
    private String team1Name;
    private String team2Name;

    @Temporal(value=TemporalType.DATE)
    private Date dateStart;

    @Temporal(value=TemporalType.DATE)
    private Date dateEnd;

    public Game getGame() {
        return game;
    }

    public void setGame(Game v) {
        game = v;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName (String v) {
        eventName = v;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation (String v) {
        eventLocation = v;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime (String v) {
        eventTime = v;
    }

    public String getTeam1Name () {
        return team1Name;
    }

    public void setTeam1Name (String v) {
        team1Name = v;
    }

    public String getTeam2Name () {
        return team2Name;
    }

    public void setTeam2Name (String v) {
        team2Name = v;
    }

    public Date getDateStart () {
        return dateStart;
    }

    public void setDateStart (Date v) {
        dateStart = v;
    }

    public Date getDateEnd () {
        return dateEnd;
    }

    public void setDateEnd (Date v) {
        dateEnd = v;
    }
}
