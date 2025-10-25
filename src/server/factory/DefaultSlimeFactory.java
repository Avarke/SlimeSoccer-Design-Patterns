package server.factory;

import java.awt.Color;
import server.Slime;

public class DefaultSlimeFactory implements SlimeFactory {

    @Override
    public Slime createSlime(double x, double y, Color color, boolean isLeft) {
        return new NormalSlime(x, y, color, isLeft);
    }

    @Override
    public Slime createNormal(double x, double y, Color color, boolean isLeft) {
        return new NormalSlime(x, y, color, isLeft);
    }

    @Override
    public Slime createHeavy(double x, double y, Color color, boolean isLeft) {
        return new HeavySlime(x, y, color, isLeft);
    }

    @Override
    public Slime createLight(double x, double y, Color color, boolean isLeft) {
        return new LightSlime(x, y, color, isLeft);
    }

    @Override
    public Slime createFast(double x, double y, Color color, boolean isLeft) {
        return new FastSlime(x, y, color, isLeft);
    }

    @Override
    public Slime createInvisible(double x, double y, Color color, boolean isLeft) {
        return new InvisibleSlime(x, y, color, isLeft);
    }
}
