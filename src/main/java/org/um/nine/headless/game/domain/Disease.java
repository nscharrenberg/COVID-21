package org.um.nine.headless.game.domain;

public class Disease implements Cloneable {
    private static int INCREMENT = 0;
    private int id;
    private Color color;
    private City city;

    public Disease clone() {
        Disease other;
        try {
            other = (Disease) super.clone();
            other.color = this.color;
            other.id = this.id;
            other.city = this.city;
            return other;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Disease(Color color) {
        this.color = color;
        this.id = INCREMENT;

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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Disease{" +
                "id=" + id +
                ", color=" + color.getName() +
                ", city=" + city.getName() + "-" + city.getId() +
                '}';
    }
}
