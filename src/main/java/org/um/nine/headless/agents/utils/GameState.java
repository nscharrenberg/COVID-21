package org.um.nine.headless.agents.utils;

import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.domain.Cure;

public class GameState implements State {

    private IDiseaseRepository iDiseaseRepository;
    private IPlayerRepository iPlayerRepository;
    private IEpidemicRepository iEpidemicRepository;
    private ICityRepository iCityRepository;
    private ICardRepository iCardRepository;
    private IBoardRepository iBoardRepository;
    public GameState() {}

    public GameState(
            IDiseaseRepository iDiseaseRepository,
            IPlayerRepository iPlayerRepository,
            ICardRepository iCardRepository,
            ICityRepository iCityRepository,
            IEpidemicRepository iEpidemicRepository,
            IBoardRepository iBoardRepository
    ) {
        this.iDiseaseRepository = iDiseaseRepository;
        this.iPlayerRepository = iPlayerRepository;
        this.iCardRepository = iCardRepository;
        this.iCityRepository = iCityRepository;
        this.iEpidemicRepository = iEpidemicRepository;
        this.iBoardRepository = iBoardRepository;
    }

    @Override
    public IDiseaseRepository getDiseaseRepository() {
        return this.iDiseaseRepository;
    }

    @Override
    public IPlayerRepository getPlayerRepository() {
        return this.iPlayerRepository;
    }

    @Override
    public IBoardRepository getBoardRepository() {
        return this.iBoardRepository;
    }

    @Override
    public ICityRepository getCityRepository() {
        return this.iCityRepository;
    }

    @Override
    public ICardRepository getCardRepository() {
        return this.iCardRepository;
    }

    @Override
    public IEpidemicRepository getEpidemicRepository() {
        return this.iEpidemicRepository;
    }

    @Override
    public boolean gameOver() {
        //TODO: implement game over strategy
        return false;
    }

    @Override
    public boolean isVictory() {
        return this.iDiseaseRepository.getCures().values().stream().filter(Cure::isDiscovered).count()>=4;
    }
}
