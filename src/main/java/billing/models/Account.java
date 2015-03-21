package main.java.billing.models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="accounts")
@NamedQueries({
        @NamedQuery(name="FIND_ALL", query="SELECT a FROM Account a"),
        @NamedQuery(name="Account.getService", query="SELECT a FROM Account a WHERE a.serviceType='service'"),
        @NamedQuery(name="Account.getDefault", query="SELECT a FROM Account a WHERE a.user=:user LIMIT 1")
})
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="service_type")
    private String serviceType;
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @OneToOne(mappedBy="account", cascade = CascadeType.PERSIST)
    //TODO - добавить при следующей миграции базы
    @JoinColumn(name="person_id")
    private Person person;

    @Column(name="amount")
    private Double totalAmount = 0.0;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name="created_date")
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @OneToMany(mappedBy="destAccount", cascade = CascadeType.PERSIST)
    @JoinColumn(name="dest_account_id")
    private List<Transaction> inTransactions;

    @OneToMany(mappedBy="srcAccount", cascade = CascadeType.PERSIST)
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

    private boolean locked = false;

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double amount) {
        this.totalAmount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

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

    //transients
    @Transient
    private String statusString;
    public String getStatusString() {
        if ((statusString == null || statusString.equals("")) && !locked) {
            statusString = "active";
        }
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }
}
