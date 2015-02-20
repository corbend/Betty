package main.java.billing.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="accounts")
public class Account {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Person person;

    @OneToMany(mappedBy="destAccount")
    @JoinColumn(name="dest_account_id")
    private List<Transaction> inTransactions;

    @OneToMany(mappedBy="srcAccount")
    @JoinColumn(name="src_account_id")
    private List<Transaction> outTransactions;

    public List<Transaction> getOutTransactions() {
        return outTransactions;
    }

    public void setOutTransactions(List<Transaction> transactions) {
        this.outTransactions = transactions;
    }

    public List<Transaction> getInTransactions() {
        return inTransactions;
    }

    public void setInTransactions(List<Transaction> inTransactions) {
        this.inTransactions = inTransactions;
    }

    private boolean locked;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private Double amount;
    private String currency = "RU";

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person v) {
        person = v;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean v) {
        locked = v;
    }

    public boolean isLocked() {
        return locked;
    }
}
