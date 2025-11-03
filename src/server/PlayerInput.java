package server;

public interface PlayerInput {
    boolean left(int player);
    boolean right(int player);
    boolean jump(int player);
    boolean reset();
}
