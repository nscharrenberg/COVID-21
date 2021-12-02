package org.um.nine.headless.game;

import org.um.nine.headless.game.domain.state.IState;
import org.um.nine.headless.game.domain.state.State;
import org.um.nine.headless.game.repositories.*;

public class GameStateFactory {
    private static IState initialState;
    private GameStateFactory() {

    }
    public static IState createInitialState() {
        return initialState = new State(
                new DiseaseRepository(),
                new PlayerRepository(),
                new CardRepository(),
                new CityRepository(),
                new EpidemicRepository(),
                new BoardRepository()
        );
    }

    public static IState getInitialState() {
        return initialState == null ? createInitialState() : initialState;
    }

}
