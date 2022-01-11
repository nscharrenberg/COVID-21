package org.um.nine.headless.game.domain;

import org.um.nine.headless.agents.Agent;
import org.um.nine.headless.agents.mcts.MCTS;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private static int INCREMENT = 0;
    private int id;
    private String name;
    private Role role;
    private City city;
    private boolean isBot;
    private List<PlayerCard> hand;
    private Agent agent;

    public Player(String name, boolean isBot) {
        this.id = INCREMENT;
        this.name = name;
        this.isBot = isBot;
        this.hand = new ArrayList<>();
        INCREMENT++;
    }

    public Player(String name, City city, boolean isBot) {
        this(name, isBot);
        city.addPawn(this);
    }

    public Player(String name) {
        this(name, false);
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        if (this.city != null) {
            this.city.getPawns().remove(this);
        }

        this.city = city;

        if (!this.city.getPawns().contains(this)) {
            this.city.addPawn(this);
        }
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public List<PlayerCard> getHand() {
        return hand;
    }

    public void setHand(List<PlayerCard> hand) {
        this.hand = hand;
    }

    public void addHand(PlayerCard card) {
        if (this.hand == null) {
            this.hand = new ArrayList<>();
        }

        this.hand.add(card);
        card.setPlayer(this);
    }

    public void discard(PlayerCard card) {
        this.hand.remove(card);
        card.setPlayer(null);
    }

    @Override
    public String toString() {
        return (isBot ? "bot-" : "player-") +
                name +
                "-" +
                role.getColor().getName();
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
