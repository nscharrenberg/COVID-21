package org.um.nine.domain;

import com.jme3.math.ColorRGBA;

import java.util.ArrayList;
import java.util.List;

public class City {
    private String name;
    private ColorRGBA color;
    private List<Disease> cubes;
    private List<Player> pawns;
    private ResearchStation researchStation;
    private List<City> neighbors;

    public City(String name, ColorRGBA color) {
        this.name = name;
        this.color = color;
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

    public void addCube(Disease cube) {
        if (this.cubes == null) {
            this.cubes = new ArrayList<>();
        }

        // TODO: Check if the disease threshold is reached, and start an outbreak.

        this.cubes.add(cube);
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
}
