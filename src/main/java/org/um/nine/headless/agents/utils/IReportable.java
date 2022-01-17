package org.um.nine.headless.agents.utils;

import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.InfectionCard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.um.nine.headless.game.Settings.DEFAULT_LOGGER;
import static org.um.nine.headless.game.Settings.DEFAULT_PLAYERS;

public interface IReportable {
    String[] REPORT_PATH = new String[]{"src/main/resources/report"};

    static String getDescription() {
        String[] path = REPORT_PATH[0].split("/");
        StringBuilder desc = new StringBuilder();
        for (int k = 4; k < path.length; k++)
            desc.append(path[k]).append(" ");
        return desc.toString().trim().replace("-", " ").replace(".txt", " ");
    }

    default void setPath(String path) {
        REPORT_PATH[0] = path;
        File file = new File(REPORT_PATH[0]);
        if (!REPORT_PATH[0].endsWith(".txt")) file.mkdirs();
    }

    default String getPath() {
        return REPORT_PATH[0];
    }

    default Logger getLogger() {
        return DEFAULT_LOGGER;
    }

    default void append(String log) {
        this.getLogger().getLog().add(log);
    }

    default void log() {
        this.getLogger().getLog().forEach(System.out::println);
    }

    default void clear() {
        this.getLogger().clear();
    }

    default void report() {
        Future<Void> future = CompletableFuture.runAsync(() -> {
            try {
                FileWriter fw = new FileWriter(getPath());
                for (String s : this.getLogger().getLog()) fw.append(s).append("\n");
                fw.close();
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
            this.clear();
        });
        try {
            future.get(10L, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    default List<String> logPlayers(IState state) {
        List<String> log = new ArrayList<>();
        for (Player p : DEFAULT_PLAYERS) {
            log.addAll(this.logPlayer(
                    p, p.equals(state.getPlayerRepository().getCurrentPlayer())
            ));
        }
        return log;
    }

    default List<String> logPlayer(Player p, boolean currentPlayer) {
        List<String> log = new ArrayList<>();
        log.add((currentPlayer ? "Current Player " : "Player ") + p);
        log.add("Role : " + p.getRole().getName() + " , Location : " + p.getCity().getName());
        log.add("Cards in hand : " + p.getHand().stream().map(c -> ((CityCard) c).getCity().getName() + " " + ((CityCard) c).getCity().getColor()).collect(Collectors.toList()));
        return log;
    }

    default List<String> logDiseasesByCityAndColor(IState state) {
        List<String> s = new ArrayList<>();
        s.add("Diseases : ");
        state.getDiseaseRepository().getCubes().forEach((key, value) -> {
            if (value.isEmpty()) return;
            Map<City, List<Disease>> grouped = value.stream().filter(disease -> disease.getCity() != null).collect(Collectors.groupingBy(Disease::getCity));
            if (!grouped.isEmpty()) {
                s.addAll(
                        grouped.entrySet().
                                stream().
                                map(kv -> kv.getKey().getName() + " (" + kv.getValue().size() + " " + key + ")").
                                collect(Collectors.toList())
                );
            }
        });
        return s;
    }

    default List<String> logDecks(IState state) {
        List<String> s = new ArrayList<>();
        Function<Card, String> toString = card -> {
            return card instanceof CityCard cc ? cc.getName() + " " + cc.getCity().getColor().getName() :
                    card instanceof InfectionCard ic ? ic.getCity().getName() + " " + ic.getCity().getColor() :
                            card.getName();
        };
        s.add("Player cards deck : " + state.getCardRepository().getPlayerDeck().stream().map(toString).collect(Collectors.toList()));
        s.add("Infection cards deck : " + state.getCardRepository().getInfectionDeck().stream().map(toString).collect(Collectors.toList()));
        s.add("Infection cards discard pile : " + state.getCardRepository().getInfectionDiscardPile().stream().map(toString).collect(Collectors.toList()));
        return s;
    }

    default void reportInitialState(IState state, String path) {
        String gamePath = getPath();
        this.setPath(gamePath + path);
        if (this instanceof ExperimentalGame game) this.append("Game : " + game.getId() + " - Initial State");
        this.append("Player order : " + DEFAULT_PLAYERS);
        this.logPlayers(state).forEach(this::append);
        this.append(this.logDiseasesByCityAndColor(state).toString());
        var outbreaks = state.getDiseaseRepository().getOutbreakMarkers().stream().filter(Marker::isCurrent).findAny().orElse(null);
        this.append("Outbreaks count : " + (outbreaks == null ? "NULL" : outbreaks.getId()));
        this.logDecks(state).forEach(this::append);
        this.report();
        this.setPath(gamePath);
        this.clear();
    }

    default void logGenome(MacroAction[] genome, String appendix) {
        String prevPath = getPath();
        this.setPath(prevPath + appendix);
        IntStream.range(0, genome.length).
                mapToObj(i -> "Round " + i + " : " + genome[i]).
                forEach(this::append);
        this.report();
        this.clear();
        this.setPath(prevPath);
    }

}
