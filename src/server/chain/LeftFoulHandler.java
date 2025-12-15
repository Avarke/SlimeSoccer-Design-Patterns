package server.chain;

import server.SlimeSoccer;

public class LeftFoulHandler extends GameCheckHandler {
    @Override
    protected boolean handle(GameEventContext context, SlimeSoccer game) {
        if (context.getLeftErrorBar().getWidth() < 1) {
            game.handleFoul(true);
            return true;

        }
        return false;
    }
}
