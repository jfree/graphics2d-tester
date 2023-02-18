/*
 * Graphics2D Tester
 *
 * (C)opyright 2023-today, by David Gilbert.
 */
package org.jfree.graphics2d.tester.skiko;

import org.jetbrains.skia.Data;
import org.jetbrains.skia.EncodedImageFormat;
import org.jetbrains.skia.Image;
import org.jetbrains.skia.Surface;
import org.jfree.graphics2d.Tester;
import org.jfree.skiko.SkikoGraphics2D;

import java.io.IOException;

import static org.jfree.graphics2d.Tester.prepareTestOutput;

public class SkikoGraphics2DTestRunner {

    private final static int REPEATS = 100;

    /**
     * Run the tests with SkikoGraphics2D.
     *
     * @param fileName  the base filename.
     * @param single  run the current single test?
     */
    public static void testSkikoGraphics2D(String fileName, boolean single) {
        if (single) {
            fileName += "-single.png";
        } else {
            fileName += ".png";
        }
        // Prepare context:
        final Tester.TesterContext tc = prepareTestOutput("JFree/SkikoGraphics2D (1.0.0)",
                "https://github.com/jfree/skijagraphics2d", single);

        final int width = Tester.getTestSheetWidth();
        final int height = Tester.getTestSheetHeight();

        final SkikoGraphics2D g2 = new SkikoGraphics2D(width, height);
        try {
            for (int i = 0; i < REPEATS; i++) {
                final long startTime = System.nanoTime();

                Tester.drawTestOutput(tc, g2);

                // Sync CPU / GPU:
                final Surface surface = g2.getSurface();
                if (surface != null) {
                    surface.flushAndSubmit(false); // full SYNC (GPU)
                }
                // image is ready

                final double elapsedTime = 1e-6d * (System.nanoTime() - startTime);
                System.out.println("drawTestOutput(SkikoGraphics2D) duration = " + elapsedTime + " ms.");

                if (i == 0) {
                    final Image image = surface.makeImageSnapshot();
                    final Data pngData = image.encodeToData(EncodedImageFormat.PNG, 0);
                    final byte[] pngBytes = pngData.getBytes();
                    try {
                        java.nio.file.Path path = java.nio.file.Path.of(fileName);
                        java.nio.file.Files.write(path, pngBytes);
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                }
            }
        } finally {
            g2.dispose();
        }
    }

    /**
     * Creates Java2D output that exercises many features of the API.
     *
     * @param args the command line arguments (ignored)
     */
    public static void main(String[] args) {
        boolean single = false;
        testSkikoGraphics2D("SkikoGraphics2D", single);
        System.exit(0);
    }
}