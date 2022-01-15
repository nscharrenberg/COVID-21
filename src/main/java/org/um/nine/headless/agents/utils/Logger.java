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

    //todo decide whether or not to use the record
    public static record ActionLog(String action, City targetLocation, Player player) {
        @Override
        public String toString() {
            return player().getName() + "\t" +
                    action() + "\t" +
                    targetLocation().getName();
        }
    }


    public static class LogRecord {

        private String action;
        private City targetLocation;
        private Player player;

        public LogRecord(String action, City targetLocation, Player player){
            this.action = action;
            this.targetLocation = targetLocation;
            this.player = player;
        }

        @Override
        public String toString() {
            return player.getName() + "\t" +
                    action + "\t" +
                    targetLocation.getName();
        }

        public Player player(){
            return player;
        }

        public City targetLocation(){
            return targetLocation;
        }

        public String action(){
            return action;
        }
    }
}
