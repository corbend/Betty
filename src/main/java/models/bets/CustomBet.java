package main.java.models.bets;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@NamedQueries({
        @NamedQuery(name="CustomBet.findAll", query="SELECT b FROM CustomBet b")
})
public class CustomBet {

    @Id
    @GeneratedValue
    private Long id;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String name;
    private String description;
    private Double coefficient;

    private String expression;
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Transient
    private BetType type;
    public BetType getType() {
        return type;
    }

    public void setType(BetType type) {
        this.type = type;
    }

    @Transient
    private List<BetType> betTypeStack;
    public List<BetType> getBetTypeStack() {
        return betTypeStack;
    }

    public void setBetTypeStack(List<BetType> betTypeStack) {
        this.betTypeStack = betTypeStack;
    }

    private String betTypeString;
    public String getBetTypeString() {
        return betTypeString;
    }

    public void setBetTypeString(String betTypeString) {
        this.betTypeString = betTypeString;
    }

    @JoinTable(name="custombet_bet_groups",
            joinColumns={@JoinColumn(name="bets_id", referencedColumnName = "id")},
            inverseJoinColumns={@JoinColumn(name="betgroups_id", referencedColumnName = "id")})
    @ManyToMany
    private List<BetGroup> betGroups;
    public List<BetGroup> getBetGroups() {
        return betGroups;
    }

    public void setBetGroups(List<BetGroup> betGroup) {
        this.betGroups = betGroup;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

}
