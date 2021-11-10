package org.um.nine.agents.reinforcement.utils;

import org.um.nine.domain.City;
import org.um.nine.domain.Marker;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.utils.versioning.State;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureExtraction {
    private State state;

    private int difficulty;

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
    private LinkedList<PlayerCard> eventDiscardPile = new LinkedList<>();
    private int playerCardsCount;
    private int infectionCardsCount;
    private int infectionCardDiscardCount;

    public FeatureExtraction(State state) {
        this.state = state;
    }

    public void extract() {
        this.extractBoardFeatures();
        this.extractCardFeatures();
        this.extractCityFeatures();
        this.extractDiseaseFeatures();
        this.extractPlayerFeatures();
    }

    private void extractBoardFeatures() {
        this.playerCount = state.getPlayerRepository().getPlayers().size();
        state.getDiseaseRepository().getOutbreakMarker().stream().filter(Marker::isCurrent).findFirst().ifPresent(outbreakMarker -> this.outbreakRate = outbreakMarker.getId());
        state.getDiseaseRepository().getInfectionRate().stream().filter(Marker::isCurrent).findFirst().ifPresent(infectionRateMarker -> this.infectionRate = infectionRateMarker.getId());
        this.difficulty = state.getBoardRepository().getDifficulty().getCount();
    }

    private void extractPlayerFeatures() {
        this.playerOrder = state.getPlayerRepository().getPlayerOrder().stream().map(Player::getName).collect(Collectors.toCollection(LinkedList::new));

        state.getPlayerRepository().getPlayers().forEach((n, p) -> {
            this.playerCardInHands.put(n, p.getHandCards());
            this.playerRole.put(n, p.getRole().getName());
        });
    }

    private void extractDiseaseFeatures() {
        state.getDiseaseRepository().getCures().forEach((k, v) -> {
            this.curedDiseasesOnBoard.put(k.toString(), v.isDiscovered());

            // TODO: Add logic to check if disease is eradicated.
            this.cureEradicated.put(k.toString(), false);
        });
    }

    private void extractCardFeatures() {
        this.eventDiscardPile = state.getCardRepository().getEventDiscardPile();
        this.infectionDiscardPile = state.getCardRepository().getInfectionDiscardPile();
        this.playerCardsCount = state.getCardRepository().getPlayerDeck().size();
        this.infectionCardsCount = state.getCardRepository().getInfectionDeck().size();
        this.infectionCardDiscardCount = state.getCardRepository().getInfectionDiscardPile().size();

    }

    private void extractCityFeatures() {
        this.state.getCityRepository().getCities().forEach((k, v) -> {
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

    public int getDifficulty() {
        return difficulty;
    }

    public int getPlayerCardsCount() {
        return playerCardsCount;
    }

    public int getInfectionCardsCount() {
        return infectionCardsCount;
    }

    public int getInfectionCardDiscardCount() {
        return infectionCardDiscardCount;
    }

    public State getState() {
        return state;
    }
}
