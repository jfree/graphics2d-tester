package org.jfree.graphics2d.tester.skija.jwm;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.EncodedImageFormat;
import io.github.humbleui.skija.Surface;
import java.io.IOException;
import org.jfree.graphics2d.Tester;
import org.jfree.skija.SkijaGraphics2D;

public class SkijaGraphics2DTestScene extends Scene {

    private SkijaGraphics2D g2 = null;
    private boolean saveFirst = true;

    @Override
    public void draw(Canvas canvas, int width, int height, float dpi, int xpos, int ypos) {
        String fileName = "SkijaGraphics2D";
        final boolean single = false;

        final long startTime = System.nanoTime();

        if ((g2 == null) || !g2.isCompatibleCanvas(canvas)) {
            if (g2 != null) {
                g2.dispose();
            }
            g2 = new SkijaGraphics2D(canvas);
        }
        /*
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, width, height);
         */
        Tester.drawTestOutput(g2, "SkijaGraphics2D 1.0.5", "https://github.com/jfree/skijagraphics2d", single);

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
    }
}
