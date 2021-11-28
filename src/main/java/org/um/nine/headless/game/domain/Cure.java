package org.um.nine.headless.game.domain;

public class Cure {
    private static int INCREMENT = 0;
    private int id;
    private Color color;
    private boolean discovered;

    public Cure(Color color) {
        this.id = INCREMENT;
        this.color = color;
        this.discovered = false;

        INCREMENT++;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    @Override
    public String toString() {
        return "CureMacro{" +
                "id=" + id +
                ", color=" + color.getName() +
                ", discovered=" + discovered +
                '}';
    }
}
