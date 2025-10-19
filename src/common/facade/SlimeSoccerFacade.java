package common.facade;

import common.GameConfiguration;

public final class SlimeSoccerFacade {
    private SlimeSoccerFacade() { }

    public static GameConfiguration.Builder configurationBuilder() {
        return GameConfiguration.builder();
    }

    public static client.SlimeSoccer launchClient() {
        return new client.SlimeSoccer();
    }

    public static client.SlimeSoccer launchClient(GameConfiguration configuration) {
        return new client.SlimeSoccer(configuration);
    }

    public static server.SlimeSoccer launchServer() {
        return new server.SlimeSoccer();
    }

    public static server.SlimeSoccer launchServer(GameConfiguration configuration) {
        return new server.SlimeSoccer(configuration);
    }
}
