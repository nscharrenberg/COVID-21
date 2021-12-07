package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.repositories.BoardRepository;

import java.util.List;

public interface IBoardRepository {
    BoardRepository setState(IState state);

    void preload();

    void start();

    City getSelectedCity();

    void setSelectedCity(City selectedCity);

    RoleAction getSelectedRoleAction();

    void setSelectedRoleAction(RoleAction selectedRoleAction);

    ActionType getSelectedPlayerAction();

    void setSelectedPlayerAction(ActionType selectedPlayerAction);

    List<RoleAction> getUsedActions();

    void setUsedActions(List<RoleAction> usedActions);

    Difficulty getDifficulty();

    void setDifficulty(Difficulty difficulty);

    void resetRound();
}
