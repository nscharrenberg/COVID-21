package org.um.nine.domain;

import com.jme3.math.ColorRGBA;

public class Disease {
    private ColorRGBA color;
    private City city;
    private int number;
    private static int cubeNumber;

    public Disease(ColorRGBA color) {
        cubeNumber++;
        if(cubeNumber>=96) {
            System.out.println();
        }
        this.color = color;
        number = cubeNumber;
        System.out.println("cubeNumber: "+cubeNumber);
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
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
        StringBuilder sb = new StringBuilder();
        sb.append("disease-");
        sb.append(city.getName());
        sb.append("-");
        sb.append(this.getColor().toString());
        sb.append("-");
        sb.append(number);
        System.out.println(cubeNumber + " " + number + " from tostring");
        return sb.toString();
    }
}
