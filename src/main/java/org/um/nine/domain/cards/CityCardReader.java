package org.um.nine.domain.cards;

import com.google.gson.*;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.um.nine.domain.City;
import org.um.nine.exceptions.ColorNotFoundException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;

public class CityCardReader {
    public City[] cityReader() throws Exception {
        JsonParser parser = new JsonParser();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Cards/CityCards.json");
        Reader reader = new InputStreamReader(inputStream);
        JsonElement JElRoot = parser.parse(reader);
        JsonObject JORoot = JElRoot.getAsJsonObject();
        JsonArray JACities = JORoot.getAsJsonArray("Cities");
        City[] cities = new City[JACities.size()];
        for(int i = 0; i < JACities.size();i++){
            JsonElement JElCity = JACities.get(i);
            JsonObject JOCity = JElCity.getAsJsonObject();
            String name = JOCity.getAsJsonPrimitive("name").getAsString();
            String colorName = JOCity.getAsJsonPrimitive("color").getAsString();
            ColorRGBA color;
            switch (colorName){
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
            JsonArray JAPositions = JOCity.getAsJsonArray("position");
            if(JAPositions.size() != 3) throw new Exception("Positions are not formatted correctly");
            Vector3f pos = new Vector3f(JAPositions.get(0).getAsFloat(),JAPositions.get(1).getAsFloat(),JAPositions.get(2).getAsFloat());
            cities[i] = new City(name,color,pos);
        }
        for(int i = 0; i < JACities.size();i++){
            LinkedList<City> neighbours = new LinkedList<>();
            LinkedList<String> cityNames = new LinkedList<>();
            JsonElement JElCity = JACities.get(i);
            JsonObject JOCity = JElCity.getAsJsonObject();
            JsonArray JANeighbours = JOCity.getAsJsonArray("neighbours");
            for(int j = 0; j < JANeighbours.size();j++){
                cityNames.add(JANeighbours.get(j).getAsString());
            }
            for(int j = 0; j < cities.length;j++){
                if(cityNames.contains(cities[j].getName())){
                    cities[i].addNeighbour(cities[j]);
                }
            }
        }
        return cities;
    }

}
