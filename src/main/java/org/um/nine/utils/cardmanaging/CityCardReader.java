package org.um.nine.utils.cardmanaging;

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
    public City[] cityReader() {
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
            cities[i] = new City(name,color,pos);
            cities[i].setPopulation(population);
        }
        for(int i = 0; i < JACities.size();i++){
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
