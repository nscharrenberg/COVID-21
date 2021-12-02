package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.agents.utils.Report;
import org.um.nine.headless.game.Game;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Disease;
import org.um.nine.headless.game.domain.actions.ActionType;
import org.um.nine.headless.game.domain.actions.macro.MacroAction;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.um.nine.headless.game.domain.actions.ActionType.SKIP_ACTION;

public record RheaExperiment(List<Game> games, Report report) implements Experiment {

    public static void main(String[] args) {
        List<Game> games = List.of(new Game(true));
        new RheaExperiment(games, null).runExperiment();
    }

    public void runExperiment() {
        for (Game game : this.games()) {
            while (game.onGoing()) {
                MacroAction nextMacro = game.getCurrentState().getNextMacro();
                game.getActionsHistory().add(nextMacro);
                executeMacro(game,nextMacro);
            }
        }
    }

    public static void executeMacro(Game game, MacroAction nextMacro) {
        int actionsLeft;
        Collections.reverse(nextMacro.movingActions());
        Collections.reverse(nextMacro.standingActions());
        System.out.println("=============================================");
        System.out.println("Player " + game.getCurrentState().getPlayerRepository().getCurrentPlayer());
        actionsLeft = executeMovingActions(game, nextMacro);
        actionsLeft = executeStandingActions(actionsLeft, game, nextMacro);
        executeRestOfActions(actionsLeft,game);
        System.out.println("=============================================");
    }

    public static int executeMovingActions(Game game, MacroAction nextMacro) {
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
    public static int executeStandingActions(int actions, Game game, MacroAction nextMacro) {
        int treated = 0;
        for (ActionType.StandingAction action : nextMacro.standingActions()) {
            System.out.println("[" + action.action() + "] -> " + action.applyTo());
            game.getCurrentState().getBoardRepository().setSelectedCity(game.getCurrentState().getPlayerRepository().getCurrentPlayer().getCity());
            Object[] obj = null;
            if (action.action().equals(ActionType.TREAT_DISEASE)) {
                int n = nextMacro.standingActions().size() - treated;
                Map<Color, List<Disease>> grouped = game.getCurrentState().getPlayerRepository().getCurrentPlayer().getCity().getCubes().stream().collect(Collectors.groupingBy(Disease::getColor));
                var c = grouped.entrySet().stream().filter(list -> list.getValue().size() >= n).findFirst().orElse(null);
                obj = new Object[]{Objects.requireNonNull(c).getKey()};
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
    public static void executeRestOfActions(int actions, Game game) {
        while (actions < 4) {
            try {
                game.getCurrentState().getPlayerRepository().playerAction(SKIP_ACTION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("[" + SKIP_ACTION + "] -> " + game.getCurrentState().getBoardRepository().getSelectedCity());
            actions++;
        }
        try {
            game.getCurrentState().getPlayerRepository().playerAction(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
