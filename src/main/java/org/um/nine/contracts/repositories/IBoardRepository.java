package org.um.nine.contracts.repositories;

import com.jme3.scene.Geometry;
import org.um.nine.domain.City;
import org.um.nine.domain.Difficulty;
import org.um.nine.domain.InfectionRateMarker;
import org.um.nine.domain.roles.RoleAction;

import java.util.List;

public interface IBoardRepository {
    void preload();

    void startGame();
    Geometry getBoard();
    City getSelectedCity();
    void setSelectedCity(City city);

    List<RoleAction> getUsedActions();

    void setUsedActions(List<RoleAction> usedActions);

    RoleAction getSelectedAction();

    void setSelectedAction(RoleAction selectedAction);

    Difficulty getDifficulty();

    void setDifficulty(Difficulty difficulty);

    InfectionRateMarker getInfectionRateMarker();
}
