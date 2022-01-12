package org.um.nine.headless.agents.rhea.state;

import org.um.nine.headless.game.repositories.*;

public class GameStateFactory {
    private static IState initialState;
    private GameStateFactory() {

    }
    public static IState createInitialState() {
        initialState = new State(
                new DiseaseRepository(),
                new PlayerRepository(),
                new CardRepository(),
                new CityRepository(),
                new EpidemicRepository(),
                new BoardRepository()
        );
        initialState.reset();
        initialState.start();

        return initialState;
    }
    public static IState getInitialState() {
        return initialState == null ? createInitialState() : initialState;
    }

}
