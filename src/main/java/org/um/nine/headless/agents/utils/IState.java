package org.um.nine.headless.agents.utils;

import com.rits.cloning.Cloner;
import org.um.nine.headless.game.contracts.repositories.*;

public interface IState {
    IDiseaseRepository getDiseaseRepository();

    IPlayerRepository getPlayerRepository();

    IBoardRepository getBoardRepository();

    ICityRepository getCityRepository();

    ICardRepository getCardRepository();

    IEpidemicRepository getEpidemicRepository();

    boolean gameOver();

    boolean isVictory();

    default IState getClonedState(IState state) {
        Cloner cloner = new Cloner();
        return new State(
                cloner.deepClone(state.getDiseaseRepository()),
                cloner.deepClone(state.getPlayerRepository()),
                cloner.deepClone(state.getCardRepository()),
                cloner.deepClone(state.getCityRepository()),
                cloner.deepClone(state.getEpidemicRepository()),
                cloner.deepClone(state.getBoardRepository())
        );
    }

}
