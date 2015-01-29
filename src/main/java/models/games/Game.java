package main.java.models.games;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="games")
@NamedQueries({
    //пример параметрического запроса
    @NamedQuery(name="Game.findByName", query="SELECT g FROM Game g WHERE g.name = :gameName"),
    //тупой запрос
    @NamedQuery(name="Game.findAll", query="SELECT g FROM Game g")
})
public class Game implements Serializable {

    //модель игры
    @Id
    @SequenceGenerator(name = "idSequence", sequenceName = "seq_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSequence")
    private Long id;
    private String name;
    private Integer numberOfPeriods;
    private Integer periodDuration;
    private Integer numberOfTeams;
    private Integer numberOfPlayersInTeam;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        name = v;
    }

    @Column(name="number_of_periods")
    public Integer getNumberOfPeriods() {
        return numberOfPeriods;
    }

    public void setNumberOfPeriods(Integer v) {
        numberOfPeriods = v;
    }

    @Column(name="period_duration")
    public Integer getPeriodDuration() {
        return periodDuration;
    }

    public void setPeriodDuration(Integer v) {
        periodDuration = v;
    }

    @Column(name="number_of_teams")
    public Integer getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(Integer v) {
        numberOfTeams = v;
    }

    @Column(name="number_of_players_in_team")
    public Integer getNumberOfPlayersInTeam() {
        return numberOfPlayersInTeam;
    }

    public void setNumberOfPlayersInTeam(Integer v) {
        numberOfPlayersInTeam = v;
    }

}
