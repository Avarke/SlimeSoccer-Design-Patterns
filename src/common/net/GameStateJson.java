package common.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Serialises and deserialises game-state snapshots for TCP transport using a
 * compact JSON format.
 */
public final class GameStateJson {

    private GameStateJson() {
    }

    public static String encode(State state) {
        if (state == null) {
            throw new IllegalArgumentException("state == null");
        }
        StringBuilder builder = new StringBuilder(256);
        builder.append("{\"p\":[");
        for (int i = 0; i < state.players.length; i++) {
            PlayerState p = state.players[i];
            if (i > 0)
                builder.append(',');
            builder.append('[')
                    .append(trimDouble(p.x)).append(',')
                    .append(trimDouble(p.y)).append(',')
                    .append(p.facingRight).append(',')
                    .append(p.color).append(',')
                    .append(trimDouble(p.stamina)).append(',')
                    .append('"').append(escapeString(p.nickname)).append('"')
                    .append(']');
        }
        builder.append("],\"b\":[")
                .append(trimDouble(state.ballX)).append(',')
                .append(trimDouble(state.ballY)).append(',')
                .append(state.effectCode)
                .append("],\"bars\":[")
                .append(trimDouble(state.leftBarWidth)).append(',')
                .append(trimDouble(state.rightBarWidth)).append(',')
                .append(trimDouble(state.rightBarX))
                .append("],\"scores\":[")
                .append(state.leftScore).append(',')
                .append(state.rightScore)
                .append("],\"goal\":")
                .append(state.goalScored)
                .append(",\"foul\":")
                .append(state.foul)
                .append(",\"phase\":\"")
                .append(state.matchPhase != null ? state.matchPhase : "")
                .append("\",\"powerUps\":[");
        for (int i = 0; i < state.powerUps.size(); i++) {
            PowerUpState p = state.powerUps.get(i);
            if (i > 0)
                builder.append(',');
            builder.append('[')
                    .append(trimDouble(p.x)).append(',')
                    .append(trimDouble(p.y)).append(',')
                    .append(trimDouble(p.radius)).append(',')
                    .append(p.color)
                    .append(']');
        }
        builder.append("]}");
        return builder.toString();
    }

    public static State decode(String json) {
        if (json == null) {
            throw new IllegalArgumentException("json == null");
        }
        String trimmed = json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON payload: " + json);
        }

        PlayerState[] players = parsePlayers(trimmed);
        double[] ball = parseNumberArray(trimmed, "\"b\":[", "],\"bars\"");
        double[] bars = parseNumberArray(trimmed, "\"bars\":[", "],\"scores\"");
        double[] scores = parseNumberArray(trimmed, "\"scores\":[", "],\"goal\"");
        boolean goal = parseBoolean(trimmed, "\"goal\":", ",\"foul\"");
        boolean foul = parseBoolean(trimmed, "\"foul\":", ",\"phase\"");
        String matchPhase = parseString(trimmed, "\"phase\":\"", "\",\"powerUps\"");
        List<PowerUpState> powerUps = parsePowerUps(trimmed);

        return new State(
                players,
                ball.length > 0 ? ball[0] : 0.0,
                ball.length > 1 ? ball[1] : 0.0,
                ball.length > 2 ? (int) ball[2] : 0,
                bars.length > 0 ? bars[0] : 0.0,
                bars.length > 1 ? bars[1] : 0.0,
                bars.length > 2 ? bars[2] : 0.0,
                scores.length > 0 ? (int) scores[0] : 0,
                scores.length > 1 ? (int) scores[1] : 0,
                goal,
                foul,
                matchPhase,
                powerUps);
    }

    private static PlayerState[] parsePlayers(String json) {
        String segment = slice(json, "\"p\":[", "],\"b\"");
        if (segment.isEmpty()) {
            return new PlayerState[0];
        }
        List<PlayerState> result = new ArrayList<>();
        int idx = 0;
        while (idx < segment.length()) {
            int start = segment.indexOf('[', idx);
            if (start == -1)
                break;
            int end = findClosingBracket(segment, start);
            if (end == -1)
                break;
            String body = segment.substring(start + 1, end);
            String[] parts = body.split(",");
            if (parts.length >= 6) {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                boolean facing = Boolean.parseBoolean(parts[2]);
                int color = Integer.parseInt(parts[3]);
                double stamina = Double.parseDouble(parts[4]);
                String rawName = parts[5].trim(); // e.g. "\"Nick\""
                String nickname = unquoteAndUnescape(rawName);
                result.add(new PlayerState(x, y, facing, color, stamina, nickname));
            }
            idx = end + 1;
        }
        return result.toArray(new PlayerState[0]);
    }

    private static double[] parseNumberArray(String json, String startToken, String endToken) {
        String segment = slice(json, startToken, endToken);
        if (segment.isEmpty()) {
            return new double[0];
        }
        String[] parts = segment.split(",");
        double[] numbers = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            numbers[i] = Double.parseDouble(parts[i]);
        }
        return numbers;
    }

    private static boolean parseBoolean(String json, String startToken, String endToken) {
        String value = slice(json, startToken, endToken);
        if (value.isEmpty()) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    private static String parseString(String json, String startToken, String endToken) {
        return slice(json, startToken, endToken);
    }

    private static List<PowerUpState> parsePowerUps(String json) {
        int start = json.indexOf("\"powerUps\":[");
        if (start == -1) {
            return Collections.emptyList();
        }
        start += "\"powerUps\":[".length();
        int end = json.lastIndexOf(']');
        if (end == -1 || end < start) {
            return Collections.emptyList();
        }
        String segment = json.substring(start, end);
        if (segment.isEmpty()) {
            return Collections.emptyList();
        }
        List<PowerUpState> result = new ArrayList<>();
        int idx = 0;
        while (idx < segment.length()) {
            int open = segment.indexOf('[', idx);
            if (open == -1)
                break;
            int close = findClosingBracket(segment, open);
            if (close == -1)
                break;
            String body = segment.substring(open + 1, close);
            String[] parts = body.split(",");
            if (parts.length >= 4) {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double r = Double.parseDouble(parts[2]);
                int color = Integer.parseInt(parts[3]);
                result.add(new PowerUpState(x, y, r, color));
            }
            idx = close + 1;
        }
        return result;
    }

    private static String slice(String source, String startToken, String endToken) {
        int start = source.indexOf(startToken);
        if (start == -1) {
            return "";
        }
        start += startToken.length();
        int end = source.indexOf(endToken, start);
        if (end == -1) {
            return "";
        }
        return source.substring(start, end);
    }

    private static String escapeString(String s) {
        if (s == null) return "";
        // very minimal escaping: backslash and double quote
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unquoteAndUnescape(String s) {
        if (s == null || s.isEmpty()) return "";
        // remove surrounding quotes if present
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            s = s.substring(1, s.length() - 1);
        }
        // reverse our escapeString
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static int findClosingBracket(String text, int openIndex) {
        int depth = 0;
        for (int i = openIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '[')
                depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static String trimDouble(double value) {
        if (value == (long) value) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    public static final class State {
        public final PlayerState[] players;
        public final double ballX;
        public final double ballY;
        public final int effectCode;
        public final double leftBarWidth;
        public final double rightBarWidth;
        public final double rightBarX;
        public final int leftScore;
        public final int rightScore;
        public final boolean goalScored;
        public final boolean foul;
        public final String matchPhase;
        public final List<PowerUpState> powerUps;

        public State(PlayerState[] players,
                double ballX,
                double ballY,
                int effectCode,
                double leftBarWidth,
                double rightBarWidth,
                double rightBarX,
                int leftScore,
                int rightScore,
                boolean goalScored,
                boolean foul,
                String matchPhase,
                List<PowerUpState> powerUps) {
            this.players = players;
            this.ballX = ballX;
            this.ballY = ballY;
            this.effectCode = effectCode;
            this.leftBarWidth = leftBarWidth;
            this.rightBarWidth = rightBarWidth;
            this.rightBarX = rightBarX;
            this.leftScore = leftScore;
            this.rightScore = rightScore;
            this.goalScored = goalScored;
            this.foul = foul;
            this.matchPhase = matchPhase != null ? matchPhase : "";
            this.powerUps = powerUps != null ? powerUps : Collections.emptyList();
        }
    }

    public static final class PlayerState {
        public final double x;
        public final double y;
        public final boolean facingRight;
        public final int color;
        public final double stamina;
        public final String nickname;

        public PlayerState(double x, double y, boolean facingRight, int color, double stamina, String nickname) {
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
            this.color = color;
            this.stamina = stamina;
            this.nickname = nickname;
        }
    }

    public static final class PowerUpState {
        public final double x;
        public final double y;
        public final double radius;
        public final int color;

        public PowerUpState(double x, double y, double radius, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }
    }
}
