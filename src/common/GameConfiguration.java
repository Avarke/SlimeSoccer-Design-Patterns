package common;

/**
 * Immutable configuration for client/server launch parameters.
 */
public final class GameConfiguration {
    private final String host;
    private final int port;
    private final long autoResetDelayMs;

    private GameConfiguration(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.autoResetDelayMs = builder.autoResetDelayMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public long getAutoResetDelayMs() {
        return autoResetDelayMs;
    }

    public static final class Builder {
        private String host = "localhost";
        private int port = 6969;
        private long autoResetDelayMs = 2500;

        public Builder withHost(String host) {
            if (host != null && !host.trim().isEmpty()) {
                this.host = host.trim();
            }
            return this;
        }

        public Builder withPort(int port) {
            if (port > 0 && port <= 65535) {
                this.port = port;
            }
            return this;
        }

        public Builder withAutoResetDelayMs(long delayMs) {
            if (delayMs > 0) {
                this.autoResetDelayMs = delayMs;
            }
            return this;
        }

        public GameConfiguration build() {
            return new GameConfiguration(this);
        }
    }
}
