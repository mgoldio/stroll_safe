package edu.illinois.strollsafe.util;

import java.util.Arrays;

/**
 * @author Michael Goldstein
 */
public class Vector {

    private float[] values = new float[3];

    public Vector(float[] d) {
        values[0] = d[0];
        values[1] = d[1];
        values[2] = d[2];
    }

    public void minusGravity(float[] gravity) {
        values[0] -= gravity[0];
        values[1] -= gravity[1];
        values[2] -= gravity[2];
    }

    public float getMagnitude() {
        return (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
    }

    public float[] getDirection() {
        float magnitude = getMagnitude();
        if (magnitude == 0)
            return new float[]{0, 0, 0};
        return new float[]{values[0] / magnitude, values[1] / magnitude, values[2] / magnitude};
    }

    public boolean pointsAwayFrom(Vector other) {
        float[] d1 = getDirection();
        float[] d2 = other.getDirection();
        double dot = d1[0] * d2[0] + d1[1] * d2[1] + d1[2] * d2[2];
        return dot > 0.78;
    }

    public float[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }
}
