package org.um.nine.headless.agents.rhea.state;

import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Cure;

public interface IState extends Cloneable {

    boolean isOriginalGameState();

    default void reset() {
        getBoardRepository().reset();
        getPlayerRepository().reset();
        getCityRepository().reset();
        getDiseaseRepository().reset();
        getCardRepository().reset();
    }

    default void start() {
        getBoardRepository().start(this);
    }

    void setBoardRepository(IBoardRepository iBoardRepository);

    void setDiseaseRepository(IDiseaseRepository iDiseaseRepository);

    IDiseaseRepository getDiseaseRepository();

    void setPlayerRepository(IPlayerRepository iPlayerRepository);

    IPlayerRepository getPlayerRepository();

    IBoardRepository getBoardRepository();

    void setCityRepository(ICityRepository iCityRepository);

    ICityRepository getCityRepository();

    void setCardRepository(ICardRepository iCardRepository);

    ICardRepository getCardRepository();

    void setEpidemicRepository(IEpidemicRepository iEpidemicRepository);

    IEpidemicRepository getEpidemicRepository();
    default boolean isGameLost() {
        if (getDiseaseRepository().isGameOver()) return true;

        if (
                getDiseaseRepository().getCubes().get(Color.RED).stream().noneMatch(c -> c.getCity() == null) ||
                        getDiseaseRepository().getCubes().get(Color.BLUE).stream().noneMatch(c -> c.getCity() == null) ||
                        getDiseaseRepository().getCubes().get(Color.BLACK).stream().noneMatch(c -> c.getCity() == null) ||
                        getDiseaseRepository().getCubes().get(Color.YELLOW).stream().noneMatch(c -> c.getCity() == null)
        ) return true;


        return getCardRepository().getPlayerDeck().isEmpty();
        //TODO: missing some losing conditions
    }

    default boolean isGameWon() {
        return this.getDiseaseRepository().getCures().values().stream().allMatch(Cure::isDiscovered);
    }

    IState clone();


}
