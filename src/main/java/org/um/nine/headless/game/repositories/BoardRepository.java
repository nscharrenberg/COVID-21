package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.Info;
import org.um.nine.headless.game.contracts.repositories.IBoardRepository;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.roles.RoleAction;

import java.util.ArrayList;
import java.util.List;

public class BoardRepository implements IBoardRepository {
    private City selectedCity;
    private RoleAction selectedRoleAction;
    private ActionType selectedPlayerAction;
    private List<RoleAction> usedActions = new ArrayList<>();
    private Difficulty difficulty;

    @Override
    public void start() {
        resetRound();
        FactoryProvider.getCityRepository().reset();

        City atlanta = FactoryProvider.getCityRepository().getCities().get(Info.START_CITY);
        FactoryProvider.getPlayerRepository().getPlayers().forEach((k, p) -> {
            FactoryProvider.getPlayerRepository().assignRoleToPlayer(p);

            atlanta.addPawn(p);
        });

        FactoryProvider.getCardRepository().buildDecks();
        FactoryProvider.getPlayerRepository().decidePlayerOrder();
        FactoryProvider.getPlayerRepository().nextPlayer();
    }

    @Override
    public City getSelectedCity() {
        return selectedCity;
    }

    @Override
    public void setSelectedCity(City selectedCity) {
        this.selectedCity = selectedCity;
    }

    @Override
    public RoleAction getSelectedRoleAction() {
        return selectedRoleAction;
    }

    @Override
    public void setSelectedRoleAction(RoleAction selectedRoleAction) {
        this.selectedRoleAction = selectedRoleAction;
    }

    @Override
    public ActionType getSelectedPlayerAction() {
        return selectedPlayerAction;
    }

    @Override
    public void setSelectedPlayerAction(ActionType selectedPlayerAction) {
        this.selectedPlayerAction = selectedPlayerAction;
    }

    @Override
    public List<RoleAction> getUsedActions() {
        return usedActions;
    }

    @Override
    public void setUsedActions(List<RoleAction> usedActions) {
        this.usedActions = usedActions;
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void resetRound() {
        selectedCity = null;
        selectedPlayerAction = null;
        selectedRoleAction = null;
        usedActions = new ArrayList<>();

        FactoryProvider.getPlayerRepository().resetRound();
    }
}
