package org.um.nine.utils.versioning;

import org.um.nine.contracts.repositories.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Replay {
    private String version;
    private List<State> timeline = new ArrayList<>();
    private Integer currentIndex = null;

    public Replay(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<State> getTimeline() {
        return Collections.unmodifiableList(timeline);
    }

    public void setTimeline(List<State> timeline) {
        this.timeline = timeline;
    }

    public void stamp(IBoardRepository boardRepository, ICardRepository cardRepository, ICityRepository cityRepository, IDiseaseRepository diseaseRepository, IEpidemicRepository epidemicRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.addToTimeline(new State(boardRepository, cardRepository, cityRepository, diseaseRepository, epidemicRepository, gameRepository, playerRepository));
    }

    private void addToTimeline(State state) {
        this.timeline.add(state);
    }

    public State findState(Integer timestamp) {
        return this.timeline.get(timestamp);
    }

    /**
     * Grab the current state
     * @return the current state on the timeline
     */
    public State getCurrentState() {
        if (currentIndex == null) {
            return null;
        }

        try {
            return this.timeline.get(currentIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Go to the next state of the timeline
     * @return the next state on the timeline
     */
    public State nextState() {
        State found = this.timeline.get(currentIndex);
        currentIndex++;

        return found;
    }
}
