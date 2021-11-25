package org.um.nine.v1.utils.cardmanaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.um.nine.v1.domain.City;
import org.um.nine.v1.domain.cards.CityCard;
import org.um.nine.v1.domain.cards.InfectionCard;
import org.um.nine.v1.domain.cards.PlayerCard;
import org.um.nine.v1.exceptions.ColorNotFoundException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Stack;

public class CityCardReader {

    public HashMap<String, City> cityReader(String path) {
        JsonParser parser = new JsonParser();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        Reader reader = new InputStreamReader(inputStream);
        JsonElement JElRoot = parser.parse(reader);
        JsonObject JORoot = JElRoot.getAsJsonObject();
        JsonArray JACities = JORoot.getAsJsonArray("Cities");
        HashMap<String, City> cities = new HashMap<>();

        for(int i = 0; i < JACities.size();i++){

            JsonElement JElCity = JACities.get(i);
            JsonObject JOCity = JElCity.getAsJsonObject();
            String name = JOCity.getAsJsonPrimitive("name").getAsString();
            String colorName = JOCity.getAsJsonPrimitive("color").getAsString();
            ColorRGBA color = ColorRGBA.Orange;
            try {
                switch (colorName) {
                    case "Blue":
                        color = ColorRGBA.Blue;
                        break;
                    case "Black":
                        color = ColorRGBA.Black;
                        break;
                    case "Red":
                        color = ColorRGBA.Red;
                        break;
                    case "Yellow":
                        color = ColorRGBA.Yellow;
                        break;
                    default:
                        throw new ColorNotFoundException();
                }
            }
            catch(ColorNotFoundException e){
                System.err.println("Color not found in City: " + name);
                System.err.close();
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
                    cities.get(name).addNeighbour(cities.get(JANeighbours.get(j).getAsString()));
                }
            }
        }

        return cities;
    }

    public static CityCard generateCityCard(City city){
        return new CityCard(city);
    }

    public static Stack<PlayerCard> generateCityCardDeck(City[] cities){
        Stack<PlayerCard> cityDeck = new Stack<>();
        for (City c:cities) {
            cityDeck.add(generateCityCard(c));
        }
        return cityDeck;
    }
    public static InfectionCard generateInfectionCard(City city){
        return new InfectionCard(city);
    }

    public static Stack<InfectionCard> generateInfectionDeck(City[] cities){
        Stack<InfectionCard> infectionDeck = new Stack<>();
        for (City c:cities) {
            infectionDeck.add(generateInfectionCard(c));
        }
        return infectionDeck;
    }
}
