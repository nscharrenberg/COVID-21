package org.um.nine.headless.game;

import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.repositories.*;

public class FactoryProvider {
    private static final IDiseaseRepository diseaseRepository = new DiseaseRepository();
    private static final IBoardRepository boardRepository = new BoardRepository();
    private static final ICityRepository cityRepository = new CityRepository();
    private static final ICardRepository cardRepository = new CardRepository();
    private static final IEpidemicRepository epidemicRepository = new EpidemicRepository();
    private static final IPlayerRepository playerRepository = new PlayerRepository();

    private FactoryProvider() {}

    public static IDiseaseRepository getDiseaseRepository() {
        return diseaseRepository;
    }

    public static IBoardRepository getBoardRepository() {
        return boardRepository;
    }

    public static ICityRepository getCityRepository() {
        return cityRepository;
    }

    public static ICardRepository getCardRepository() {
        return cardRepository;
    }

    public static IEpidemicRepository getEpidemicRepository() {
        return epidemicRepository;
    }

    public static IPlayerRepository getPlayerRepository() {
        return playerRepository;
    }
}
