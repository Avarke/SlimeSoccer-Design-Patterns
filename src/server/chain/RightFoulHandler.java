package server.chain;

import server.SlimeSoccer;

public class RightFoulHandler extends GameCheckHandler {
    @Override
    protected boolean handle(GameEventContext context, SlimeSoccer game) {
        if (context.getRightErrorBar().getWidth() < 1) {
            game.handleFoul(false);
            return true;
        }
        return false;
    }
}
