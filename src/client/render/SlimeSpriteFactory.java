package client.render;

import java.util.HashMap;
import java.util.Map;

public class SlimeSpriteFactory {
    private static final Map<Integer, SlimeSprite> cache = new HashMap<>();
    public static SlimeSprite getSprite(int radius) {
        return cache.computeIfAbsent(radius, SlimeSprite::new);
    }
}
