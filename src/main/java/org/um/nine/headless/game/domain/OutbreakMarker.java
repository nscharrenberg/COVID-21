package org.um.nine.headless.game.domain;

public class OutbreakMarker extends Marker {
    private static int INCREMENT = 0;
    private Color color;

    public OutbreakMarker(int id, Color color) {
        super(id);
        this.color = color;

        INCREMENT++;
    }

    public OutbreakMarker(int id, boolean isCurrent, Color color) {
        super(id, isCurrent);
        this.color = color;

        INCREMENT++;
    }

    public Color getDiseaseType() {
        return color;
    }

    public void setDiseaseType(Color color) {
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
