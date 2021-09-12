package org.um.nine.domain;

public class Player {
    private String name;
    private boolean isBot = false;

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, boolean isBot) {
        this.name = name;
        this.isBot = isBot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }
}
