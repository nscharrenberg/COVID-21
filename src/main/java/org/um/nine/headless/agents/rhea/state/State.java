package org.um.nine.headless.agents.rhea.state;

import org.um.nine.headless.game.contracts.repositories.*;

public class State implements IState {

    private IDiseaseRepository iDiseaseRepository;
    private IPlayerRepository iPlayerRepository;
    private IEpidemicRepository iEpidemicRepository;
    private ICityRepository iCityRepository;
    private ICardRepository iCardRepository;
    private IBoardRepository iBoardRepository;
    public State() {}

    public State( IDiseaseRepository iDiseaseRepository,
                  IPlayerRepository iPlayerRepository,
                  ICardRepository iCardRepository,
                  ICityRepository iCityRepository,
                  IEpidemicRepository iEpidemicRepository,
                  IBoardRepository iBoardRepository
    ) {
        this.setDiseaseRepository(iDiseaseRepository);
        this.setPlayerRepository(iPlayerRepository);
        this.setCardRepository(iCardRepository);
        this.setCityRepository(iCityRepository);
        this.setEpidemicRepository(iEpidemicRepository);
        this.setBoardRepository(iBoardRepository);
    }


    @Override
    public void setDiseaseRepository(IDiseaseRepository iDiseaseRepository) {
        this.iDiseaseRepository = iDiseaseRepository;
    }
    @Override
    public IDiseaseRepository getDiseaseRepository() {
        return this.iDiseaseRepository;
    }

    @Override
    public void setPlayerRepository(IPlayerRepository iPlayerRepository) {
        this.iPlayerRepository = iPlayerRepository;
    }
    @Override
    public IPlayerRepository getPlayerRepository() {
        return this.iPlayerRepository;
    }
    @Override
    public void setBoardRepository(IBoardRepository iBoardRepository) {
        this.iBoardRepository = iBoardRepository;
    }
    @Override
    public IBoardRepository getBoardRepository() {
        return this.iBoardRepository;
    }

    @Override
    public void setCityRepository(ICityRepository iCityRepository) {
        this.iCityRepository = iCityRepository;
    }

    @Override
    public ICityRepository getCityRepository() {
        return this.iCityRepository;
    }

    @Override
    public void setCardRepository(ICardRepository iCardRepository) {
        this.iCardRepository = iCardRepository;
    }
    @Override
    public ICardRepository getCardRepository() {
        return this.iCardRepository;
    }


    @Override
    public void setEpidemicRepository(IEpidemicRepository iEpidemicRepository) {
        this.iEpidemicRepository = iEpidemicRepository;
    }
    @Override
    public IEpidemicRepository getEpidemicRepository() {
        return this.iEpidemicRepository;
    }
}
