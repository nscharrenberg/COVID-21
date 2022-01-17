package org.um.nine.headless.game.domain;

public class Card implements Cloneable {
    private static int INCREMENT = 0;
    private int id;
    private String name;

    public Card clone() {
        Card other = null;
        try {
            other = (Card) super.clone();
            other.name = this.name;
            other.id = this.id;
            return other;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Card(String name) {
        this.name = name;
        this.id = INCREMENT;

        INCREMENT = INCREMENT > 105 ? 0 : INCREMENT + 1;
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

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
