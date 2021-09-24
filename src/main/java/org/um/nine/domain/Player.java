package org.um.nine.domain;

import com.jme3.math.ColorRGBA;

public class Player {
    private String name;
    private Role role;
    private City location;
    private boolean isBot;

    public Player(String name, boolean isBot) {
        this.name = name;
        this.isBot = isBot;
        //TODO : this.location = Atlanta city
    }

    public Player(String name, City city, boolean isBot) {
        this(name,isBot);
        this.location = city;
        this.location.addPawn(this);
    }

    public Player(String name) {
        this.name = name;
        this.isBot = false;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return location;
    }

    public void setCity(City location) {
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("player-");
        sb.append(getCity().getName());
        sb.append("-");
        sb.append(getName());
        sb.append("-");
        sb.append(role.getName());

        return sb.toString();
    }
}
