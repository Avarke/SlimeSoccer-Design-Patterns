package server;

import java.util.Random;

public class AIAdapter implements PlayerInput {
    private static class AIState { int moveTimer, moveDir, jumpTimer; }
    private final int controlledPlayer;
    private final Random rnd = new Random();
    private final AIState s = new AIState();

    public AIAdapter(int controlledPlayer) { this.controlledPlayer = controlledPlayer; }

    @Override public boolean left(int p)  { if (p!=controlledPlayer) return false; update(); return s.moveDir==-1; }
    @Override public boolean right(int p) { if (p!=controlledPlayer) return false; update(); return s.moveDir==1; }
    @Override public boolean jump(int p)  {
        if (p!=controlledPlayer) return false;
        if (s.jumpTimer>0) { s.jumpTimer--; return true; }
        if (rnd.nextDouble()<0.01) { s.jumpTimer=10; return true; }
        return false;
    }
    @Override public boolean reset() { return false; }

    private void update() {
        if (s.moveTimer>0) { s.moveTimer--; return; }
        if (rnd.nextDouble()<0.02) { s.moveDir = rnd.nextInt(3)-1; s.moveTimer = rnd.nextInt(20)+10; }
        else s.moveDir = 0;
    }
}
