package org.um.nine.headless.agents.utils;

import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.LinkedList;

public class Log {
    private final LinkedList<LogRecord> log;

    public Log() {
        log = new LinkedList<>();
    }

    public void addStep(String action, City targetLocation, Player player) {
        log.add(new LogRecord(action, targetLocation, player));
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        log.forEach(state -> info.append(state.toString()).append("\n"));
        return info.toString();
    }

    public LinkedList<LogRecord> getLog() {
        return log;
    }

    public static record LogRecord(String action, City targetLocation, Player player) {

        @Override
        public String toString() {
            return player().getName() + "\t" +
                    action() + "\t" +
                    targetLocation().getName();
        }

    }
}
