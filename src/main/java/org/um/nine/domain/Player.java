package org.um.nine.domain;

public class Player {
    private String name;
    private Role role;
    private boolean isBot;

    public Player(String name, boolean isBot) {
        this.name = name;
        this.isBot = isBot;
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
}
