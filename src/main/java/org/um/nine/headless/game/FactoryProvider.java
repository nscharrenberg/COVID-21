package org.um.nine.headless.game;

import org.um.nine.headless.game.repositories.*;

public class FactoryProvider {
    private static final DiseaseRepository diseaseRepository = new DiseaseRepository();
    private static final BoardRepository boardRepository = new BoardRepository();
    private static final CityRepository cityRepository = new CityRepository();
    private static final CardRepository cardRepository = new CardRepository();
    private static final EpidemicRepository epidemicRepository = new EpidemicRepository();
    private static final PlayerRepository playerRepository = new PlayerRepository();

    public static DiseaseRepository getDiseaseRepository() {
        return diseaseRepository;
    }

    public static BoardRepository getBoardRepository() {
        return boardRepository;
    }

    public static CityRepository getCityRepository() {
        return cityRepository;
    }

    public static CardRepository getCardRepository() {
        return cardRepository;
    }

    public static EpidemicRepository getEpidemicRepository() {
        return epidemicRepository;
    }

    public static PlayerRepository getPlayerRepository() {
        return playerRepository;
    }
}
