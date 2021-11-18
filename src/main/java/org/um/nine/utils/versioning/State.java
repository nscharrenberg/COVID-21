package org.um.nine.utils.versioning;

import com.rits.cloning.Cloner;
import org.um.nine.agents.Rhea.StateObservation;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.ActionType;

import java.util.List;

public class State implements StateObservation {
    private IBoardRepository boardRepository;
    private ICardRepository cardRepository;
    private ICityRepository cityRepository;
    private IDiseaseRepository diseaseRepository;
    private IEpidemicRepository epidemicRepository;
    private IGameRepository gameRepository;
    private IPlayerRepository playerRepository;

    public State(IBoardRepository boardRepository, ICardRepository cardRepository, ICityRepository cityRepository, IDiseaseRepository diseaseRepository, IEpidemicRepository epidemicRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        Cloner cloner = new Cloner();

        this.boardRepository = cloner.deepClone(boardRepository);
        this.cardRepository = cloner.deepClone(cardRepository);
        this.cityRepository = cloner.deepClone(cityRepository);
        this.diseaseRepository = cloner.deepClone(diseaseRepository);
        this.epidemicRepository = cloner.deepClone(epidemicRepository);
        this.gameRepository = cloner.deepClone(gameRepository);
        this.playerRepository = cloner.deepClone(playerRepository);
    }

    public IBoardRepository getBoardRepository() {
        return boardRepository;
    }

    public ICardRepository getCardRepository() {
        return cardRepository;
    }

    public ICityRepository getCityRepository() {
        return cityRepository;
    }

    public IDiseaseRepository getDiseaseRepository() {
        return diseaseRepository;
    }

    public IEpidemicRepository getEpidemicRepository() {
        return epidemicRepository;
    }

    public IGameRepository getGameRepository() {
        return gameRepository;
    }

    public IPlayerRepository getPlayerRepository() {
        return playerRepository;
    }


    @Override
    public List<ActionType> getAvailableActions() {
        return null;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public void advance(ActionType action) {

    }

    @Override
    public StateObservation copy() {
        return new State(this.boardRepository,
                this.cardRepository,
                this.cityRepository,
                this.diseaseRepository,
                this.epidemicRepository,
                this.gameRepository,
                this.playerRepository);
    }
}
