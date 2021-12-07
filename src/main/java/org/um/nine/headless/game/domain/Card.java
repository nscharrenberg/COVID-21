package org.um.nine.headless.game.domain;

public class Card {
    private static int INCREMENT = 0;
    private int id;
    private String name;

    public Card(String name) {
        this.name = name;
        this.id = INCREMENT;

        INCREMENT = INCREMENT >105?0 : INCREMENT+1;
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
