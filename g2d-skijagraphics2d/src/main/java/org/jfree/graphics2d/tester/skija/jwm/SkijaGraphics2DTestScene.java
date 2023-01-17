package org.jfree.graphics2d.tester.skija.jwm;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.EncodedImageFormat;
import io.github.humbleui.skija.Surface;
import java.io.IOException;
import org.jfree.graphics2d.Tester;
import org.jfree.skija.SkijaGraphics2D;

public class SkijaGraphics2DTestScene extends Scene {

    private final boolean single = false;

    private final Tester.TesterContext tc;
    private boolean saveFirst = true;

    /**
     * Public constructor used by introspection
     */
    public SkijaGraphics2DTestScene() {
        // Prepare context:
        this.tc = Tester.prepareTestOutput("SkijaGraphics2D 1.0.5", single);
    }

    @Override
    public void draw(final Canvas canvas, int width, int height, float dpi, int xpos, int ypos) {
        String fileName = "SkijaGraphics2D";

        final long startTime = System.nanoTime();

        final SkijaGraphics2D g2 = new SkijaGraphics2D(canvas);
        try {
            Tester.drawTestOutput(tc, g2, "https://github.com/jfree/skijagraphics2d", single);

            // Sync CPU / GPU:
            final Surface surface = canvas.getSurface();
            if (surface != null) {
                surface.flushAndSubmit(false); // full SYNC (GPU)
            }
            // image is ready

            final double elapsedTime = 1e-6d * (System.nanoTime() - startTime);
            System.out.println("drawTestOutput(SkijaGraphics2D) duration = " + elapsedTime + " ms.");

            if (saveFirst) {
                saveFirst = false;

                final io.github.humbleui.skija.Image image = surface.makeImageSnapshot();
                final Data pngData = image.encodeToData(EncodedImageFormat.PNG);
                final byte[] pngBytes = pngData.getBytes();
                try {
                    if (single) {
                        fileName += "-single.png";
                    } else {
                        fileName += ".png";
                    }
                    java.nio.file.Path path = java.nio.file.Path.of(fileName);
                    java.nio.file.Files.write(path, pngBytes);
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        } finally {
            g2.dispose();
        }
    }
}
