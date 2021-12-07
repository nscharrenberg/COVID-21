package org.um.nine.headless.game.utils;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jme3.math.Vector3f;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;

public class CityUtils {
    public static HashMap<String, City> reader(String path) {
        JsonParser parser = new JsonParser();
        InputStream inputStream = CityUtils.class.getClassLoader().getResourceAsStream(path);
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
                case "Blue" -> color = Color.BLUE;
                case "Black" -> color = Color.BLACK;
                case "Red" -> color = Color.RED;
                case "Yellow" -> color = Color.YELLOW;
                default -> System.out.println("Invalid Color Provided in " + name);
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

    public static Stack<InfectionCard> loadInfectionDeck(HashMap<String,City> cities){
        Stack<InfectionCard> infectionDeck = new Stack<>();
        try {
            FileReader fr = new FileReader(
                    Objects.requireNonNull(
                            CityUtils.class.getClassLoader().getResource(
                                    "Cards/InfectionDeck.txt")
                    ).getFile()
            );
            Scanner sc = new Scanner(fr);
            while(sc.hasNextLine()){
                String s = sc.nextLine();
                infectionDeck.push(new InfectionCard(cities.get(s)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return infectionDeck;
    }
    public static Stack<InfectionCard> generateInfectionDeck(City[] cities) {
        Stack<InfectionCard> infectionDeck = new Stack<>();

        for (City city : cities) {
            infectionDeck.add(new InfectionCard(city));
        }

        return infectionDeck;
    }
}
