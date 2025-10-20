package server.factory;

import java.awt.Color;

/*
Factory for creating Slime instances.
*/
import server.Slime;

public class SlimeFactory {

    private SlimeFactory() {}

    public static Slime createSlime(double x, double y, Color color, boolean isLeft) {
        return new Slime(x, y, color, isLeft);
    }
}
