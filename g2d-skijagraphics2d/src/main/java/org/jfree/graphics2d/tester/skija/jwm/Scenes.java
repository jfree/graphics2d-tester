package org.jfree.graphics2d.tester.skija.jwm;

import io.github.humbleui.skija.Canvas;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeMap;

public class Scenes {

    public static final TreeMap<String, Scene> scenes;
    public static String currentScene = "Empty";
    public static final HUD hud = new HUD();
    public static boolean stats = true;

    static {
        scenes = new TreeMap<>(Comparator.comparing(s -> s.toLowerCase()));
        scenes.put("Empty", null);
        scenes.put("SkijaGraphics2DTest", null);
        setScene(currentScene);
    }

    public static Scene newScene(String name) {
        String className = "org.jfree.graphics2d.tester.skija.jwm." + name.replaceAll(" ", "") + "Scene";
        try {
            @SuppressWarnings("unchecked")
            Class<Scene> cls = (Class<Scene>) Class.forName(className);
            return cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Scene nextScene() {
        return setScene(Optional.ofNullable(scenes.higherKey(currentScene)).orElse(scenes.firstKey()));
    }

    public static Scene prevScene() {
        return setScene(Optional.ofNullable(scenes.lowerKey(currentScene)).orElse(scenes.lastKey()));
    }

    public static Scene setScene(String scene) {
        currentScene = scene;
        if (!scenes.containsKey(scene)) {
            throw new IllegalArgumentException("Unknown scene: " + scene);
        }
        if (scenes.get(scene) == null) {
            scenes.put(scene, newScene(scene));
        }
        return scenes.get(scene);
    }

    public static Scene currentScene() {
        return scenes.get(currentScene);
    }

    public static void draw(Canvas canvas, int width, int height, float scale, int mouseX, int mouseY) {
        canvas.clear(0xFFFFFFFF);
        int layer = canvas.save();
        var scene = currentScene();
        if (scene.scale()) {
            canvas.scale(scale, scale);
        }
        scene.draw(canvas, width, height, scale, mouseX, mouseY);
        canvas.restoreToCount(layer);

        hud.tick();
        if (stats) {
            layer = canvas.save();
            canvas.scale(scale, scale);
            hud.draw(canvas, scene, width, height);
            canvas.restoreToCount(layer);
        } else {
            hud.log();
        }
    }

    private Scenes() {
        // no-op
    }
}
