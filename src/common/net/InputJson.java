package common.net;

/**
 * Utility for serialising and deserialising input state messages exchanged over TCP.
 */
public final class InputJson {

    private InputJson() { }

    public static String encode(boolean jump, boolean left, boolean right) {
        // Produce a compact JSON object without whitespace to simplify parsing.
        return "{\"jump\":" + jump + ",\"left\":" + left + ",\"right\":" + right + '}';
    }

    public static InputState decode(String json) {
        if (json == null) {
            throw new IllegalArgumentException("json == null");
        }
        boolean jump = parseBoolean(json, "jump");
        boolean left = parseBoolean(json, "left");
        boolean right = parseBoolean(json, "right");
        return new InputState(jump, left, right);
    }

    private static boolean parseBoolean(String json, String key) {
        int idx = json.indexOf('"' + key + '"');
        if (idx == -1) {
            return false;
        }
        int colon = json.indexOf(':', idx);
        if (colon == -1) {
            return false;
        }
        int pos = colon + 1;
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
        if (pos >= json.length()) {
            return false;
        }
        if (json.startsWith("true", pos)) {
            return true;
        }
        if (json.startsWith("false", pos)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid boolean for key \"" + key + "\" in json: " + json);
    }

    public static final class InputState {
        public final boolean jump;
        public final boolean left;
        public final boolean right;

        public InputState(boolean jump, boolean left, boolean right) {
            this.jump = jump;
            this.left = left;
            this.right = right;
        }
    }
}

