package server;

public class NetworkPacketAdapter implements PlayerInput {
    private final InputState state;

    public NetworkPacketAdapter(InputState state) {
        this.state = state;
    }

    @Override
    public boolean left(int p) {
        return switch (p) {
            case 1 -> state.p1L;
            case 2 -> state.p2L;
            case 3 -> state.p3L;
            case 4 -> state.p4L;
            default -> false;
        };
    }

    @Override
    public boolean right(int p) {
        return switch (p) {
            case 1 -> state.p1R;
            case 2 -> state.p2R;
            case 3 -> state.p3R;
            case 4 -> state.p4R;
            default -> false;
        };
    }

    @Override
    public boolean jump(int p) {
        return switch (p) {
            case 1 -> state.p1J;
            case 2 -> state.p2J;
            case 3 -> state.p3J;
            case 4 -> state.p4J;
            default -> false;
        };
    }

    @Override
    public boolean reset() {
        return state.reset;
    }
}
