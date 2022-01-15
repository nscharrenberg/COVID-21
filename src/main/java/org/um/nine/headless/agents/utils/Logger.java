package org.um.nine.headless.agents.utils;

import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.LinkedList;

public class Logger {
    private final LinkedList<String> log;

    public Logger() {
        log = new LinkedList<>();
    }

    public void addStep(String action, City targetLocation, Player player) {
        log.add(new ActionLog(action, targetLocation, player).toString());
    }
    protected void clear() {
        this.getLog().clear();
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        log.forEach(state -> info.append(state).append("\n"));
        return info.toString();
    }

    public LinkedList<String> getLog() {
        return log;
    }

    public static record ActionLog(String action, City targetLocation, Player player) {
        @Override
        public String toString() {
            return player().getName() + "\t" +
                    action() + "\t" +
                    targetLocation().getName();
        }
    }
}
