package org.um.nine.headless.agents.rhea.macro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.domain.Difficulty;

class MacroActionFactoryTest {

    private IState state;

    @Test
    @DisplayName("getMacroActions")
    void getMacroActions() {
        var macroActions = MacroActionFactory.init(this.state).getActions();

        System.out.println("Current city : "+this.state.getPlayerRepository().getCurrentPlayer().getCity().getName());

        for (MacroAction ma : macroActions){
            System.out.println(ma);
        }
    }

    @BeforeEach
    void setUp() {
        try {
            GameStateFactory.getInitialState().getPlayerRepository().createPlayer("Test1",false);
            GameStateFactory.getInitialState().getPlayerRepository().createPlayer("Test2",false);
            GameStateFactory.getInitialState().getBoardRepository().setDifficulty(Difficulty.EASY);
            GameStateFactory.getInitialState().getBoardRepository().start();
            GameStateFactory.getInitialState().getCityRepository().addResearchStation(
                    GameStateFactory.getInitialState().getCityRepository().getCities().get("Tokyo")
            );
            GameStateFactory.getInitialState().getCityRepository().addResearchStation(
                    GameStateFactory.getInitialState().getCityRepository().getCities().get("Cairo")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.state = GameStateFactory.getInitialState().getClonedState();
    }



}