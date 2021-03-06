package org.um.nine.headless.game.domain;

import org.um.nine.headless.game.Settings;

public class OutbreakMarker extends Marker {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutbreakMarker that = (OutbreakMarker) o;

        return color == that.color;
    }


    private static int INCREMENT = 0;
    private Color color;

    public OutbreakMarker(Color color) {
        super(INCREMENT);
        this.color = color;

        INCREMENT++;
        if (INCREMENT > Settings.MAX_OUTBREAKS) INCREMENT = 0;
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
    public OutbreakMarker clone() {
        OutbreakMarker clone = (OutbreakMarker) super.clone();
        clone.color = this.color;
        return clone;
    }

    @Override
    public String toString() {
        return "outbreak-marker-" +
                color.getName() +
                "-" +
                this.getId();
    }
}
