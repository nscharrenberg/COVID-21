package org.um.nine.contracts.repositories;

import com.jme3.scene.Geometry;
import org.um.nine.domain.ActionType;
import org.um.nine.domain.City;
import org.um.nine.domain.Difficulty;
import org.um.nine.domain.InfectionRateMarker;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.screens.dialogs.DispatcherDialog;
import org.um.nine.screens.dialogs.GovernmentGrantDialog;
import org.um.nine.screens.dialogs.PrognosisEventDialog;
import org.um.nine.screens.dialogs.ResilientPopulationDialog;

import java.util.List;

public interface IBoardRepository {
    void preload();

    void startGame();
    Geometry getBoard();
    City getSelectedCity();
    void setSelectedCity(City city);

    List<RoleAction> getUsedActions();

    void setUsedActions(List<RoleAction> usedActions);

    RoleAction getSelectedRoleAction();

    void setSelectedRoleAction(RoleAction selectedRoleAction);

    Difficulty getDifficulty();

    void setDifficulty(Difficulty difficulty);

    InfectionRateMarker getInfectionRateMarker();

    ActionType getSelectedPlayerAction();

    void setSelectedPlayerAction(ActionType selectedPlayerAction);

    void resetRound();

    void cleanup();

    DispatcherDialog getDispatcherDialog();

    IGameRepository getGameRepository();

    ICardRepository getCardRepository();

    PrognosisEventDialog getPrognosisEventDialog();

    ResilientPopulationDialog getResilientPopulationDialog();

    GovernmentGrantDialog getGovernmentGrantDialog();
}
