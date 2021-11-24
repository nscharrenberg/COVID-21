package org.um.nine.headless.agents.utils;

import com.rits.cloning.Cloner;
import org.um.nine.headless.game.FactoryProvider;
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

    default IState getClonedState() {
        Cloner cloner = new Cloner();
        return new State(
                cloner.deepClone(FactoryProvider.getDiseaseRepository()),
                cloner.deepClone(FactoryProvider.getPlayerRepository()),
                cloner.deepClone(FactoryProvider.getCardRepository()),
                cloner.deepClone(FactoryProvider.getCityRepository()),
                cloner.deepClone(FactoryProvider.getEpidemicRepository()),
                cloner.deepClone(FactoryProvider.getBoardRepository())
        );
    }

}
