package org.um.nine.headless.game.domain;

public class OutbreakMarker extends Marker {
    private static int INCREMENT = 0;
    private Color color;

    public OutbreakMarker(Color color) {
        super(INCREMENT);
        this.color = color;

        INCREMENT++;
    }

    public OutbreakMarker(Color color, boolean isCurrent) {
        super(INCREMENT, isCurrent);
        this.color = color;

        INCREMENT++;
    }

    public Color getDiseaseType() {
        return color;
    }

    public void setDiseaseType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "outbreak-marker-" +
                color.getName() +
                "-" +
                this.getId();
    }
}
