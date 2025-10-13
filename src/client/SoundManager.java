package client;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final Map<String, Clip> cache = new HashMap<>();
    private static String basePath = "resources/sfx";

    public static void setBasePath(String path) { basePath = path; }

    public static synchronized void play(String name) {
        try {
            Clip clip = cache.get(name);
            if (clip == null) {
                File f = new File(basePath, name);
                AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                AudioFormat base = ais.getFormat();
                // Ensure PCM
                AudioFormat decoded = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        base.getSampleRate(),
                        16,
                        base.getChannels(),
                        base.getChannels() * 2,
                        base.getSampleRate(),
                        false
                );
                AudioInputStream dais = AudioSystem.getAudioInputStream(decoded, ais);
                clip = AudioSystem.getClip();
                clip.open(dais);
                cache.put(name, clip);
            }
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
