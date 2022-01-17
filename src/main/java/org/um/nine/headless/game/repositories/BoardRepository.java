package org.um.nine.headless.game.repositories;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.contracts.repositories.IBoardRepository;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.roles.RoleAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.um.nine.headless.game.Settings.DEFAULT_INITIAL_STATE;

public class BoardRepository implements IBoardRepository {
    private City selectedCity;
    private RoleAction selectedRoleAction;
    private ActionType selectedPlayerAction;
    private List<RoleAction> usedActions = new ArrayList<>();
    private Difficulty difficulty;

    @Override
    public BoardRepository clone() {
        try {
            BoardRepository clone = (BoardRepository) super.clone();
            clone.setSelectedCity(this.getSelectedCity());
            clone.setSelectedRoleAction(this.getSelectedRoleAction());
            clone.setSelectedPlayerAction(this.getSelectedPlayerAction());
            clone.setUsedActions(new ArrayList<>(List.copyOf(this.usedActions)));
            clone.setDifficulty(this.getDifficulty());
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void preload() {
        this.difficulty = this.difficulty == null ? Settings.DEFAULT_DIFFICULTY : this.difficulty;
    }

    /**
     * Starts the Game Logic by loading in all the necessary data such as:
     * Loading cities, Assigning Roles, Distributing Cards, Infecting Cities, deciding first player
     */
    @Override
    public void start(IState state) {
        this.difficulty = this.difficulty == null ? Settings.DEFAULT_DIFFICULTY : this.difficulty;
        City atlanta = state.getCityRepository().getCities().get(Settings.START_CITY);

        state.getPlayerRepository().getPlayers().forEach((k, p) -> {

            if (!DEFAULT_INITIAL_STATE)
                state.getPlayerRepository().assignRoleToPlayer(p);

            atlanta.addPawn(p);
        });

        state.getCardRepository().buildDecks(state);
        state.getPlayerRepository().decidePlayerOrder();
        state.getPlayerRepository().nextPlayer();
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

    /**
     * Resets all the state information back to it's original state for this round
     */
    @Override
    public void resetRound() {
        selectedCity = null;
        selectedPlayerAction = null;
        selectedRoleAction = null;
        usedActions = new ArrayList<>();

    }

    /**
     * Reset the whole game state
     */
    @Override
    public void reset() {
        resetRound();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardRepository that = (BoardRepository) o;

        return Objects.equals(selectedCity, that.selectedCity) &&
                selectedRoleAction == that.selectedRoleAction &&
                selectedPlayerAction == that.selectedPlayerAction &&
                Objects.equals(usedActions, that.usedActions) &&
                difficulty == that.difficulty;
    }

}
