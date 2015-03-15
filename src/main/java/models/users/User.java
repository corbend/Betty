package main.java.models.users;

import main.java.models.bets.UserBet;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="users")
@XmlRootElement
@NamedQuery(name = "User.findByLogin", query = "SELECT u FROM User u WHERE u.login = :login")
public class User implements Serializable {

    public User() {}

    public User(String login, String email, String telephone) {
        this.login = login;
        this.email = email;
        this.telephone = telephone;
    }

    public List<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
    }

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<UserGroup> groups;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user")
    private List<UserBet> userBets;

    private Long accountId;

    @Column
    @NotNull
    private String name;

    @Id
    @Column(name="user_name")
    @NotNull
    private String login;

    @Column(name="password_hash")
    @NotNull
    private String passwordHash;

    @Column(name="password_salt")
    @NotNull
    private String passwordSalt;

    @Transient
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    @Transient
    private String password2;

    @Column
    @NotNull
    @Size(min=10, max=12)
    private String telephone;

    @Column
    @NotNull
    @Size(min=10, max=100)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        name = v;
    }

    public List<UserBet> getUserBets() {
        return userBets;
    }

    public void setUserBets(List<UserBet> userBets) {
        this.userBets = userBets;
    }
}