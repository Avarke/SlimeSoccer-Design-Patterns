package server;

public class InputState {
    public volatile boolean p1L, p1R, p1J;
    public volatile boolean p2L, p2R, p2J;
    public volatile boolean p3L, p3R, p3J;
    public volatile boolean p4L, p4R, p4J;
    public volatile boolean reset;

    public void set(int player, boolean left, boolean right, boolean jump) {
        switch (player) {
            case 1 -> { p1L = left; p1R = right; p1J = jump; }
            case 2 -> { p2L = left; p2R = right; p2J = jump; }
            case 3 -> { p3L = left; p3R = right; p3J = jump; }
            case 4 -> { p4L = left; p4R = right; p4J = jump; }
        }
    }
}
