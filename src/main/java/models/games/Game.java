package main.java.models.games;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="games")
@NamedQueries({
    //пример параметрического запроса
    @NamedQuery(name="Game.findByName", query="SELECT g FROM Game g WHERE g.name = :gameName"),
    //тупой запрос
    @NamedQuery(name="Game.findAll", query="SELECT g FROM Game g"),
    @NamedQuery(name="Game.findActive", query="SELECT g FROM Game g WHERE g.active = false")
})
public class Game implements Serializable {

    public enum TimeUnit {
        SECOND, DAY, MONTH, DECADE, HALF_YEAR;

        public static int getSeconds(TimeUnit value) {
            int am;
            switch (value) {
                case SECOND:
                    am = 1;
                    break;
                case DAY:
                    am = 24 * 60;
                    break;
                case MONTH:
                    am = 31 * 24 * 60;
                    break;
                case DECADE:
                    am = 4 * 31 * 24 * 60;
                    break;
                case HALF_YEAR:
                    am = 6 * 31 * 24 * 60;
                default:
                    am = 24 * 60;
            }

            return am;
        }
    }

    //модель игры
    @Id
    //@SequenceGenerator(name = "idSequence", sequenceName = "seq_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue//(strategy = GenerationType.SEQUENCE, generator = "idSequence")
    private Long id;
    private String name;
    private Integer numberOfPeriods;
    private Integer periodDuration;
    private Integer numberOfTeams;
    private Integer numberOfPlayersInTeam;

    @OneToMany
    @JoinColumn(name="game_id")
    private List<GameShedule> gameShedules;

    //за какой промежуток времени будет создаваться расписание
    private Long sheduleWindow;
    //единицы измерения
    private TimeUnit periodTimeUnit;

    //период обновления расписания
    private int sheduleEvery;

    private boolean active;

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

    @Column(name="shedule_window")
    public Long getSheduleWindow() {
        return sheduleWindow;
    }

    public void setSheduleWindow(Long v) {
        sheduleWindow = v;
    }

    @Column(name="period_time_unit")
    public TimeUnit getPeriodTimeUnit() {
        return periodTimeUnit;
    }

    public void setPeriodTimeUnit(TimeUnit v) {
        periodTimeUnit = v;
    }

    @Column(name="shedule_every")
    public int getSheduleEvery() {
        return sheduleEvery;
    }

    public void setSheduleEvery(int v) {
        sheduleEvery = v;
    }

    @Column(name="active")
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean v) {
        active = v;
    }

    public List<GameShedule> getGameShedules() {
        if (gameShedules == null) {
            gameShedules = new ArrayList();
        }
        return gameShedules;
    }

    public void setGameShedules(List<GameShedule> lst) {
        gameShedules = lst;
    }

}
