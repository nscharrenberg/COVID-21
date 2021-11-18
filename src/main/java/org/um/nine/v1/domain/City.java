package org.um.nine.v1.domain;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class City {
    private String name;
    private ColorRGBA color;
    private List<Disease> cubes = new ArrayList<>();
    private List<Player> pawns = new ArrayList<>();
    private ResearchStation researchStation;
    private List<City> neighbors = new ArrayList<>();
    private Vector3f location;
    private int population;

    public City(String name, ColorRGBA color, Vector3f location) {
        this.name = name;
        this.color = color;
        this.location = location;
    }

    public int getPopulation(){
        return population;
    }

    public void setPopulation(int amount){
        population = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }

    public ResearchStation getResearchStation() {
        return researchStation;
    }

    public void setResearchStation(ResearchStation researchStation) {
        this.researchStation = researchStation;
    }

    public List<Disease> getCubes() {
        return cubes;
    }

    /**
     * adds a disease cube to a city
     * @param cube disease cube to be added
     * @return true if successful, false if outbreak occurs
     */
    public boolean addCube(Disease cube) {
        if (this.cubes == null) {
            this.cubes = new ArrayList<>();
        } else if(this.cubes.size()>=3){
            //check outbreak
            int c=0;
            for (Disease d:this.cubes) {
                if(d.getColor() == cube.getColor())c++;
            }
            if(c>=3) return false;
        }

        this.cubes.add(cube);
        cube.setCity(this);
        return true;
    }

    public void setCubes(List<Disease> cubes) {
        this.cubes = cubes;
    }

    public List<Player> getPawns() {
        return pawns;
    }

    public void addPawn(Player pawn) {
        if (this.pawns == null) {
            this.pawns = new ArrayList<>();
        }

        this.pawns.add(pawn);
        pawn.setCity(this);
    }

    public void setPawns(List<Player> pawns) {
        this.pawns = pawns;
    }

    public List<City> getNeighbors() {
        return neighbors;
    }

    public void addNeighbour(City city) {
        if (this.neighbors == null) {
            this.neighbors = new ArrayList<>();
        }

        this.neighbors.add(city);

        if (!city.getNeighbors().contains(this)) {
            city.addNeighbour(this);
        }
    }

    public void setNeighbors(List<City> neighbors) {
        this.neighbors = neighbors;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public Vector3f getCubePosition(Disease disease) {
        int index = this.cubes.indexOf(disease);

        if (index == -1) {
            index = this.cubes.size() + 1;
        }

        float offsetX = 15;
        float offsetY = 10;

        for (int i = 1; i <= index; i++) {
            offsetX = offsetX + 5;
            offsetY = offsetY + 10;
        }

        return new Vector3f(-15 + offsetX, 15 - offsetY, 0);
    }

    public Vector3f getPawnPosition(Player player) {
        int index = this.pawns.indexOf(player);

        if (index == -1) {
            index = this.pawns.size() + 1;
        }

        float offsetX = 15;
        float offsetY = 10;

        for (int i = 1; i <= index; i++) {
            offsetX = offsetX + 5;
            offsetY = offsetY + 10;
        }

        return new Vector3f(5 + offsetX, 25 - offsetY, 0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("city-");
        sb.append(name);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        return Objects.equals(name, city.name);
    }

}
