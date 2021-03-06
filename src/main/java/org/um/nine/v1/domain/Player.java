package org.um.nine.v1.domain;

import org.um.nine.v1.domain.cards.PlayerCard;

import java.util.ArrayList;

public class Player {
    private String name;
    private Role role;
    private City location;
    private boolean isBot;
    private boolean itsTurn;
    private ArrayList<PlayerCard> hand;
    private RoundState currentState;


    public Player(String name, boolean isBot) {
        this(name);
        this.isBot = isBot;
    }

    public Player(String name, City city, boolean isBot) {
        this(name,isBot);
        this.location = city;
        this.location.addPawn(this);
    }

    public Player(String name) {
        this.name = name;
        this.isBot = false;
        this.hand = new ArrayList<>();
        this.itsTurn = false;
    }

    public ArrayList<PlayerCard> getHandCards(){
        return hand;
    }

    public void setHandCards(ArrayList<PlayerCard> cards){
        hand = cards;
    }

    public void addCard(PlayerCard card){
        hand.add(card);
    }

    public void discard(PlayerCard card){
        hand.remove(card);
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

    public boolean isItsTurn() {
        return itsTurn;
    }

    public void setItsTurn(boolean itsTurn) {
        this.itsTurn = itsTurn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(isBot)
            sb.append("bot-");
        else
            sb.append("player-");
        sb.append(getName());
        sb.append("-");
        sb.append(role.getName());

        return sb.toString();
    }


}
