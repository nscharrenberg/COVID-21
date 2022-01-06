package org.um.nine.headless.agents.utils;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.Card;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Disease;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.InfectionCard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.um.nine.headless.game.Settings.DEFAULT_REPORTER;
import static org.um.nine.headless.game.Settings.LOG;

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

    public static void clearLog() {
        DEFAULT_REPORTER.clear();
    }


    public static void record(String string) {
        DEFAULT_REPORTER.getLog().add(string);
    }

    public static void logState(IState state) {
        for (Player p : state.getPlayerRepository().getPlayers().values()) {
            logPlayer(p, p.equals(state.getPlayerRepository().getCurrentPlayer()));
        }

        logDiseases(state);
        logDecks(state);

    }

    private static void logDecks(IState state) {
        record("Player Cards Deck : \n" + state.getCardRepository().getPlayerDeck().stream().map(Card::getName).collect(Collectors.toList()));
        record("Infection Cards Deck : \n" + state.getCardRepository().getInfectionDeck().stream().map(InfectionCard::getName).collect(Collectors.toList()));
    }

    public static void logPlayer(Player p, boolean currentPlayer) {
        if (!LOG) return;

        record((currentPlayer ? "Current Player " : "Player ") + p);
        record("Location " + p.getCity().getName());
        record("Cards in hand: " + p.getHand().stream().
                map(c -> ((CityCard) c).getCity().getName() +
                        " " + ((CityCard) c).getCity().getColor()).
                collect(Collectors.toList()));
    }

    public static void logDiseases(IState state) {
        if (!LOG) return;
        List<String> s = new ArrayList<>();
        for (City c : state.getCityRepository().getCities().values()) {
            Map<String, List<Disease>> grouped = c.getCubes().stream().collect(
                    Collectors.groupingBy(att -> att.getColor().getName())
            );
            if (!grouped.isEmpty())
                s.addAll(grouped.entrySet().
                        stream().
                        map(kv -> c.getName() + " (" +
                                kv.getValue().size() + " " +
                                kv.getKey() + ")").
                        collect(Collectors.toList()));
        }
        record("Diseases : " + s);
    }


    public static void log() {
        DEFAULT_REPORTER.log(false);
    }

    protected void log(boolean clear) {
        this.getLog().forEach(System.out::println);
        if (clear) this.clear();
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
