package org.um.nine.headless.game;

import org.um.nine.headless.game.domain.Difficulty;

import java.util.List;

public class Settings {
    public static final String APP_TITLE = "COVID-21 The Game";
    public static final int RESEARCH_STATION_THRESHOLD = 6;
    public static final int PLAYER_THRESHOLD = 4;
    public static final int OUTBREAK_THRESHOLD = 3;
    public static final int MAX_OUTBREAKS = 8;
    public static final int HAND_LIMIT = 7;
    public static final int ROLLING_HORIZON = 5;
    public static final int BOT_PLAYERS = 2;
    public static final Difficulty DIFFICULTY = Difficulty.EASY;
    public static final List<String> RS = List.of("Atlanta","Cairo","Tokyo");
    public static final String START_CITY = RS.get(0);

}
