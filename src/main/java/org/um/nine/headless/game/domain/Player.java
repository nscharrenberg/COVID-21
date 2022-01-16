package org.um.nine.headless.game.domain;

import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class Player implements Cloneable {
    private static int INCREMENT = 0;
    private int id;
    private String name;
    private Role role;
    private City city;
    private boolean isBot;
    private List<PlayerCard> hand;

    public Player clone() {
        Player other = new Player(this.name, this.getCity(), this.isBot);
        other.setId(this.id);
        other.setRole(this.role);
        other.city = this.getCity();
        other.setHand(this.getHand().stream().map(PlayerCard::clone).peek(card -> card.setPlayer(this)).collect(Collectors.toList()));
        return other;
    }

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

    public void setCityField(City cityField) {
        this.city = cityField;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return id == player.id &&
                isBot == player.isBot &&
                Objects.equals(name, player.name) &&
                Objects.equals(role, player.role) &&
                Objects.equals(city, player.city) &&
                Objects.equals(hand, player.hand);
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
        return name.trim().toLowerCase(Locale.ROOT) + "-" + role.getName();
    }
}
