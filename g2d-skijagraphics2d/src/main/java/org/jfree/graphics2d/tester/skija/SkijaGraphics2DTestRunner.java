package org.jfree.graphics2d.tester.skija;

import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.EncodedImageFormat;
import io.github.humbleui.skija.Surface;
import org.jfree.graphics2d.Tester;
import org.jfree.skija.SkijaGraphics2D;

import java.io.IOException;
import static org.jfree.graphics2d.Tester.prepareTestOutput;

public class SkijaGraphics2DTestRunner {

    private final static int REPEATS = 100;

    /**
     * Run the tests with SkijaGraphics2D.
     *
     * @param fileName  the base filename.
     * @param single  run the current single test?
     */
    public static void testSkijaGraphics2D(String fileName, boolean single) {
        if (single) {
            fileName += "-single.png";
        } else {
            fileName += ".png";
        }
        // Prepare context:
        final Tester.TesterContext tc = prepareTestOutput(
                "JFree/" + SkijaGraphics2D.VERSION,
                "https://github.com/jfree/skijagraphics2d", single);

        final int width = Tester.getTestSheetWidth();
        final int height = Tester.getTestSheetHeight();

        final SkijaGraphics2D g2 = new SkijaGraphics2D(width, height);
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
                System.out.println("drawTestOutput(SkijaGraphics2D) duration = " + elapsedTime + " ms.");

                if (i == 0) {
                    final io.github.humbleui.skija.Image image = surface.makeImageSnapshot();
                    final Data pngData = image.encodeToData(EncodedImageFormat.PNG);
                    final byte[] pngBytes = pngData.getBytes();
                    try {
                        java.nio.file.Path path = java.nio.file.Path.of(fileName);
                        java.nio.file.Files.write(path, pngBytes);
                    } catch (IOException e) {
                        System.err.println(e);
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
     * @param args ignored
     */
    public static void main(String[] args) throws IOException {
        boolean single = false;
        testSkijaGraphics2D("SkijaGraphics2D", single);
        System.exit(0);
    }

}
