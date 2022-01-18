package org.um.nine.headless.agents.rhea.state;

import org.um.nine.headless.game.contracts.repositories.IAnalyticsRepository;
import org.um.nine.headless.game.repositories.AnalyticsRepository;

public class GameStateFactory {
    private static IState initialState;
    private static final IAnalyticsRepository analyticsRepository = new AnalyticsRepository();

    private GameStateFactory() {

    }
    public static IState createInitialState() {
        initialState = new State();
        initialState.reset();
        initialState.start();
        return initialState;
    }
    public static IState getInitialState() {
        return initialState == null ? createInitialState() : initialState;
    }

    public static IAnalyticsRepository getAnalyticsRepository() {
        return analyticsRepository;
    }
}
