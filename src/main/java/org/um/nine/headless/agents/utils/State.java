package org.um.nine.headless.agents.utils;

import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Cure;
import org.um.nine.headless.game.domain.Marker;
import org.um.nine.headless.game.domain.OutbreakMarker;

public class State implements IState {

    private IDiseaseRepository iDiseaseRepository;
    private IPlayerRepository iPlayerRepository;
    private IEpidemicRepository iEpidemicRepository;
    private ICityRepository iCityRepository;
    private ICardRepository iCardRepository;
    private IBoardRepository iBoardRepository;
    public State() {}

    public State(
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
        OutbreakMarker outbreaks = this.iDiseaseRepository.getOutbreakMarkers()
                .stream().filter(Marker::isCurrent)
                .findFirst().orElse(null);
        if (outbreaks == null) return true;

        var redCubes = this.iDiseaseRepository.getCubes().get(Color.RED).stream().filter(c -> c.getCity() == null).count();
        var blueCubes = this.iDiseaseRepository.getCubes().get(Color.BLUE).stream().filter(c -> c.getCity() == null).count();
        var blackCubes = this.iDiseaseRepository.getCubes().get(Color.BLACK).stream().filter(c -> c.getCity() == null).count();
        var yellowCubes = this.iDiseaseRepository.getCubes().get(Color.YELLOW).stream().filter(c -> c.getCity() == null).count();

        if (redCubes == 0 || blueCubes == 0 || blackCubes == 0 || yellowCubes == 0) return true;


        return false;
    }

    @Override
    public boolean isVictory() {
        return this.iDiseaseRepository.getCures().values().stream().filter(Cure::isDiscovered).count()>=4;
    }
}
