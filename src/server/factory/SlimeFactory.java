package server.factory;

import java.awt.Color;
import server.Slime;

public interface SlimeFactory {
    Slime createSlime(double x, double y, Color color, boolean isLeft);
    Slime createNormal(double x, double y, Color color, boolean isLeft);
    Slime createHeavy(double x, double y, Color color, boolean isLeft);
    Slime createLight(double x, double y, Color color, boolean isLeft);
    Slime createFast(double x, double y, Color color, boolean isLeft);
    Slime createInvisible(double x, double y, Color color, boolean isLeft);
}
