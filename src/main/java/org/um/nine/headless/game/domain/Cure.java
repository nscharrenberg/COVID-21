package org.um.nine.headless.game.domain;

public class Cure implements Cloneable {
    private static int INCREMENT = 0;
    private int id;
    private Color color;
    private boolean discovered;

    @Override
    public Cure clone() {
        try {
            Cure clone = (Cure) super.clone();
            clone.id = this.id;
            clone.color = this.color;
            clone.discovered = this.discovered;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cure(Color color) {
        this.id = INCREMENT;
        this.color = color;
        this.discovered = false;

        INCREMENT = INCREMENT > 3 ? 0 : INCREMENT + 1;
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
