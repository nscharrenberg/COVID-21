package org.um.nine.contracts.repositories;

import com.jme3.scene.Geometry;

public interface IBoardRepository {
    void startGame();
    Geometry getBoard();
}
