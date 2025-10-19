package client.audio;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public final class SoundManager {
    public enum Sfx { GOAL, FOUL, JUMP, KICK }

    private static final Map<Sfx, Clip> clips = new EnumMap<>(Sfx.class);

    private SoundManager() {}

    public static void load() {
        load(Sfx.GOAL, "/client/sfx/goal.wav");
        load(Sfx.FOUL, "/client/sfx/foul.wav");
        load(Sfx.JUMP, "/client/sfx/jump.wav");
        load(Sfx.KICK, "/client/sfx/kick.wav");
    }

    private static void load(Sfx key, String path) {
        try {
            URL url = SoundManager.class.getResource(path);
            if (url == null) return;
            try (AudioInputStream in = AudioSystem.getAudioInputStream(url)) {
                Clip c = AudioSystem.getClip();
                c.open(in);
                clips.put(key, c);
            }
        } catch (Exception ignored) { }
    }

    public static void play(Sfx key) {
        Clip c = clips.get(key);
        if (c == null) return;
        if (c.isRunning()) c.stop();
        c.setFramePosition(0);
        c.start();
    }
}
