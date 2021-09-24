package test.utils.cardmanaging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.um.nine.domain.City;
import org.um.nine.utils.cardmanaging.CityCardReader;

import java.util.HashMap;

public class CityReaderTest{

    @Test
    void CityNameReadingTest(){
        CityCardReader c = new CityCardReader();
        HashMap<String, City> cities = c.cityReader("Cards/testCities.json");
        String[] names = {"San Francisco","Atlanta","Chicago"};
        for(String city: names){
            Assertions.assertEquals(city,cities.get(city).getName());
        }
    }

    @Test
    void CityNeighboursReadingTest(){
        CityCardReader c = new CityCardReader();
        HashMap<String, City> cities = c.cityReader("Cards/testCities.json");
        City SF = cities.get("San Francisco");
        Assertions.assertEquals(SF.getNeighbors().get(0).getName(),"Atlanta");
        Assertions.assertEquals(SF.getNeighbors().get(1).getName(),"Chicago");
    }

    @Test
    void CityPopulationTest(){
        CityCardReader c = new CityCardReader();
        HashMap<String, City> cities = c.cityReader("Cards/testCities.json");
        City[] city = {cities.get("San Francisco"),cities.get("Atlanta"),cities.get("Chicago")};
        //San Francisco
        Assertions.assertEquals(city[0].getPopulation(),874961);
        //Atlanta
        Assertions.assertEquals(city[1].getPopulation(),488800);
        //Chicago
        Assertions.assertEquals(city[2].getPopulation(),2710000);
    }

}