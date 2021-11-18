package org.um.nine.headless.game.domain;

import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class City {
    private static int INCREMENT = 0;
    private int id;
    private String name;
    private Color color;
    private Vector3f location;
    private int population;
    private ResearchStation researchStation;
    private List<Disease> cubes = new ArrayList<>();
    private List<Player> pawns = new ArrayList<>();
    private List<City> neighbors = new ArrayList<>();

    public City(String name, Color color, Vector3f location) {
        this.id = INCREMENT;
        this.name = name;
        this.color = color;
        this.location = location;

        INCREMENT++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public List<City> getNeighbors() {
        return neighbors;
    }

    public List<Disease> getCubes() {
        return cubes;
    }

    public void setCubes(List<Disease> cubes) {
        this.cubes = cubes;
    }

    /**
     * adds a disease cube to a city
     * @param disease - disease cube to be added
     * @return true if successful, false if outbreak occurs
     */
    public boolean addCube(Disease disease) {
        if (this.cubes == null) {
            this.cubes = new ArrayList<>();
        }

        if (this.cubes.size() >= 3) {
            Map<String, List<Disease>> grouped = this.cubes.stream().collect(
                    Collectors.groupingBy(att -> att.getColor().getName())
            );

            boolean outbreak = false;

            for (Map.Entry<String, List<Disease>> entry : grouped.entrySet()) {
                if (entry.getValue().size() >= 3 &&
                        disease.getColor().getName().equals(entry.getKey())) {
                    outbreak = true;
                    break;
                }
            }

            if (outbreak) {
                return false;
            }
        }

        this.cubes.add(disease);
        disease.setCity(this);

        return true;
    }

    /**
     * Add a neighbouring city
     * @param city - the neighbour
     */
    public void addNeighbor(City city) {
        if (this.neighbors == null) {
            this.neighbors = new ArrayList<>();
        }

        this.neighbors.add(city);

        // If neighbor doesn't have this city as a neighbor yet, add it as well.
        if (!city.getNeighbors().contains(this)) {
            city.addNeighbor(this);
        }
    }

    public void setNeighbors(List<City> neighbors) {
        this.neighbors = neighbors;
    }

    public ResearchStation getResearchStation() {
        return researchStation;
    }

    public void setResearchStation(ResearchStation researchStation) {
        this.researchStation = researchStation;

        this.researchStation.setCity(this);
    }

    public void addPawn(Player player) {
        if (this.pawns == null) {
            this.pawns = new ArrayList<>();
        }

        this.pawns.add(player);
        player.setCity(this);
    }

    public List<Player> getPawns() {
        return pawns;
    }

    public void setPawns(List<Player> pawns) {
        this.pawns = pawns;
    }

    @Override
    public String toString() {
        return "city-" + this.getId() + "-" + this.getName() + "-" + this.getColor().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (id != city.id) return false;
        if (population != city.population) return false;
        if (!name.equals(city.name)) return false;
        if (color != city.color) return false;
        return location.equals(city.location);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + color.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + population;
        return result;
    }
}
