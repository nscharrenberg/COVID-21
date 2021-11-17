package org.um.nine.agents.reinforcement.utils;

import com.jme3.math.ColorRGBA;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.domain.roles.*;
import org.um.nine.utils.versioning.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgentBox implements Encodable {
    public static int FEATURE_COUNT = 14;

    private static final int CITY_COUNT = 0;
    private static final int NEIGHBOURING_CITY_INDEX = 0;
    private static final int RED_DISEASE_CUBES_ON_CITY = CITY_COUNT;
    private static final int BLACK_DISEASE_CUBES_ON_CITY = CITY_COUNT + 1;
    private static final int BLUE_DISEASE_CUBES_ON_CITY = CITY_COUNT + 2;
    private static final int YELLOW_DISEASE_CUBES_ON_CITY = CITY_COUNT + 3;
    private static final int RESEARCH_STATION_ON_CITY = CITY_COUNT + 4;
    private static final int PLAYER_ONE_LOCATION = CITY_COUNT + 5;
    private static final int PLAYER_ROLES = CITY_COUNT + 9;
    private static final int CARDS_IN_HANDS = CITY_COUNT + 10;
    private static final int GLOBAL_INFO = CITY_COUNT + 14;

    private static final int PLAYER_TURN = 0;
    private static final int INFECTION_COUNT = 1;
    private static final int OUTBREAK_COUNT = 2;
    private static final int PLAYER_COUNT = 3;
    private static final int DIFFICULTY_LEVEL = 4;
    private static final int TOTAL_RESEARCH_STATIONS = 5;
    private static final int PLAYER_CARD_COUNT = 6;
    private static final int INFECTION_CARD_COUNT = 7;
    private static final int INFECTION_DISCARD_CARD_COUNT = 8;

    private final FeatureExtraction features;

    public AgentBox(FeatureExtraction features) {
        this.features = features;
    }

    @Override
    public double[] toArray() {
       return getData().toDoubleVector();
    }

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
       INDArray temp = Nd4j.zeros(48, 112);

       List<String> sortedCities = features.getCities().stream().sorted().collect(Collectors.toList());

        for (int i = 0; i < sortedCities.size(); i++) {
            String cityName = sortedCities.get(i);

            // Pass along neighbors
            HashMap<String, Integer> diseases = features.getDiseasesOnCity().get(cityName);

            // Column 2 - Red Disease Cube Count per city
            temp.put(i, RED_DISEASE_CUBES_ON_CITY, diseases.get(ColorRGBA.Red.toString()));

            // Column 3 - Black disease cube count per city
            temp.put(i, BLACK_DISEASE_CUBES_ON_CITY, diseases.get(ColorRGBA.Black.toString()));

            // Column 4 - Blue disease cube count per city
            temp.put(i, BLUE_DISEASE_CUBES_ON_CITY, diseases.get(ColorRGBA.Blue.toString()));

            // Column 5 - Yellow disease cube count per city
            temp.put(i, YELLOW_DISEASE_CUBES_ON_CITY, diseases.get(ColorRGBA.Yellow.toString()));

            // Column 6 - Research station on city
            temp.put(i, RESEARCH_STATION_ON_CITY, features.getResearchStationOnCity().get(cityName) ? 1 : 0);

            // Column 7 to 10 - Player Locations
            int index = 0;
            for (Map.Entry<String, String> entry : features.getPlayerCity().entrySet()) {
                if (entry.getValue().equals(cityName)) {
                    temp.put(i, PLAYER_ONE_LOCATION + index, 1);
                }

                index++;

                // Only allow up to 4 players. Enforcing to ensure fixed length matrix.
                if (index == 4) {
                    break;
                }
            }

            // Column 12 to 15 - Cards in Hand
            index = 0;
            for (Map.Entry<String, List<PlayerCard>> entry : features.getPlayerCardInHands().entrySet()) {
                List<PlayerCard> cards = entry.getValue();

                if (cards.stream().anyMatch(v -> v.getName().equals(cityName))) {
                    temp.put(i, CARDS_IN_HANDS + index, 1);
                }

                index++;

                // Only allow up to 4 players. Enforcing to ensure fixed length matrix.
                if (index == 4) {
                    break;
                }
            }
        }

        int index = 0;
        for (Map.Entry<String, String> entry : features.getPlayerRole().entrySet()) {
            // Column 11 - Player Roles
            temp.put(index, PLAYER_ROLES, roleToInteger(entry.getValue()));

            index++;

            // Only allow up to 4 players. Enforcing to ensure fixed length matrix.
            if (index == 4) {
                break;
            }
        }

        // Column 16 - Global Information
        temp.put(PLAYER_TURN, GLOBAL_INFO, features.getPlayerId().get(features.getCurrentPlayer()));
        temp.put(INFECTION_COUNT, GLOBAL_INFO, features.getInfectionRate());
        temp.put(OUTBREAK_COUNT, GLOBAL_INFO, features.getOutbreakRate());
        temp.put(PLAYER_COUNT, GLOBAL_INFO, features.getPlayerCount());
        temp.put(DIFFICULTY_LEVEL, GLOBAL_INFO, features.getDifficulty());
        temp.put(TOTAL_RESEARCH_STATIONS, GLOBAL_INFO, features.getNumberOfResearchStationsOnBoard());
        temp.put(PLAYER_CARD_COUNT, GLOBAL_INFO, features.getPlayerCardsCount());
        temp.put(INFECTION_CARD_COUNT, GLOBAL_INFO, features.getInfectionCardsCount());
        temp.put(INFECTION_DISCARD_CARD_COUNT, GLOBAL_INFO, features.getInfectionCardDiscardCount());

        return temp;
    }

    public static int roleToInteger(String name) {
       if (name.equals(ContingencyPlannerRole.NAME)) {
           return 1;
       } else if (name.equals(DispatcherRole.NAME)) {
           return 2;
       } else if (name.equals(MedicRole.NAME)) {
           return 3;
       } else if (name.equals(OperationsExpertRole.NAME)) {
           return 4;
       } else if (name.equals(QuarantineSpecialistRole.NAME)) {
           return 5;
       } else if (name.equals(ResearcherRole.NAME)) {
           return 6;
       } else if (name.equals(ScientistRole.NAME)) {
           return 7;
       } else {
           return 0;
       }
    }

    @Override
    public Encodable dup() {
        return null;
    }

    public static int getFeatureCount() {
        return FEATURE_COUNT;
    }

    public static int getCityCount() {
        return CITY_COUNT;
    }

    public static int getNeighbouringCityIndex() {
        return NEIGHBOURING_CITY_INDEX;
    }

    public static int getRedDiseaseCubesOnCity() {
        return RED_DISEASE_CUBES_ON_CITY;
    }

    public static int getBlackDiseaseCubesOnCity() {
        return BLACK_DISEASE_CUBES_ON_CITY;
    }

    public static int getBlueDiseaseCubesOnCity() {
        return BLUE_DISEASE_CUBES_ON_CITY;
    }

    public static int getYellowDiseaseCubesOnCity() {
        return YELLOW_DISEASE_CUBES_ON_CITY;
    }

    public static int getResearchStationOnCity() {
        return RESEARCH_STATION_ON_CITY;
    }

    public static int getPlayerOneLocation() {
        return PLAYER_ONE_LOCATION;
    }

    public static int getPlayerRoles() {
        return PLAYER_ROLES;
    }

    public static int getCardsInHands() {
        return CARDS_IN_HANDS;
    }

    public static int getGlobalInfo() {
        return GLOBAL_INFO;
    }

    public static int getPlayerTurn() {
        return PLAYER_TURN;
    }

    public static int getInfectionCount() {
        return INFECTION_COUNT;
    }

    public static int getOutbreakCount() {
        return OUTBREAK_COUNT;
    }

    public static int getPlayerCount() {
        return PLAYER_COUNT;
    }

    public static int getDifficultyLevel() {
        return DIFFICULTY_LEVEL;
    }

    public static int getTotalResearchStations() {
        return TOTAL_RESEARCH_STATIONS;
    }

    public static int getPlayerCardCount() {
        return PLAYER_CARD_COUNT;
    }

    public static int getInfectionCardCount() {
        return INFECTION_CARD_COUNT;
    }

    public static int getInfectionDiscardCardCount() {
        return INFECTION_DISCARD_CARD_COUNT;
    }

    public FeatureExtraction getFeatures() {
        return features;
    }

    public State getState() {
        return this.features.getState();
    }
}
