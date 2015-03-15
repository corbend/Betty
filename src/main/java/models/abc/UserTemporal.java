package main.java.models.abc;

import main.java.models.users.User;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class UserTemporal {

    @Id
    @GeneratedValue
    private Long id;
    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    @JoinColumn(name="user_id")
    private User user;
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Temporal(value= TemporalType.DATE)
    private Date acquiredDate;
    @Temporal(value=TemporalType.DATE)
    private Date resolvedDate;

}
