package server;

public class KeyboardWindowAdapter implements PlayerInput {

    @Override
    public boolean left(int p) {
        return switch (p) {
            case 1 -> Window.playerOneLeft;
            case 2 -> Window.playerTwoLeft;
            case 3 -> Window.playerThreeLeft;
            case 4 -> Window.playerFourLeft;
            default -> false;
        };
    }

    @Override
    public boolean right(int p) {
        return switch (p) {
            case 1 -> Window.playerOneRight;
            case 2 -> Window.playerTwoRight;
            case 3 -> Window.playerThreeRight;
            case 4 -> Window.playerFourRight;
            default -> false;
        };
    }

    @Override
    public boolean jump(int p) {
        return switch (p) {
            case 1 -> Window.playerOneJump;
            case 2 -> Window.playerTwoJump;
            case 3 -> Window.playerThreeJump;
            case 4 -> Window.playerFourJump;
            default -> false;
        };
    }

    @Override
    public boolean reset() {
        return Window.reset;
    }
}
