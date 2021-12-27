package org.um.nine.headless.agents.rhea.macro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.agents.state.IState;

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
            GameStateFactory.createInitialState();
            GameStateFactory.getInitialState().reset();

            GameStateFactory.getInitialState().getCityRepository().addResearchStation(
                    GameStateFactory.getInitialState().getCityRepository().getCities().get("Tokyo")
            );
            GameStateFactory.getInitialState().getCityRepository().addResearchStation(
                    GameStateFactory.getInitialState().getCityRepository().getCities().get("Cairo")
            );
            GameStateFactory.getInitialState().start();


        } catch (Exception e) {
            e.printStackTrace();
        }
        this.state = GameStateFactory.getInitialState();
    }



}