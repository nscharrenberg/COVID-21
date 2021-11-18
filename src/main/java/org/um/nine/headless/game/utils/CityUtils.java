package org.um.nine.headless.game.utils;


import com.google.gson.*;
import com.jme3.math.Vector3f;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Stack;

public class CityUtils {
    public HashMap<String, City> reader(String path) {
        JsonParser parser = new JsonParser();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        Reader reader = new InputStreamReader(inputStream);
        JsonElement JElRoot = parser.parse(reader);
        JsonObject JORoot = JElRoot.getAsJsonObject();
        JsonArray JACities = JORoot.getAsJsonArray("Cities");
        HashMap<String, City> cities = new HashMap<>();

        for (int i = 0; i < JACities.size(); i++) {
            JsonElement JElCity = JACities.get(i);
            JsonObject JOCity = JElCity.getAsJsonObject();
            String name = JOCity.getAsJsonPrimitive("name").getAsString();
            String colorName = JOCity.getAsJsonPrimitive("color").getAsString();
            Color color = Color.ORANGE;

            switch (colorName) {
                case "Blue":
                    color = Color.BLUE;
                    break;
                case "Black":
                    color = Color.BLACK;
                    break;
                case "Red":
                    color = Color.RED;
                    break;
                case "Yellow":
                    color = Color.YELLOW;
                    break;
                default:
                    System.out.println("Invalid Color Provided in " + name);
            }

            int population = JOCity.getAsJsonPrimitive("population").getAsInt();
            JsonArray JAPositions = JOCity.getAsJsonArray("position");
            Vector3f pos = new Vector3f(0,0,0);

            try{
                pos = new Vector3f(JAPositions.get(0).getAsFloat(),JAPositions.get(1).getAsFloat(),JAPositions.get(2).getAsFloat());
            }
            catch(Exception e){
                System.err.println("Positions are not formatted correctly");
                System.err.close();
            }

            City city = new City(name,color,pos);
            city.setPopulation(population);

            cities.put(name, city);

            JsonArray JANeighbours = JOCity.getAsJsonArray("neighbours");
            for(int j = 0; j < JANeighbours.size();j++){
                if(cities.get(JANeighbours.get(j).getAsString()) != null){
                    cities.get(name).addNeighbor(cities.get(JANeighbours.get(j).getAsString()));
                }
            }
        }

        return cities;
    }

    public static Stack<PlayerCard> generateCityCardDeck(City[] cities) {
        Stack<PlayerCard> cityDeck = new Stack<>();

        for (City city : cities) {
            cityDeck.add(new CityCard(city));
        }

        return cityDeck;
    }

    public static Stack<InfectionCard> generateInfectionDeck(City[] cities) {
        Stack<InfectionCard> infectionDeck = new Stack<>();

        for (City city : cities) {
            infectionDeck.add(new InfectionCard(city));
        }

        return infectionDeck;
    }
}
