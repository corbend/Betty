package main.java.models.users;

import javax.persistence.*;

@Entity
@Table(name="users_groups")
public class UserGroup {

    @Id
    @GeneratedValue
    private Long id;

    private String groupname;
    public String getName() {
        return groupname;
    }

    public void setName(String name) {
        this.groupname = name;
    }

    @Column(name="user_name")
    private String user_name;
    public String getUsername() {
        return user_name;
    }

    public void setUsername(String username) {
        this.user_name = username;
    }

}
