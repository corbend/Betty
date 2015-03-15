package main.java.billing.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="persons")
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String externalId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private Long documentNumber;
    private Long documentSerial;

    @JoinColumn(name="account_id")
    private Account account;
    public Account getAccountId() {
        return account;
    }

    public void setAccountId(Account account) {
        this.account = account;
    }


    @Temporal(value=TemporalType.DATE)
    private Date birthdate;
    private String password;

    public Person() {
        firstName = "Name";
        middleName = "Middle Name";
        lastName = "Last Name";
    }

    public Long getId() {
        return id;
    }
    public void setId(Long idx) {
        id = idx;
    }

    @Column(name="external_id")
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String value) {
        externalId = value;
    }

    @Column(name="first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String value) {
        firstName = value;
    }

    @Column(name="middle_name")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String value) {
        middleName = value;
    }

    @Column(name="last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        lastName = value;
    }

    @Column(name="nickname")
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String value) {
        nickname = value;
    }

    @Column(name="password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        password = value;
    }

    @Column(name="document_serial")
    public Long getDocumentSerial() {
        return documentSerial;
    }

    public void setDocumentSerial(Long value) {
        documentSerial = value;
    }

    @Column(name="document_number")
    public Long getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Long value) {
        documentNumber = value;
    }

    @Column(name="birthdate")
    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date value) {
        birthdate = value;
    }

}