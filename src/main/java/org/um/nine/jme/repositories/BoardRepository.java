package org.um.nine.jme.repositories;

import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.roles.RoleAction;

import java.util.List;

public class BoardRepository {

    public void preload() {
        GameStateFactory.getInitialState().getBoardRepository().preload();
    }

    /**
     * Starts the Game Logic by loading in all the necessary data such as:
     * Loading cities, Assigning Roles, Distributing Cards, Infecting Cities,
     * deciding first player
     */

    public void start() {
        GameStateFactory.getInitialState().getBoardRepository().start();
    }

    public City getSelectedCity() {
        return GameStateFactory.getInitialState().getBoardRepository().getSelectedCity();
    }

    public void setSelectedCity(City selectedCity) {
        GameStateFactory.getInitialState().getBoardRepository().setSelectedCity(selectedCity);
    }

    public RoleAction getSelectedRoleAction() {
        return GameStateFactory.getInitialState().getBoardRepository().getSelectedRoleAction();
    }

    public void setSelectedRoleAction(RoleAction selectedRoleAction) {
        GameStateFactory.getInitialState().getBoardRepository().setSelectedRoleAction(selectedRoleAction);
    }

    public ActionType getSelectedPlayerAction() {
        return GameStateFactory.getInitialState().getBoardRepository().getSelectedPlayerAction();
    }

    public void setSelectedPlayerAction(ActionType selectedPlayerAction) {
        GameStateFactory.getInitialState().getBoardRepository().setSelectedPlayerAction(selectedPlayerAction);
    }

    public List<RoleAction> getUsedActions() {
        return GameStateFactory.getInitialState().getBoardRepository().getUsedActions();
    }

    public void setUsedActions(List<RoleAction> usedActions) {
        GameStateFactory.getInitialState().getBoardRepository().setUsedActions(usedActions);
    }

    public Difficulty getDifficulty() {
        return GameStateFactory.getInitialState().getBoardRepository().getDifficulty();
    }

    public void setDifficulty(Difficulty difficulty) {
        GameStateFactory.getInitialState().getBoardRepository().setDifficulty(difficulty);
    }

    /**
     * Resets all the state information back to it's original state for this round
     */

    public void resetRound() {
        GameStateFactory.getInitialState().getBoardRepository().resetRound();
    }

    /**
     * Reset the whole game state
     */
    public void reset() {
        GameStateFactory.getInitialState().getBoardRepository().reset();
    }
}