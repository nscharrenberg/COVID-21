package org.um.nine.headless.agents.utils;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Disease;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.um.nine.headless.game.Settings.DEFAULT_LOGGER;

public interface IReportable {
    String[] REPORT_PATH = new String[]{"src/main/resources/report"};

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

    default void logPlayer(Player p, boolean currentPlayer) {
        append((currentPlayer ? "Current Player " : "Player ") + p);
        append("Role : " + p.getRole().getName());
        append("Location :" + p.getCity().getName());
        append("Cards in hand : " + p.
                getHand().
                stream().
                map(c -> ((CityCard) c).
                        getCity().getName() +
                        " " + ((CityCard) c).
                        getCity().getColor()).
                collect(Collectors.toList()));
    }

    default void logDiseases(IState state) {
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
        append("Diseases : " + s);
    }
}
