package org.um.nine.domain;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.cards.PlayerCard;

import java.util.ArrayList;

public class Player {
    private String name;
    private Role role;
    private City location;
    private boolean isBot;
    private ArrayList<PlayerCard> hand;

    public Player(String name, boolean isBot) {
        this.name = name;
        this.isBot = isBot;
        this.hand = new ArrayList<>();
        //TODO : this.location = Atlanta city
    }

    public Player(String name, City city, boolean isBot) {
        this(name,isBot);
        this.location = city;
        this.location.addPawn(this);
        this.hand = new ArrayList<>();
    }

    public Player(String name) {
        this.name = name;
        this.isBot = false;
        this.hand = new ArrayList<>();
    }

    public ArrayList<PlayerCard> getHandCards(){
        return hand;
    }

    public void setHandCards(ArrayList<PlayerCard> cards){
        hand = cards;
    }

    public void addCard(PlayerCard card){
        if(hand.size()<=7){
            hand.add(card);
        }else{
            //todo add choice for discard
            discard(card);
        }
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
