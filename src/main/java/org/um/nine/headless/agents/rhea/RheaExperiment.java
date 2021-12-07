package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionFactory;
import org.um.nine.headless.agents.utils.ExperimentalGame;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Disease;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RheaExperiment(ExperimentalGame game) {

    public void runExperiment() {
        while (game.onGoing()) {
            MacroAction nextMacro = MacroActionFactory.getFirstMacros(game.getCurrentState());
            game.getActionsHistory().add(nextMacro);
            executeMacro(game, nextMacro);
        }
    }

    public static void executeMacro(ExperimentalGame game, MacroAction nextMacro) {
        int actionsLeft;
        Collections.reverse(nextMacro.movingActions());
        Collections.reverse(nextMacro.standingActions());
        System.out.println("=============================================");
        System.out.println("Player " + game.getCurrentState().getPlayerRepository().getCurrentPlayer());
        actionsLeft = executeMovingActions(game, nextMacro);
        actionsLeft = executeStandingActions(actionsLeft, game, nextMacro);
        executeRestOfActions(actionsLeft, game);
        System.out.println("=============================================");
    }

    public static int executeMovingActions(ExperimentalGame game, MacroAction nextMacro) {
        int actions = 0;
        for (ActionType.MovingAction action : nextMacro.movingActions()) {
            System.out.println("[" + action.action() + "] -> " + action.toCity());
            game.getCurrentState().getBoardRepository().setSelectedCity(action.toCity());
            try {
                game.getCurrentState().getPlayerRepository().playerAction(action.action());
            } catch (Exception e) {
                e.printStackTrace();
            }
            actions++;
        }
        return actions;
    }

    public static int executeStandingActions(int actions, ExperimentalGame game, MacroAction nextMacro) {
        int treated = 0;
        for (ActionType.StandingAction action : nextMacro.standingActions()) {
            System.out.println("[" + action.action() + "] -> " + action.applyTo());
            game.getCurrentState().getBoardRepository().setSelectedCity(game.getCurrentState().getPlayerRepository().getCurrentPlayer().getCity());
            Object[] obj = null;
            if (action.action().equals(ActionType.TREAT_DISEASE)) {
                int n = nextMacro.standingActions().size() - treated;
                Map<Color, List<Disease>> grouped = game.getCurrentState().getPlayerRepository().getCurrentPlayer().getCity().getCubes().stream().collect(Collectors.groupingBy(Disease::getColor));
                var c = grouped.entrySet().stream().filter(list -> list.getValue().size() >= n).findFirst().orElse(null);
                if (c == null) continue;
                //FIXME: here we ignore this issue but this should not actually happen..
                // by exiting the loop we don't update actions so we will still have the
                // free action to spend later on
                obj = new Object[]{c.getKey()};
                treated++;
            }
            try {
                game.getCurrentState().getPlayerRepository().playerAction(action.action(), obj == null ? new Object[]{} : obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            actions++;
        }
        return actions;
    }

    public static void executeRestOfActions(int actions, ExperimentalGame game) {
        if (actions < 4) {
            MacroAction remaining = MacroActionFactory.findSuitableMacro(4 - actions);
            int nextActions = executeMovingActions(game, remaining);
            nextActions = executeStandingActions(nextActions, game, remaining);
            while (actions + nextActions != 4) {
                try {
                    game.getCurrentState().getPlayerRepository().playerAction(ActionType.SKIP_ACTION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                nextActions++;
            }
        }
        try {
            game.getCurrentState().getPlayerRepository().playerAction(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
