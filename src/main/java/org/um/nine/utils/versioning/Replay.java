package org.um.nine.utils.versioning;

import org.um.nine.contracts.repositories.*;

import java.util.HashMap;

public class Replay {
    private int timestamp = 1;
    private String version;
    private HashMap<Integer, State> timeline;

    public Replay(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HashMap<Integer, State> getTimeline() {
        return timeline;
    }

    public void setTimeline(HashMap<Integer, State> timeline) {
        this.timeline = timeline;
    }

    public void stamp(IBoardRepository boardRepository, ICardRepository cardRepository, ICityRepository cityRepository, IDiseaseRepository diseaseRepository, IEpidemicRepository epidemicRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.addToTimeline(new State(boardRepository, cardRepository, cityRepository, diseaseRepository, epidemicRepository, gameRepository, playerRepository));
    }

    private void addToTimeline(State state) {
        this.timeline.put(timestamp, state);

        timestamp++;
    }

    public State findState(Integer timestamp) {
        return this.timeline.get(timestamp);
    }
}
