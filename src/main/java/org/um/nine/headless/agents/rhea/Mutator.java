package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.macro.RPAMacroActionsFactory;
import org.um.nine.headless.agents.utils.ExperimentalGame;

import static org.um.nine.headless.game.Settings.RANDOM_PROVIDER;
import static org.um.nine.headless.game.Settings.ROLLING_HORIZON;

public record Mutator(ExperimentalGame game) {

    private static final double INITIAL_MUTATION_RATE = 1d, FINAL_MUTATION_RATE = 0.5;
    private static final int N_EVALUATION_SIMULATIONS = 5;
    private static final int N_MUTATIONS = 100;

    private static MacroActionsExecutor macroExecutor;


    public void mutateIndividual(Individual individual) {

        int mutatedGene = RANDOM_PROVIDER.nextInt(ROLLING_HORIZON);

        for (int i = 0; i < ROLLING_HORIZON; i++) {
            MacroAction macroIndex = individual.genome()[i];

            if (i < mutatedGene) {
                executor().executeIndexedMacro(game().getCurrentState(), macroIndex);
            } else if (i == mutatedGene) {
                individual.genome()[i] = (macroIndex = HPAMacroActionsFactory.getNextMacroAction(individual, game().getCurrentState()));
                executor().executeIndexedMacro(game().getCurrentState(), macroIndex);
            } else {
                individual.genome()[i] = (macroIndex = RPAMacroActionsFactory.getNextMacroAction(individual, game().getCurrentState()));
                executor().executeIndexedMacro(game().getCurrentState(), macroIndex);
            }
        }

    }

    private MacroActionsExecutor executor() {
        return macroExecutor == null ? (macroExecutor = new MacroActionsExecutor(game())) : macroExecutor;
    }


}
