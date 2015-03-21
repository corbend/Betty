package main.java.models.sys;

import main.java.models.games.Game;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@NamedQueries({
        @NamedQuery(name="ScheduleParser.findAll", query="SELECT b FROM ScheduleParser b")
})
public class ScheduleParser implements Serializable {

    public class Proxy {

        private Long id;
        private String name;
        private String url;
        private Boolean status;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }


        public Proxy(ScheduleParser p) {

            ScheduleParser pclone = p.clone();
            this.setId(pclone.getId());
            this.setName(pclone.getName());
            this.setUrl(pclone.getUrl());
            this.setStatus(pclone.getStatus());

        }

        @Override
        public String toString() {
            return getClass().getName() + "=>" + id + ":" + name + ":" + url + ":" + status;
        }
    }

    public ScheduleParser() {};

    public ScheduleParser(Long id, String name, String url, Boolean status, Game game) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.status = status;
        this.game = game;
    }

    @Override
    public ScheduleParser clone() {

        return new ScheduleParser(
                this.getId(),
                this.getName(),
                this.getUrl(),
                this.getStatus(),
                this.getGame()
        );
    }

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String url;
    private Boolean status = true;

    @JoinColumn(name="game_id")
    private Game game;

    public Proxy createProxy() {
        return new Proxy(this);
    }

    @Transient
    private Boolean complete;
    @Transient
    private Date lastCompleteTime;

    public DateTime getLastCompleteTime() {
        return new DateTime(lastCompleteTime);
    }

    public void setLastCompleteTime(Date lastCompleteTime) {
        this.lastCompleteTime = lastCompleteTime;
    }
    public void setLastCompleteTime(DateTime lastCompleteTime) {
        this.lastCompleteTime = lastCompleteTime.toDate();
    }

    public Boolean isComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {

        return getClass().getName() + "=>" + id + ":" + name + ":" + url + ":" + status;
    }

}
