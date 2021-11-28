package org.um.nine.headless.game;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.State;
import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.repositories.*;

public class FactoryProvider {
    private static final IState initialState = new State(
            new DiseaseRepository(),
            new PlayerRepository(),
            new CardRepository(),
            new CityRepository(),
            new EpidemicRepository(),
            new BoardRepository()
    );
    static {
        initialState.getDiseaseRepository().setState(initialState);
        initialState.getPlayerRepository().setState(initialState);
        initialState.getCardRepository().setState(initialState);
        initialState.getCityRepository().setState(initialState);
        initialState.getEpidemicRepository().setState(initialState);
        initialState.getBoardRepository().setState(initialState);
    }
    private FactoryProvider() {}
    public static IState getInitialState() {return initialState;}
    public static IDiseaseRepository getDiseaseRepository() {
        return getInitialState().getDiseaseRepository();
    }
    public static IBoardRepository getBoardRepository() {
        return getInitialState().getBoardRepository();
    }
    public static ICityRepository getCityRepository() {
        return getInitialState().getCityRepository();
    }
    public static ICardRepository getCardRepository() {
        return getInitialState().getCardRepository();
    }
    public static IEpidemicRepository getEpidemicRepository() {
        return getInitialState().getEpidemicRepository();
    }
    public static IPlayerRepository getPlayerRepository() {
        return getInitialState().getPlayerRepository();
    }
}
