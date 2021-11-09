package org.um.nine.agents.reinforcement.utils;

import com.rits.cloning.Cloner;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.City;
import org.um.nine.domain.Marker;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.domain.cards.PlayerCard;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureExtraction {
    private IBoardRepository boardRepository;
    private ICardRepository cardRepository;
    private ICityRepository cityRepository;
    private IDiseaseRepository diseaseRepository;
    private IEpidemicRepository epidemicRepository;
    private IGameRepository gameRepository;
    private IPlayerRepository playerRepository;

    // City Features
    private HashMap<String, HashMap<String, Integer>> diseasesOnCity = new HashMap<>();
    private HashMap<String, Boolean> researchStationOnCity = new HashMap<>();
    private HashMap<String, Integer> playersOnCity = new HashMap<>();
    private HashMap<String, List<String>> adjacentCities = new HashMap<>();

    // Player Features
    private String currentPlayer;
    private Queue<String> playerOrder = new LinkedList<>();
    private HashMap<String, String> playerRole = new HashMap<>();
    private HashMap<String, List<PlayerCard>> playerCardInHands = new HashMap<>();

    // Disease Features
    private HashMap<String, Integer> diseaseCubesOnBoard = new HashMap<>();
    private HashMap<String, Boolean> curedDiseasesOnBoard = new HashMap<>();
    private HashMap<String, Boolean> cureEradicated = new HashMap<>();

    // Board Features
    private int playerCount = 2; // 2 is the minimum amount of players, so default is 2
    private int numberOfResearchStationsOnBoard = 1; // Atlanta has a research station by default, so we always have at least 1 research station
    private int outbreakRate = 0;
    private int infectionRate = 0;

    // Card Features
    private Stack<InfectionCard> infectionDiscardPile = new Stack<>();
    private LinkedList<PlayerCard> eventDiscardPile;

    public FeatureExtraction(IBoardRepository boardRepository, ICardRepository cardRepository, ICityRepository cityRepository, IDiseaseRepository diseaseRepository, IEpidemicRepository epidemicRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        Cloner cloner = new Cloner();

        this.boardRepository = cloner.deepClone(boardRepository);
        this.cardRepository = cloner.deepClone(cardRepository);
        this.cityRepository = cloner.deepClone(cityRepository);
        this.diseaseRepository = cloner.deepClone(diseaseRepository);
        this.epidemicRepository = cloner.deepClone(epidemicRepository);
        this.gameRepository = cloner.deepClone(gameRepository);
        this.playerRepository = cloner.deepClone(playerRepository);
    }

    public void extract() {
        this.extractBoardFeatures();
        this.extractCardFeatures();
        this.extractCityFeatures();
        this.extractDiseaseFeatures();
        this.extractPlayerFeatures();
    }

    private void extractBoardFeatures() {
        this.playerCount = playerRepository.getPlayers().size();
        diseaseRepository.getOutbreakMarker().stream().filter(Marker::isCurrent).findFirst().ifPresent(outbreakMarker -> this.outbreakRate = outbreakMarker.getId());
        diseaseRepository.getInfectionRate().stream().filter(Marker::isCurrent).findFirst().ifPresent(infectionRateMarker -> this.infectionRate = infectionRateMarker.getId());
    }

    private void extractPlayerFeatures() {
        this.playerOrder = playerRepository.getPlayerOrder().stream().map(Player::getName).collect(Collectors.toCollection(LinkedList::new));

        playerRepository.getPlayers().forEach((n, p) -> {
            this.playerCardInHands.put(n, p.getHandCards());
            this.playerRole.put(n, p.getRole().getName());
        });
    }

    private void extractDiseaseFeatures() {
        diseaseRepository.getCures().forEach((k, v) -> {
            this.curedDiseasesOnBoard.put(k.toString(), v.isDiscovered());

            // TODO: Add logic to check if disease is eradicated.
            this.cureEradicated.put(k.toString(), false);
        });
    }

    private void extractCardFeatures() {
        this.eventDiscardPile = cardRepository.getEventDiscardPile();
        this.infectionDiscardPile = cardRepository.getInfectionDiscardPile();
    }

    private void extractCityFeatures() {
        this.cityRepository.getCities().forEach((k, v) -> {
            this.researchStationOnCity.put(k, v.getResearchStation() != null);
            this.playersOnCity.put(k, v.getPawns().size());
            this.adjacentCities.put(k, v.getNeighbors().stream().map(City::getName).toList());

            // Retrieve diseases per city
            HashMap<String, Integer> cubesPerColor = new HashMap<>();
            v.getCubes().forEach(c -> {
                cubesPerColor.merge(c.getColor().toString(), 1, Integer::sum);
                this.diseaseCubesOnBoard.put(c.getColor().toString(), this.diseaseCubesOnBoard.get(c.getColor().toString()) + 1);
            });
            this.diseasesOnCity.put(k, cubesPerColor);
        });
    }

    public HashMap<String, HashMap<String, Integer>> getDiseasesOnCity() {
        return diseasesOnCity;
    }

    public HashMap<String, Boolean> getResearchStationOnCity() {
        return researchStationOnCity;
    }

    public HashMap<String, Integer> getPlayersOnCity() {
        return playersOnCity;
    }

    public HashMap<String, List<String>> getAdjacentCities() {
        return adjacentCities;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public Queue<String> getPlayerOrder() {
        return playerOrder;
    }

    public HashMap<String, String> getPlayerRole() {
        return playerRole;
    }

    public HashMap<String, List<PlayerCard>> getPlayerCardInHands() {
        return playerCardInHands;
    }

    public HashMap<String, Integer> getDiseaseCubesOnBoard() {
        return diseaseCubesOnBoard;
    }

    public HashMap<String, Boolean> getCuredDiseasesOnBoard() {
        return curedDiseasesOnBoard;
    }

    public HashMap<String, Boolean> getCureEradicated() {
        return cureEradicated;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getNumberOfResearchStationsOnBoard() {
        return numberOfResearchStationsOnBoard;
    }

    public int getOutbreakRate() {
        return outbreakRate;
    }

    public int getInfectionRate() {
        return infectionRate;
    }

    public Stack<InfectionCard> getInfectionDiscardPile() {
        return infectionDiscardPile;
    }

    public LinkedList<PlayerCard> getEventDiscardPile() {
        return eventDiscardPile;
    }
}
