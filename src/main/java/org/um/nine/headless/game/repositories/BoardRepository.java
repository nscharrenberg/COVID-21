package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.roles.RoleAction;

import java.util.ArrayList;
import java.util.List;

public class BoardRepository {
    private City selectedCity;
    private RoleAction selectedRoleAction;
    private ActionType selectedPlayerAction;
    private List<RoleAction> usedActions = new ArrayList<>();
    private Difficulty difficulty;

    public void start() {
        resetRound();
        FactoryProvider.getCityRepository().reset();

        // TODO: Init Players
        // TODO: Build Decks
        // TODO: Decide player orders
        // TODO: Set first players turn
    }

    public City getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(City selectedCity) {
        this.selectedCity = selectedCity;
    }

    public RoleAction getSelectedRoleAction() {
        return selectedRoleAction;
    }

    public void setSelectedRoleAction(RoleAction selectedRoleAction) {
        this.selectedRoleAction = selectedRoleAction;
    }

    public ActionType getSelectedPlayerAction() {
        return selectedPlayerAction;
    }

    public void setSelectedPlayerAction(ActionType selectedPlayerAction) {
        this.selectedPlayerAction = selectedPlayerAction;
    }

    public List<RoleAction> getUsedActions() {
        return usedActions;
    }

    public void setUsedActions(List<RoleAction> usedActions) {
        this.usedActions = usedActions;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void resetRound() {
        selectedCity = null;
        selectedPlayerAction = null;
        selectedRoleAction = null;
        usedActions = new ArrayList<>();

        FactoryProvider.getPlayerRepository().resetRound();
    }
}
