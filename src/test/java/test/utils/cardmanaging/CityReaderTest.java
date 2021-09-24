package test.utils.cardmanaging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.um.nine.domain.City;
import org.um.nine.utils.cardmanaging.CityCardReader;

public class CityReaderTest{

    @Test
    void CityNameReadingTest(){
        CityCardReader c = new CityCardReader();
        City[] cities = c.cityReader("Cards/testCities.json");
        String[] names = {"San Francisco","Atlanta","Chicago"};
        int counter = 0;
        for(City city: cities){
            Assertions.assertEquals(city.getName(),names[counter]);
            counter++;
        }
    }

    @Test
    void CityNeighboursReadingTest(){
        CityCardReader c = new CityCardReader();
        City[] cities = c.cityReader("Cards/testCities.json");
        City SF = cities[0];
        Assertions.assertEquals(SF.getNeighbors().get(0).getName(),"Atlanta");
        Assertions.assertEquals(SF.getNeighbors().get(1).getName(),"Chicago");
    }

    @Test
    void CityPopulationTest(){
        CityCardReader c = new CityCardReader();
        City[] cities = c.cityReader("Cards/testCities.json");
        //San Francisco
        Assertions.assertEquals(cities[0].getPopulation(),874961);
        //Atlanta
        Assertions.assertEquals(cities[1].getPopulation(),488800);
        //Chicago
        Assertions.assertEquals(cities[2].getPopulation(),2710000);
    }

}