package org.um.nine.utils.versioning;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.*;

import java.util.HashMap;

public class ReplayManager {
    private HashMap<String, Replay> replays = new HashMap<>();
    private Replay currentReplay = null;

    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private ICardRepository cardRepository;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IEpidemicRepository epidemicRepository;

    public void create() {
        Replay replay = new Replay(boardRepository.getVersion());
        replay.stamp(boardRepository, cardRepository, cityRepository, diseaseRepository, epidemicRepository, gameRepository, playerRepository);

        setCurrentReplay(replay);
    }

    /**
     * Add teh current state to the timeline of the current replay
     */
    public void stamp() {
        if (this.currentReplay == null) {
            create();
        }

        stamp(this.currentReplay);
    }

    /**
     * Add the current state to the timeline of the given replay
     * @param replay - the given replay
     */
    public void stamp(Replay replay) {
        replay.stamp(boardRepository, cardRepository, cityRepository, diseaseRepository, epidemicRepository, gameRepository, playerRepository);
    }

    public void setCurrentReplay(Replay replay) {
        this.replays.put(replay.getVersion(), replay);
        this.currentReplay = replay;
    }

    public Replay getCurrentReplay() {
        return this.currentReplay;
    }

    public Replay findReplay(String version) {
        return this.replays.get(version);
    }

    public HashMap<String, Replay> getReplays() {
        return this.replays;
    }
}
