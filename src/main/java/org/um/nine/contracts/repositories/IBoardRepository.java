package org.um.nine.contracts.repositories;

import com.jme3.scene.Geometry;
import org.um.nine.domain.City;

public interface IBoardRepository {
    void startGame();
    Geometry getBoard();
    City getSelectedCity();
    void setSelectedCity(City city);
}
