package org.jfree.graphics2d.tester.skija.jwm;

import io.github.humbleui.jwm.App;
import io.github.humbleui.jwm.Event;
import io.github.humbleui.jwm.EventKey;
import io.github.humbleui.jwm.EventMouseMove;
import io.github.humbleui.jwm.EventMouseScroll;
import io.github.humbleui.jwm.EventWindowCloseRequest;
import io.github.humbleui.jwm.EventWindowResize;
import io.github.humbleui.jwm.EventWindowScreenChange;
import static io.github.humbleui.jwm.Key.G;
import static io.github.humbleui.jwm.Key.L;
import static io.github.humbleui.jwm.Key.S;
import io.github.humbleui.jwm.Layer;
import io.github.humbleui.jwm.Window;
import io.github.humbleui.jwm.skija.EventFrameSkija;
import io.github.humbleui.skija.Canvas;
import static io.github.humbleui.skija.impl.Platform.LINUX;
import static io.github.humbleui.skija.impl.Platform.WINDOWS;
import io.github.humbleui.skija.impl.Stats;
import java.util.function.Consumer;
import org.jfree.graphics2d.Tester;

/**
 *
 * @author bourgesl
 */
public class SkijaGraphics2DWithJWMRunner implements Consumer<Event> {

    public Window _window;
    public Layer _layer;
    public int _xpos = 720, _ypos = 405, _width = 0, _height = 0;
    public float _scale = 1;

    public String[] _layers = null;
    public int _layerIdx = 0;

    public SkijaGraphics2DWithJWMRunner() {
        Stats.enabled = true;

        switch (io.github.humbleui.skija.impl.Platform.CURRENT) {
            case MACOS_X64:
            case MACOS_ARM64:
                _layers = new String[]{"Metal", "GL"};
                break;
            case WINDOWS:
                _layers = new String[]{"D3D12", "GL", "Raster"};
                break;
            case LINUX:
                _layers = new String[]{"GL", "Raster"};
                break;
        }

        _window = App.makeWindow();
        _window.setEventListener(this);
        changeLayer();

        final int width = Tester.getTestSheetWidth();
        final int height = Tester.getTestSheetHeight();

        var scale = 1.0; // _window.getScreen().getScale();
        System.out.println("scale: " + scale);

        _window.setWindowSize((int) (width * scale), (int) (height * scale));
        _window.setWindowPosition(0, 0);
        _window.setVisible(true);
        _window.accept(EventWindowScreenChange.INSTANCE);
    }

    public void paint(Canvas canvas) {
        Scenes.draw(canvas, _width, _height, _scale, Math.max(0, Math.min(_xpos, _width)), Math.max(0, Math.min(_ypos, _height)));
    }

    public void changeLayer() {
        if (HUD.extras.size() < 1) {
            HUD.extras.add(null);
        }

        String layerName = _layers[_layerIdx];
        String className = "io.github.humbleui.jwm.skija.Layer" + layerName + "Skija";

        try {
            _layer = (Layer) SkijaGraphics2DWithJWMRunner.class.forName(className).getDeclaredConstructor().newInstance();
            HUD.extras.set(0, new Pair<>("L", "Layer: " + layerName));
            _window.setLayer(_layer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(Event e) {
        if (e instanceof EventWindowScreenChange) {
            _scale = 1.0f; // _window.getScreen().getScale();
        } else if (e instanceof EventWindowResize er) {
            _width = (int) (er.getContentWidth() / _scale);
            _height = (int) (er.getContentHeight() / _scale);
        } else if (e instanceof EventMouseMove) {
            _xpos = (int) (((EventMouseMove) e).getX() / _scale);
            _ypos = (int) (((EventMouseMove) e).getY() / _scale);
        } else if (e instanceof EventMouseScroll ee) {
            Scenes.currentScene().onScroll(ee.getDeltaX() / _scale, ee.getDeltaY() / _scale);
        } else if (e instanceof EventKey eventKey) {
            if (eventKey.isPressed() == true) {
                switch (eventKey.getKey()) {
                    case S -> {
                        Stats.enabled = !Stats.enabled;
                    }
                    case G -> {
                        System.out.println("Before GC " + Stats.allocated);
                        System.gc();
                    }
                    case L -> {
                        _layerIdx = (_layerIdx + _layers.length - 1) % _layers.length;
                        changeLayer();
                    }
                    case LEFT ->
                        Scenes.prevScene();

                    case RIGHT ->
                        Scenes.nextScene();

                    case DOWN ->
                        Scenes.currentScene().changeVariant(1);

                    case UP ->
                        Scenes.currentScene().changeVariant(-1);

                    default ->
                        System.out.println("Key pressed: " + eventKey.getKey());
                }
            }
        } else if (e instanceof EventFrameSkija ee) {
            paint(ee.getSurface().getCanvas());
            _window.requestFrame();
        } else if (e instanceof EventWindowCloseRequest) {
            _window.close();
            App.terminate();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        App.start(() -> new SkijaGraphics2DWithJWMRunner());
    }
}
