package main.java.controllers.bets;

import main.java.managers.bets.BetManager;
import main.java.models.bets.BetType;
import main.java.models.bets.CustomBet;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@RequestScoped
public class CustomBetViewCtrl {

    private static Logger log = Logger.getLogger(CustomBetViewCtrl.class.getName());

    private CustomBet selectedBet;
    public CustomBet getSelectedBet() {
        return selectedBet;
    }

    public void setSelectedBet(CustomBet selectedBet) {
        this.selectedBet = selectedBet;
    }

    private List<SelectItemGroup> betTypesStandartOptions;
    public List<SelectItemGroup> getBetTypesStandartOptions() {
        return betTypesStandartOptions;
    }

    public void setBetTypesStandartOptions(List<SelectItemGroup> betTypeStandartOptions) {
        this.betTypesStandartOptions = betTypeStandartOptions;
    }

    private CustomBet newCustomBet = new CustomBet();
    public CustomBet getNewCustomBet() {
        return newCustomBet;
    }

    public void setNewCustomBet(CustomBet bet) {
        newCustomBet = bet;
    }

    private void getBetTypeForSelect() {
        //инициализация опций выбора типов ставок из перечисления BetType
        List<SelectItemGroup> itemGroups = new ArrayList<>();
        Map<String, List<BetType>> stacks = BetType.getStacks();

        for (String keyName : stacks.keySet()) {
            SelectItemGroup itemGroup = new SelectItemGroup(keyName);

            int i = stacks.get(keyName).size() - 1;
            SelectItem[] selectItems = new SelectItem[i + 1];

            for (BetType betType: stacks.get(keyName)) {
                selectItems[i] = new SelectItem(betType, betType.toString());
                i--;
            }

            itemGroup.setSelectItems(selectItems);
            itemGroups.add(itemGroup);

            betTypesStandartOptions = itemGroups;
        }
    }

    @PostConstruct
    public void init() {
        getBetTypeForSelect();
    }

    @EJB
    private BetManager betManager;

    public void createNewBet(ActionEvent actionEvent) {
        log.log(Level.INFO, "SAVE NEW BET->" + newCustomBet.toString());
        betManager.saveCustomBet(newCustomBet);
    }

    public String createNewBet() {
        log.log(Level.INFO, "SAVE NEW BET->" + newCustomBet.toString());
        betManager.saveCustomBet(newCustomBet);
        return "betManagement.xhtml";
    }
}
