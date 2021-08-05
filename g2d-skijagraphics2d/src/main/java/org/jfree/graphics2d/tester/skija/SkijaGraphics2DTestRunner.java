package org.jfree.graphics2d.tester.skija;

import org.jetbrains.skija.Data;
import org.jetbrains.skija.EncodedImageFormat;
import org.jfree.graphics2d.Tester;
import org.jfree.skija.SkijaGraphics2D;

import java.io.IOException;

public class SkijaGraphics2DTestRunner {

    /**
     * Run the tests with SkijaGraphics2D.
     *
     * @param fileName  the base filename.
     * @param single  run the current single test?
     */
    public static void testSkijaGraphics2D(String fileName, boolean single) {
        SkijaGraphics2D g2 = new SkijaGraphics2D((int) Tester.getTestSheetWidth(), (int) Tester.getTestSheetHeight());
        Tester.drawTestOutput(g2, "SkijaGraphics2d 1.0.2-SNAPSHOT", "https://github.com/jfree/skijagraphics2d", single);
        org.jetbrains.skija.Image image = g2.getSurface().makeImageSnapshot();
        Data pngData = image.encodeToData(EncodedImageFormat.PNG);
        byte [] pngBytes = pngData.getBytes();
        try {
            if (single) {
                fileName += "-single.png";
            } else {
                fileName += ".png";
            }
            java.nio.file.Path path = java.nio.file.Path.of(fileName);
            java.nio.file.Files.write(path, pngBytes);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Creates Java2D output that exercises many features of the API.
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        boolean single = false;
        testSkijaGraphics2D("skijagraphics2d", single);
        System.exit(0);
    }

}
