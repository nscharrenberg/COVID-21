package org.um.nine.headless.agents.rhea.macro;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.state.IState;

import static org.um.nine.headless.game.Settings.*;

class MacroActionFactoryTest {

    private IState state;

    @Test
    @DisplayName("getMacroActions")
    void getMacroActions() {
        var macroActions = HPAMacroActionsFactory.init(
                this.state,
                this.state.getPlayerRepository().getCurrentPlayer().getCity(),
                this.state.getPlayerRepository().getCurrentPlayer()
        ).getActions();
        System.out.println("Current city : " + this.state.getPlayerRepository().getCurrentPlayer().getCity().getName());
        for (MacroAction ma : macroActions) {
            System.out.println(ma);
        }

        for (MacroAction ma : macroActions) {
            if (ma.size() < 4) {
                ma = HPAMacroActionsFactory.init(
                        this.state,
                        this.state.getPlayerRepository().getCurrentPlayer().getCity(),
                        this.state.getPlayerRepository().getCurrentPlayer()
                ).fillMacroAction(ma);
            }
        }
        System.out.println("\n");
        for (MacroAction ma : macroActions) {
            System.out.println(ma);
        }
    }

    @BeforeEach
    void setUp() {
        try {
            Assertions.assertTrue(DEFAULT_INITIAL_STATE);
            Assertions.assertTrue(HEADLESS);

            DEFAULT_RUNNING_GAME = new ExperimentalGame();
            this.state = DEFAULT_RUNNING_GAME.getCurrentState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}