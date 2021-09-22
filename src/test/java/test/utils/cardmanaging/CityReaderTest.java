package test.utils.cardmanaging;

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
            assert(city.getName().equals(names[counter]));
            counter++;
        }
    }

    @Test
    void CityNeighboursReadingTest(){
        CityCardReader c = new CityCardReader();
        City[] cities = c.cityReader("Cards/testCities.json");
        City SF = cities[0];
        assert(SF.getNeighbors().get(0).getName().equals("Atlanta"));
        assert(SF.getNeighbors().get(1).getName().equals("Chicago"));
    }

    @Test
    void CityPopulationTest(){
        CityCardReader c = new CityCardReader();
        City[] cities = c.cityReader("Cards/testCities.json");
        //San Francisco
        assert(cities[0].getPopulation() == 874961);
        //Atlanta
        assert(cities[1].getPopulation() == 488800);
        //Chicago
        assert(cities[2].getPopulation() == 2710000);
    }

}