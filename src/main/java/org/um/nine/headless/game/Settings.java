package org.um.nine.headless.game;

import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.Role;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.roles.OperationsExpert;
import org.um.nine.headless.game.domain.roles.Researcher;
import org.um.nine.headless.game.domain.roles.Scientist;

import java.util.List;
import java.util.Map;
import java.util.Random;

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
    public static final boolean DEFAULT_INITIAL_STATE = true;
    public static final int DEFAULT_PLAYERS = 4;
    public static final Map<String, ? extends Role> DEFAULT_ROLES = Map.of(
            "Bot 1",new OperationsExpert(),
            "Bot 2", new Medic(),
            "Bot 3", new Researcher(),
            "Bot 4", new Scientist()
    );
    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.EASY;
    public static final Random RANDOM_PROVIDER = new Random();
    static {
        if (!HEADLESS && DEFAULT_INITIAL_STATE)
            throw new IllegalStateException();
    }
}
