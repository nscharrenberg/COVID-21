package org.um.nine.headless.game;

import com.rits.cloning.Cloner;
import org.um.nine.headless.agents.rhea.core.Mutator;
import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.state.StateHeuristic;
import org.um.nine.headless.agents.utils.Logger;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.Role;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.roles.OperationsExpert;
import org.um.nine.headless.game.domain.roles.Researcher;
import org.um.nine.headless.game.domain.roles.Scientist;

import java.util.*;

import static org.um.nine.headless.agents.rhea.state.StateEvaluation.Fcm;
import static org.um.nine.headless.agents.rhea.state.StateEvaluation.FoA;
import static org.um.nine.headless.agents.rhea.state.StateHeuristic.p;

public class Settings {
    public static final String APP_TITLE = "COVID-21 The Game";
    public static final int RESEARCH_STATION_THRESHOLD = 6;
    public static final int PLAYER_THRESHOLD = 4;
    public static final int OUTBREAK_THRESHOLD = 3;
    public static final int MAX_OUTBREAKS = 8;
    public static final int HAND_LIMIT = 7;
    public static final int ROLLING_HORIZON = 5;
    public static final List<String> RS = List.of("Atlanta");
    public static final String START_CITY = RS.get(0);
    public static final boolean HEADLESS = true;
    public static final boolean LOG = true;
    public static final boolean DEFAULT_INITIAL_STATE = true;
    public static final List<Player> DEFAULT_PLAYERS = new ArrayList<>();
    public static int ROUND_INDEX = 0;


    private static final StateHeuristic f = s -> (FoA.evaluateState(s) + Fcm.evaluateState(s)) / 2;
    public static final StateHeuristic BEST_HEURISTIC = state -> p(f, state);


    public static final Map<String, ? extends Role> DEFAULT_ROLES = Map.of(
            "Bot 1", new OperationsExpert(),
            "Bot 2", new Medic(),
            "Bot 3", new Researcher(),
            "Bot 4", new Scientist()
    );

    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.EASY;
    public static final Random RANDOM_PROVIDER = new Random(1 << 7 << 1999);
    public static final Cloner DEFAULT_CLONER = new Cloner();

    static {
        if (!HEADLESS && DEFAULT_INITIAL_STATE)
            throw new IllegalStateException();

        //DEFAULT_CLONER.setDontCloneInstanceOf(Stack.class);
        DEFAULT_CLONER.registerImmutable(Record.class, Enum.class, Stack.class);
    }


    public static final Logger DEFAULT_LOGGER = new Logger();
    public static ExperimentalGame DEFAULT_RUNNING_GAME;
    public static MacroActionsExecutor DEFAULT_MACRO_ACTIONS_EXECUTOR;

    public static Mutator DEFAULT_MUTATOR;
}
