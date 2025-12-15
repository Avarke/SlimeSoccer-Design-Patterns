package server.chain;

import server.SlimeSoccer;

public abstract class GameCheckHandler {
    protected GameCheckHandler nextHandler;

    public void setNext(GameCheckHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public void check(GameEventContext context, SlimeSoccer game) {
        if (!handle(context, game) && nextHandler != null) {
            nextHandler.check(context, game);
        }
    }

    protected abstract boolean handle(GameEventContext context, SlimeSoccer game);
}
