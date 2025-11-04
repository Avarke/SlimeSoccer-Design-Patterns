package server.builder;

import server.Ball;

public final class BallDirector {
    public Ball construct(BallBuilder builder, double x, double y) {
        return builder.at(x, y).build();
    }
}
