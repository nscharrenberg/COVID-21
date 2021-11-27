package org.um.nine.headless.agents.utils.MacroActions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.State;
import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.domain.Difficulty;

class MacroActionFactoryTest {

    private IState state;

    @Test
    @DisplayName("getMacroActions")
    void getMacroActions() {
        MacroActionFactory.init(this.state);
        var macroActions = MacroActionFactory.buildMacroActions(this.state, MacroActionFactory.MacroType.Treat3);
        System.out.println("Current city : "+this.state.getPlayerRepository().getCurrentPlayer().getCity().getName());
        for (MacroAction ma : macroActions){
            System.out.println(ma);
        }
    }

    @BeforeEach
    void setUp() {
        try {
            FactoryProvider.getPlayerRepository().createPlayer("Test1",false);
            FactoryProvider.getPlayerRepository().createPlayer("Test2",false);
            FactoryProvider.getBoardRepository().setDifficulty(Difficulty.EASY);
            FactoryProvider.getBoardRepository().start();
            FactoryProvider.getCityRepository().addResearchStation(
                    FactoryProvider.getCityRepository().getCities().get("Tokyo")
            );
            FactoryProvider.getCityRepository().addResearchStation(
                    FactoryProvider.getCityRepository().getCities().get("Cairo")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.state = new State().getClonedState();
    }



}